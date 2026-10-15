/**
 * Blocks external contact info (WeChat / phone / QQ / email patterns)
 * from leaving the in-app messaging channel.
 */

const PATTERNS: { name: string; regex: RegExp }[] = [
  { name: 'phone', regex: /(?:\+?\d{1,3}[-.\s]?)?(?:\(?\d{2,4}\)?[-.\s]?)?\d{3,4}[-.\s]?\d{3,4}/g },
  { name: 'wechat', regex: /(?:wechat|微信|v信|vx|wx)\s*[:：]?\s*[a-zA-Z][\w-]{5,19}/gi },
  { name: 'wechat_id', regex: /(?<![a-zA-Z0-9])[a-zA-Z][\w-]{5,19}(?![a-zA-Z0-9])/g },
  { name: 'qq', regex: /(?:qq|QQ)\s*[:：]?\s*\d{5,12}/g },
  { name: 'email', regex: /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g },
];

const CONTACT_KEYWORDS = /微信|wechat|\bvx\b|\bwx\b|加我|私聊|手机号|电话|call me|whatsapp|telegram/i;

export function sanitizeOutbound(text: string): { text: string; blocked: boolean } {
  let blocked = false;
  let result = text;

  if (CONTACT_KEYWORDS.test(text)) {
    blocked = true;
  }

  for (const { regex } of PATTERNS) {
    regex.lastIndex = 0;
    if (regex.test(text)) {
      blocked = true;
      regex.lastIndex = 0;
      result = result.replace(regex, '[blocked]');
    }
  }

  if (blocked && result === text) {
    return {
      text: '⚠️ 外部联系方式不可以哦，请留在 Campus Trade 里聊～',
      blocked: true,
    };
  }

  if (blocked) {
    return {
      text: result.includes('[blocked]')
        ? result.replace(/\[blocked\]/g, '••••')
        : '⚠️ 外部联系方式不可以哦，请留在 Campus Trade 里聊～',
      blocked: true,
    };
  }

  return { text: result, blocked: false };
}
