import request from '@/utils/request'

// 诊断所有邮箱账户的IMAP连接状态
export function diagnoseImapStatus() {
  return request({
    url: '/email/diagnostic/imap/status',
    method: 'get'
  })
}

// 测试单个邮箱账户的IMAP连接
export function testImapConnection(accountId) {
  return request({
    url: '/email/diagnostic/imap/test/' + accountId,
    method: 'post'
  })
}

// 获取邮箱配置建议
export function getConfigSuggestions() {
  return request({
    url: '/email/diagnostic/config/suggestions',
    method: 'get'
  })
}

// 手动触发回复检测
export function manualReplyScan(accountId) {
  return request({
    url: '/email/diagnostic/reply/scan/' + accountId,
    method: 'post'
  })
}

// 获取回复检测统计
export function getReplyStatistics() {
  return request({
    url: '/email/diagnostic/reply/statistics',
    method: 'get'
  })
}
