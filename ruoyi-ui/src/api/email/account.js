import request from '@/utils/request'

// 查询邮件账号列表
export function listAccount(query) {
  return request({
    url: '/email/account/list',
    method: 'get',
    params: query
  })
}

// 查询邮件账号详细
export function getAccount(accountId) {
  return request({
    url: '/email/account/' + accountId,
    method: 'get'
  })
}

// 查询邮件账号详细（包含密码，用于编辑）
export function getAccountForEdit(accountId) {
  return request({
    url: '/email/account/' + accountId + '/edit',
    method: 'get'
  })
}

// 新增邮件账号
export function addAccount(data) {
  return request({
    url: '/email/account',
    method: 'post',
    data: data
  })
}

// 修改邮件账号
export function updateAccount(data) {
  return request({
    url: '/email/account',
    method: 'put',
    data: data
  })
}

// 删除邮件账号
export function delAccount(accountId) {
  return request({
    url: '/email/account/' + accountId,
    method: 'delete'
  })
}

// 获取所有账号（用于下拉选择）
export function getAllAccounts() {
  return request({
    url: '/email/account/all',
    method: 'get'
  })
}

// 批量更新账号状态
export function batchUpdateAccountStatus(data) {
  return request({
    url: '/email/account/batch/status',
    method: 'put',
    data: data
  })
}
