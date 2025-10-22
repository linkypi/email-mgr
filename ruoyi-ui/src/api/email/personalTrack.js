import request from '@/utils/request'

// 查询发件箱列表（基于email_track_record）
export function getSentList(query) {
  return request({
    url: '/email/personal/sent',
    method: 'get',
    params: query
  })
}

// 查询收件箱列表（基于email_track_record）
export function getInboxList(query) {
  return request({
    url: '/email/personal/inbox',
    method: 'get',
    params: query
  })
}

// 查询星标邮件列表（基于email_track_record）
export function getStarredList(query) {
  return request({
    url: '/email/personal/starred',
    method: 'get',
    params: query
  })
}

// 查询已删除邮件列表（基于email_track_record）
export function getDeletedList(query) {
  return request({
    url: '/email/personal/deleted',
    method: 'get',
    params: query
  })
}

// 根据ID查询邮件跟踪记录
export function getEmailTrackRecord(id) {
  return request({
    url: '/email/trackRecord/' + id,
    method: 'get'
  })
}

// 标记邮件为星标
export function markAsStarred(ids) {
  return request({
    url: '/email/personal/starred',
    method: 'put',
    data: ids
  })
}

// 取消星标
export function unmarkAsStarred(ids) {
  return request({
    url: '/email/personal/unstarred',
    method: 'put',
    data: ids
  })
}

// 标记邮件为已读
export function markAsRead(ids) {
  return request({
    url: '/email/personal/read',
    method: 'put',
    data: ids
  })
}

// 标记邮件为未读
export function markAsUnread(ids) {
  return request({
    url: '/email/personal/unread',
    method: 'put',
    data: ids
  })
}

// 标记邮件为重要
export function markAsImportant(ids) {
  return request({
    url: '/email/personal/important',
    method: 'put',
    data: ids
  })
}

// 取消重要标记
export function unmarkAsImportant(ids) {
  return request({
    url: '/email/personal/unimportant',
    method: 'put',
    data: ids
  })
}

// 移动到已删除
export function moveToDeleted(ids) {
  return request({
    url: '/email/personal/delete',
    method: 'put',
    data: ids
  })
}

// 从已删除恢复
export function restoreFromDeleted(ids) {
  return request({
    url: '/email/personal/restore',
    method: 'put',
    data: ids
  })
}

// 彻底删除邮件
export function deletePermanently(ids) {
  return request({
    url: '/email/personal/' + ids,
    method: 'delete'
  })
}

// 获取发件箱未读数量
export function getSentUnreadCount() {
  return request({
    url: '/email/personal/sent/unread/count/track',
    method: 'get'
  })
}

// 获取收件箱未读数量
export function getInboxUnreadCount() {
  return request({
    url: '/email/personal/inbox/unread/count/track',
    method: 'get'
  })
}

// 获取星标邮件数量
export function getStarredCount() {
  return request({
    url: '/email/personal/starred/count/track',
    method: 'get'
  })
}

// 获取已删除邮件数量
export function getDeletedCount() {
  return request({
    url: '/email/personal/deleted/count/track',
    method: 'get'
  })
}

// 获取个人邮件统计信息
export function getEmailStatistics() {
  return request({
    url: '/email/personal/statistics/track',
    method: 'get'
  })
}

// 发送回复邮件
export function sendReply(data) {
  return request({
    url: '/email/personal/reply',
    method: 'post',
    data: data
  })
}

