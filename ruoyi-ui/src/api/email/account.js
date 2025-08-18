import request from '@/utils/request'

// 查询邮箱账号列表
export function listAccount(query) {
  return request({
    url: '/email/account/list',
    method: 'get',
    params: query
  })
}

// 查询邮箱账号详细
export function getAccount(accountId) {
  return request({
    url: '/email/account/' + accountId,
    method: 'get'
  })
}

// 新增邮箱账号
export function addAccount(data) {
  return request({
    url: '/email/account',
    method: 'post',
    data: data
  })
}

// 修改邮箱账号
export function updateAccount(data) {
  return request({
    url: '/email/account',
    method: 'put',
    data: data
  })
}

// 删除邮箱账号
export function delAccount(accountId) {
  return request({
    url: '/email/account/' + accountId,
    method: 'delete'
  })
}

// 导出邮箱账号
export function exportAccount(query) {
  return request({
    url: '/email/account/export',
    method: 'get',
    params: query
  })
}

// 测试邮箱账号连接
export function testAccount(data) {
  return request({
    url: '/email/account/test',
    method: 'post',
    data: data
  })
}

// 获取可用邮箱账号列表
export function getAvailableAccounts() {
  return request({
    url: '/email/account/available',
    method: 'get'
  })
}
