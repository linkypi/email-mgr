import request from '@/utils/request'

// 查询邮件联系人列表
export function listContact(query) {
  return request({
    url: '/email/contact/list',
    method: 'get',
    params: query
  })
}

// 查询邮件联系人详细
export function getContact(contactId) {
  return request({
    url: '/email/contact/' + contactId,
    method: 'get'
  })
}

// 新增邮件联系人
export function addContact(data) {
  return request({
    url: '/email/contact',
    method: 'post',
    data: data
  })
}

// 修改邮件联系人
export function updateContact(data) {
  return request({
    url: '/email/contact',
    method: 'put',
    data: data
  })
}

// 删除邮件联系人
export function delContact(contactId) {
  return request({
    url: '/email/contact/' + contactId,
    method: 'delete'
  })
}

// 获取所有联系人（用于下拉选择）
export function getAllContacts() {
  return request({
    url: '/email/contact/all',
    method: 'get'
  })
}

// 导入邮件联系人
export function importContact(data) {
  return request({
    url: '/email/contact/import',
    method: 'post',
    data: data
  })
}

// 导出邮件联系人
export function exportContact(query) {
  return request({
    url: '/email/contact/export',
    method: 'post',
    data: query
  })
}

// 下载导入模板
export function importTemplate() {
  return request({
    url: '/email/contact/importTemplate',
    method: 'get'
  })
}

// 根据群组查询联系人
export function getContactsByGroup(groupId) {
  return request({
    url: '/email/contact/listByGroup/' + groupId,
    method: 'get'
  })
}

// 根据标签查询联系人
export function getContactsByTag(tag) {
  return request({
    url: '/email/contact/listByTag/' + tag,
    method: 'get'
  })
}

// 查询回复率最高的联系人
export function getTopReplyRateContacts(limit) {
  return request({
    url: '/email/contact/topReplyRate/' + limit,
    method: 'get'
  })
}
