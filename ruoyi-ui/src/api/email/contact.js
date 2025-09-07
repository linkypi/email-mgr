import request from '@/utils/request'

// 查询收件人列表
export function getContactList(query) {
  return request({
    url: '/email/contact/list',
    method: 'get',
    params: query
  })
}

// 查询收件人详细
export function getContact(contactId) {
  return request({
    url: '/email/contact/' + contactId,
    method: 'get'
  })
}

// 新增收件人
export function addContact(data) {
  return request({
    url: '/email/contact',
    method: 'post',
    data: data
  })
}

// 修改收件人
export function updateContact(data) {
  return request({
    url: '/email/contact',
    method: 'put',
    data: data
  })
}

// 删除收件人
export function delContact(contactId) {
  return request({
    url: '/email/contact/' + contactId,
    method: 'delete'
  })
}

// 批量删除收件人
export function batchDeleteContacts(contactIds) {
  return request({
    url: '/email/contact/batchDelete',
    method: 'post',
    data: contactIds
  })
}

// 导出收件人
export function exportContact(query) {
  return request({
    url: '/email/contact/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

// 导入收件人数据
export function importContact(data) {
  return request({
    url: '/email/contact/importData',
    method: 'post',
    data: data
  })
}

// 下载导入模板
export function downloadTemplate() {
  return request({
    url: '/email/contact/importTemplate',
    method: 'post',
    responseType: 'blob'
  })
}

// 批量导入收件人
export function batchImportContacts(data) {
  return request({
    url: '/email/contact/batchImport',
    method: 'post',
    data: data
  })
}

// 根据邮箱地址查询收件人
export function getContactByEmail(email) {
  return request({
    url: '/email/contact/getByEmail/' + email,
    method: 'get'
  })
}

// 根据群组ID查询收件人列表
export function getContactsByGroup(groupId) {
  return request({
    url: '/email/contact/listByGroup/' + groupId,
    method: 'get'
  })
}

// 根据标签查询收件人列表
export function getContactsByTag(tag) {
  return request({
    url: '/email/contact/listByTag/' + tag,
    method: 'get'
  })
}

// 根据群组ID列表查询收件人
export function getContactsByGroupIds(groupIds) {
  return request({
    url: '/email/contact/listByGroupIds',
    method: 'post',
    data: groupIds
  })
}

// 根据标签ID列表查询收件人
export function getContactsByTagIds(tagIds) {
  return request({
    url: '/email/contact/listByTagIds',
    method: 'post',
    data: tagIds
  })
}

// 根据收件人ID列表查询收件人
export function getContactsByIds(contactIds) {
  return request({
    url: '/email/contact/listByIds',
    method: 'post',
    data: contactIds
  })
}

// 获取所有收件人列表（用于下拉选择）
export function getAllContacts() {
  return request({
    url: '/email/contact/all',
    method: 'get'
  })
}

// 搜索收件人（支持多条件搜索）
export function searchContacts(data) {
  return request({
    url: '/email/contact/search',
    method: 'post',
    data: data
  })
}

// 获取收件人统计信息
export function getContactStatistics() {
  return request({
    url: '/email/contact/statistics',
    method: 'get'
  })
}

// 查询回复率最高的收件人
export function getTopReplyRateContacts(limit) {
  return request({
    url: '/email/contact/topReplyRate/' + limit,
    method: 'get'
  })
}

// 更新收件人统计信息
export function updateContactStatistics(contactId) {
  return request({
    url: '/email/contact/updateStatistics/' + contactId,
    method: 'put'
  })
}

// 批量更新收件人统计信息
export function batchUpdateContactStatistics(contactIds) {
  return request({
    url: '/email/contact/batchUpdateStatistics',
    method: 'put',
    data: contactIds
  })
}

// 验证邮箱地址是否已存在
export function validateEmail(email) {
  return request({
    url: '/email/contact/validateEmail/' + email,
    method: 'get'
  })
}

// 恢复已删除的收件人
export function restoreContact(contactId) {
  return request({
    url: '/email/contact/restore/' + contactId,
    method: 'put'
  })
}

// 批量恢复已删除的收件人
export function batchRestoreContacts(contactIds) {
  return request({
    url: '/email/contact/batchRestore',
    method: 'post',
    data: contactIds
  })
}