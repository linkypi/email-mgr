import request from '@/utils/request'

// 获取所有用户角色类型
export function getUserRoles() {
  return request({
    url: '/system/userRole/roles',
    method: 'get'
  })
}

// 设置用户角色
export function setUserRole(data) {
  return request({
    url: '/system/userRole/setRole',
    method: 'put',
    data: data
  })
}
