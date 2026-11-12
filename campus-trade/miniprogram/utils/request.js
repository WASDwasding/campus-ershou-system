const { baseUrl } = require('./config')
const session = require('./session')
const adminSession = require('./admin-session')

const TOAST_MAX = 36

function showError(err) {
  if (!err) {
    wx.showToast({ title: '请求失败', icon: 'none' })
    return
  }
  if (err.code === 'NOT_VERIFIED') {
    wx.showModal({
      title: '需要校园认证',
      content: '认证通过后才能发布、下单、支付、确认面交和申诉',
      confirmText: '去认证',
      success(r) {
        if (r.confirm) wx.navigateTo({ url: '/pages/verify/verify' })
      }
    })
    return
  }
  if (err.code === 'PUBLISH_BANNED') {
    wx.showModal({
      title: '不能再发布',
      content: '该账号因疑似欺诈被禁止发布商品，仍可以登录和购买。',
      showCancel: false
    })
    return
  }
  if (err.code === 'PASSWORD_CHANGE_REQUIRED') {
    wx.showModal({
      title: '请先修改密码',
      content: '初始密码必须先修改，之后才能处理审核、申诉和举报。',
      showCancel: false,
      success() {
        wx.redirectTo({ url: '/pages/admin/password/password' })
      }
    })
    return
  }
  if (err.code === 'USER_BANNED') {
    const until = err.data && err.data.until
    wx.showModal({
      title: '账号已封禁',
      content: ((err.data && err.data.reason) || err.message || '已封禁') + (until ? '\n至 ' + until : ''),
      showCancel: false
    })
    return
  }
  const fields = err.data && err.data.fieldErrors
  const list = Array.isArray(fields) ? fields.map((f) => f && f.message).filter(Boolean) : []
  const msg = err.message || '请求失败'
  if (list.length || Array.from(msg).length > TOAST_MAX) {
    wx.showModal({
      title: list.length ? '请检查填写内容' : '提示',
      content: list.length ? msg + '\n' + list.join('\n') : msg,
      showCancel: false
    })
    return
  }
  wx.showToast({ title: msg, icon: 'none' })
}

// 服务端已经给出确定结论（不含 409 处理中和 5xx），同一幂等键不必再重试
function isBusinessError(err) {
  if (!err || !err.statusCode) return false
  return err.statusCode < 500 && err.code !== 'CONFLICT'
}

function raw(options) {
  const method = options.method || 'GET'
  const header = Object.assign({ 'Content-Type': 'application/json' }, options.header || {})
  if (options.auth === 'admin') {
    if (adminSession.token()) header.Authorization = 'Bearer ' + adminSession.token()
  } else if (options.auth !== false && session.token()) {
    header.Authorization = 'Bearer ' + session.token()
  }
  if (options.idempotencyKey) header['Idempotency-Key'] = options.idempotencyKey
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + options.url,
      method,
      data: options.data,
      header,
      timeout: 20000,
      success(res) {
        if (options.rawBody) {
          resolve(res)
          return
        }
        const body = res.data || {}
        const ok = res.statusCode === 200 && body.code === 'OK'
        if (ok) {
          resolve(body.data)
          return
        }
        const err = {
          statusCode: res.statusCode,
          code: body.code || 'BAD_REQUEST',
          message: body.message || '请求失败',
          data: body.data || null,
          traceId: body.traceId || ''
        }
        if (res.statusCode === 401 && options.auth === 'admin') {
          adminSession.clear()
          wx.reLaunch({ url: '/pages/admin/login/login' })
          reject(err)
          return
        }
        if (res.statusCode === 401 && options.auth !== false) {
          session.clear()
          const socket = require('./socket')
          socket.close()
          wx.reLaunch({ url: '/pages/login/login' })
          reject(err)
          return
        }
        reject(err)
      },
      fail() {
        reject({ code: 'UPSTREAM_UNAVAILABLE', message: '网络不可用' })
      }
    })
  })
}

function upload(filePath, purpose) {
  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: baseUrl + '/api/files',
      filePath,
      name: 'file',
      formData: { purpose },
      header: { Authorization: 'Bearer ' + session.token() },
      success(res) {
        let body = {}
        try {
          body = JSON.parse(res.data || '{}')
        } catch (e) {
          reject({ code: 'BAD_REQUEST', message: '上传响应无法解析' })
          return
        }
        if (res.statusCode === 200 && body.code === 'OK') resolve(body.data)
        else reject({ statusCode: res.statusCode, code: body.code, message: body.message || '上传失败', data: body.data })
      },
      fail() {
        reject({ code: 'UPSTREAM_UNAVAILABLE', message: '上传失败' })
      }
    })
  })
}

module.exports = { raw, upload, showError, isBusinessError }
