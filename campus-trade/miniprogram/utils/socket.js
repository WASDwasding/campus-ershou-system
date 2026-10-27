const { wsUrl } = require('./config')
const session = require('./session')

let socketOpen = false
let socketTask = null
let pingTimer = null
let retryTimer = null
let attempt = 0
let stopped = false
const listeners = []

function onEvent(fn) {
  listeners.push(fn)
  return function off() {
    const i = listeners.indexOf(fn)
    if (i >= 0) listeners.splice(i, 1)
  }
}

function emit(msg) {
  listeners.forEach((fn) => {
    try {
      fn(msg)
    } catch (e) {}
  })
}

function clearPing() {
  if (pingTimer) {
    clearInterval(pingTimer)
    pingTimer = null
  }
}

function scheduleRetry() {
  if (stopped || !session.token()) return
  const steps = [1000, 2000, 4000, 8000, 15000]
  const wait = steps[Math.min(attempt, steps.length - 1)]
  attempt += 1
  if (retryTimer) clearTimeout(retryTimer)
  retryTimer = setTimeout(connect, wait)
}

function connect() {
  if (!session.token() || socketOpen || stopped) return
  if (socketTask) return
  const task = wx.connectSocket({
    url: wsUrl,
    header: { Authorization: 'Bearer ' + session.token() }
  })
  socketTask = task
  task.onOpen(() => {
    socketOpen = true
    attempt = 0
    clearPing()
    pingTimer = setInterval(() => {
      if (socketOpen && socketTask) socketTask.send({ data: JSON.stringify({ type: 'PING' }) })
    }, 30000)
  })
  task.onMessage((res) => {
    let msg = null
    try {
      msg = JSON.parse(res.data)
    } catch (e) {
      return
    }
    emit(msg)
  })
  task.onClose((res) => {
    socketOpen = false
    socketTask = null
    clearPing()
    if (res && res.code === 4403) {
      stopped = true
      return
    }
    if (res && res.code === 4401) {
      stopped = true
      session.clear()
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }
    scheduleRetry()
  })
  task.onError(() => {
    socketOpen = false
    socketTask = null
    clearPing()
    scheduleRetry()
  })
}

function close() {
  stopped = true
  clearPing()
  if (retryTimer) clearTimeout(retryTimer)
  if (socketTask) {
    socketTask.close({})
    socketTask = null
  }
  socketOpen = false
}

function resume() {
  stopped = false
  connect()
}

module.exports = { connect: resume, close, onEvent }
