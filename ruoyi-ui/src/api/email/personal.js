import request from '@/utils/request'

// 查询个人邮件列表
export function listEmailPersonal(query) {
  return request({
    url: '/email/personal/list',
    method: 'get',
    params: query
  })
}

// 查询收件箱列表
export function listInbox(query) {
  return request({
    url: '/email/personal/inbox/list',
    method: 'get',
    params: query
  })
}

// 查询发件箱列表
export function listSent(query) {
  return request({
    url: '/email/personal/sent/list',
    method: 'get',
    params: query
  })
}

// 查询星标邮件列表
export function listStarred(query) {
  return request({
    url: '/email/personal/starred/list',
    method: 'get',
    params: query
  })
}

// 查询已删除邮件列表
export function listDeleted(query) {
  return request({
    url: '/email/personal/deleted/list',
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

// 标记邮件为已读
export function markAsRead(emailId) {
  return request({
    url: '/email/personal/read/' + emailId,
    method: 'put'
  })
}

// 标记邮件为星标
export function markAsStarred(emailId) {
  return request({
    url: '/email/personal/star/' + emailId,
    method: 'put'
  })
}

// 标记邮件为重要
export function markAsImportant(emailId) {
  return request({
    url: '/email/personal/important/' + emailId,
    method: 'put'
  })
}

// 恢复已删除邮件
export function restoreEmail(emailId) {
  return request({
    url: '/email/personal/restore/' + emailId,
    method: 'put'
  })
}

// 彻底删除邮件
export function deletePermanently(emailId) {
  return request({
    url: '/email/personal/permanent/' + emailId,
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

// 获取收件箱未读数量
export function getInboxUnreadCount() {
  return request({
    url: '/email/personal/inbox/unread/count',
    method: 'get'
  })
}

// 获取发件箱未读数量
export function getSentUnreadCount() {
  return request({
    url: '/email/personal/sent/unread/count',
    method: 'get'
  })
}

// 获取星标邮件未读数量
export function getStarredUnreadCount() {
  return request({
    url: '/email/personal/starred/unread/count',
    method: 'get'
  })
}

// 获取已删除邮件未读数量
export function getDeletedUnreadCount() {
  return request({
    url: '/email/personal/deleted/unread/count',
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
