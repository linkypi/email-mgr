import request from '@/utils/request'

// 查询IMAP监听列表
export function listImapListeners(query) {
  return request({
    url: '/email/imap/list',
    method: 'get',
    params: query
  })
}

// 启动IMAP监听
export function startImapListener(accountId) {
  return request({
    url: '/email/imap/start/' + accountId,
    method: 'post'
  })
}

// 停止IMAP监听
export function stopImapListener(accountId) {
  return request({
    url: '/email/imap/stop/' + accountId,
    method: 'post'
  })
}

// 重启IMAP监听
export function restartImapListener(accountId) {
  return request({
    url: '/email/imap/restart/' + accountId,
    method: 'post'
  })
}

// 同步IMAP邮件
export function syncImapListener(accountId) {
  return request({
    url: '/email/imap/sync/' + accountId,
    method: 'post'
  })
}
