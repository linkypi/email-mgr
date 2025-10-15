import request from '@/utils/request'

// 查询个人邮件列表
export function listEmailPersonal(query) {
  return request({
    url: '/email/personal/list',
    method: 'get',
    params: query
  })
}

// 查询收件箱列表（基于email_track_record）
export function listInbox(query) {
  return request({
    url: '/email/personal/inbox',
    method: 'get',
    params: query
  })
}

// 查询发件箱列表（基于email_track_record）
export function listSent(query) {
  return request({
    url: '/email/personal/sent',
    method: 'get',
    params: query
  })
}

// 查询星标邮件列表（基于email_track_record）
export function listStarred(query) {
  return request({
    url: '/email/personal/starred',
    method: 'get',
    params: query
  })
}

// 查询已删除邮件列表（基于email_track_record）
export function listDeleted(query) {
  return request({
    url: '/email/personal/deleted',
    method: 'get',
    params: query
  })
}

// 查询个人邮件详细
export function getEmail(emailId) {
  return request({
    url: '/email/personal/' + emailId,
    method: 'get'
  })
}

// 新增个人邮件
export function addEmail(data) {
  return request({
    url: '/email/personal',
    method: 'post',
    data: data
  })
}

// 修改个人邮件
export function updateEmail(data) {
  return request({
    url: '/email/personal',
    method: 'put',
    data: data
  })
}

// 删除个人邮件
export function delEmail(emailId) {
  return request({
    url: '/email/personal/' + emailId,
    method: 'delete'
  })
}

// 标记邮件为已读（基于email_track_record）
export function markAsRead(ids) {
  return request({
    url: '/email/personal/read',
    method: 'put',
    data: ids
  })
}

// 标记邮件为星标（基于email_track_record）
export function markAsStarred(ids) {
  return request({
    url: '/email/personal/starred',
    method: 'put',
    data: ids
  })
}

// 取消星标（基于email_track_record）
export function unmarkAsStarred(ids) {
  return request({
    url: '/email/personal/unstarred',
    method: 'put',
    data: ids
  })
}

// 标记邮件为重要（基于email_track_record）
export function markAsImportant(ids) {
  return request({
    url: '/email/personal/important',
    method: 'put',
    data: ids
  })
}

// 移动到已删除（基于email_track_record）
export function moveToDeleted(ids) {
  return request({
    url: '/email/personal/delete',
    method: 'put',
    data: ids
  })
}

// 从已删除恢复（基于email_track_record）
export function restoreEmail(ids) {
  return request({
    url: '/email/personal/restore',
    method: 'put',
    data: ids
  })
}

// 彻底删除邮件（基于email_track_record）
export function deletePermanently(ids) {
  return request({
    url: '/email/personal/' + ids,
    method: 'delete'
  })
}

// 获取未读邮件数量
export function getUnreadCount() {
  return request({
    url: '/email/personal/unread/count',
    method: 'get'
  })
}

// 获取收件箱未读数量（基于email_track_record）
export function getInboxUnreadCount() {
  return request({
    url: '/email/personal/inbox/unread/count/track',
    method: 'get'
  })
}

// 获取发件箱未读数量（基于email_track_record）
export function getSentUnreadCount() {
  return request({
    url: '/email/personal/sent/unread/count/track',
    method: 'get'
  })
}

// 获取发件箱总数量（基于email_track_record）
export function getSentTotalCount() {
  return request({
    url: '/email/personal/sent/total/count/track',
    method: 'get'
  })
}

// 获取星标邮件数量（基于email_track_record）
export function getStarredCount() {
  return request({
    url: '/email/personal/starred/count/track',
    method: 'get'
  })
}

// 获取星标邮件未读数量（基于email_track_record）
export function getStarredUnreadCount() {
  return request({
    url: '/email/personal/starred/unread/count',
    method: 'get'
  })
}

// 获取已删除邮件数量（基于email_track_record）
export function getDeletedCount() {
  return request({
    url: '/email/personal/deleted/count/track',
    method: 'get'
  })
}

// 获取已删除邮件未读数量（基于email_track_record）
export function getDeletedUnreadCount() {
  return request({
    url: '/email/personal/deleted/unread/count',
    method: 'get'
  })
}

// 获取个人邮件统计信息（基于email_track_record）
export function getEmailStatistics() {
  return request({
    url: '/email/personal/statistics/track',
    method: 'get'
  })
}

// 发送邮件
export function sendEmail(data) {
  return request({
    url: '/email/personal/send',
    method: 'post',
    data: data
  })
}

// 查询草稿箱列表
export function listDrafts(query) {
  return request({
    url: '/email/personal/drafts/list',
    method: 'get',
    params: query
  })
}

// 保存草稿
export function saveDraft(data) {
  return request({
    url: '/email/personal/draft',
    method: 'post',
    data: data
  })
}

// 更新草稿
export function updateDraft(data) {
  return request({
    url: '/email/personal/draft',
    method: 'put',
    data: data
  })
}

// 删除草稿
export function delDraft(draftId) {
  return request({
    url: '/email/personal/draft/' + draftId,
    method: 'delete'
  })
}
