import request from '@/utils/request'

// 查询发件人信息列表
export function listSender(query) {
  return request({
    url: '/email/sender/list',
    method: 'get',
    params: query
  })
}

// 查询发件人信息详细
export function getSender(senderId) {
  return request({
    url: '/email/sender/' + senderId,
    method: 'get'
  })
}

// 查询发件人信息及其关联的邮箱账号
export function getSenderWithAccounts(senderId) {
  return request({
    url: '/email/sender/' + senderId + '/accounts',
    method: 'get'
  })
}

// 新增发件人信息
export function addSender(data) {
  return request({
    url: '/email/sender',
    method: 'post',
    data: data
  })
}

// 修改发件人信息
export function updateSender(data) {
  return request({
    url: '/email/sender',
    method: 'put',
    data: data
  })
}

// 删除发件人信息
export function delSender(senderId) {
  return request({
    url: '/email/sender/' + senderId,
    method: 'delete'
  })
}

// 批量更新发件人状态
export function updateSenderStatus(data) {
  return request({
    url: '/email/sender/status',
    method: 'put',
    data: data
  })
}

// 获取发件人选项列表
export function getSenderOptions() {
  return request({
    url: '/email/sender/options',
    method: 'get'
  })
}

// 导出发件人信息
export function exportSender(query) {
  return request({
    url: '/email/sender/export',
    method: 'post',
    data: query
  })
}

