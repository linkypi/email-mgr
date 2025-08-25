import request from '@/utils/request'

// 查询邮件跟踪记录列表
export function listEmailTrackRecord(query) {
  return request({
    url: '/email/track-record/list',
    method: 'get',
    params: query
  })
}

// 查询邮件跟踪记录详细
export function getEmailTrackRecord(id) {
  return request({
    url: '/email/track-record/' + id,
    method: 'get'
  })
}

// 根据Message-ID查询邮件跟踪记录
export function getEmailTrackRecordByMessageId(messageId) {
  return request({
    url: '/email/track-record/message/' + messageId,
    method: 'get'
  })
}

// 根据任务ID查询邮件跟踪记录列表
export function getEmailTrackRecordByTaskId(taskId) {
  return request({
    url: '/email/track-record/task/' + taskId,
    method: 'get'
  })
}

// 根据邮箱账号ID查询邮件跟踪记录列表
export function getEmailTrackRecordByAccountId(accountId) {
  return request({
    url: '/email/track-record/account/' + accountId,
    method: 'get'
  })
}

// 根据状态查询邮件跟踪记录列表
export function getEmailTrackRecordByStatus(status) {
  return request({
    url: '/email/track-record/status/' + status,
    method: 'get'
  })
}

// 新增邮件跟踪记录
export function addEmailTrackRecord(data) {
  return request({
    url: '/email/track-record',
    method: 'post',
    data: data
  })
}

// 修改邮件跟踪记录
export function updateEmailTrackRecord(data) {
  return request({
    url: '/email/track-record',
    method: 'put',
    data: data
  })
}

// 删除邮件跟踪记录
export function delEmailTrackRecord(id) {
  return request({
    url: '/email/track-record/' + id,
    method: 'delete'
  })
}

// 根据Message-ID删除邮件跟踪记录
export function delEmailTrackRecordByMessageId(messageId) {
  return request({
    url: '/email/track-record/message/' + messageId,
    method: 'delete'
  })
}

// 根据任务ID删除邮件跟踪记录
export function delEmailTrackRecordByTaskId(taskId) {
  return request({
    url: '/email/track-record/task/' + taskId,
    method: 'delete'
  })
}

// 获取任务邮件状态统计
export function getTaskStats(taskId) {
  return request({
    url: '/email/track-record/stats/task/' + taskId,
    method: 'get'
  })
}

// 获取邮箱账号邮件状态统计
export function getAccountStats(accountId) {
  return request({
    url: '/email/track-record/stats/account/' + accountId,
    method: 'get'
  })
}

// 更新邮件状态
export function updateEmailStatus(messageId, status) {
  return request({
    url: '/email/track-record/status/' + messageId + '/' + status,
    method: 'put'
  })
}

// 导出邮件跟踪记录
export function exportEmailTrackRecord(query) {
  return request({
    url: '/email/track-record/export',
    method: 'post',
    data: query
  })
}

