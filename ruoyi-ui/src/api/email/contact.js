import request from '@/utils/request'

// 查询联系人列表
export function listContact(query) {
  return request({
    url: '/email/contact/list',
    method: 'get',
    params: query
  })
}

// 查询联系人详细
export function getContact(contactId) {
  return request({
    url: '/email/contact/' + contactId,
    method: 'get'
  })
}

// 新增联系人
export function addContact(data) {
  return request({
    url: '/email/contact',
    method: 'post',
    data: data
  })
}

// 修改联系人
export function updateContact(data) {
  return request({
    url: '/email/contact',
    method: 'put',
    data: data
  })
}

// 删除联系人
export function delContact(contactId) {
  return request({
    url: '/email/contact/' + contactId,
    method: 'delete'
  })
}

// 导出联系人
export function exportContact(query) {
  return request({
    url: '/email/contact/export',
    method: 'post',
    data: query
  })
}

// 批量导入联系人
export function importContact(data) {
  return request({
    url: '/email/contact/importData',
    method: 'post',
    data: data
  })
}
