import request from '@/utils/request'

// 查询邮件群组列表
export function listGroup(query) {
  return request({
    url: '/email/group/list',
    method: 'get',
    params: query
  })
}

// 查询邮件群组详细
export function getGroup(groupId) {
  return request({
    url: '/email/group/' + groupId,
    method: 'get'
  })
}

// 新增邮件群组
export function addGroup(data) {
  return request({
    url: '/email/group',
    method: 'post',
    data: data
  })
}

// 修改邮件群组
export function updateGroup(data) {
  return request({
    url: '/email/group',
    method: 'put',
    data: data
  })
}

// 删除邮件群组
export function delGroup(groupId) {
  return request({
    url: '/email/group/' + groupId,
    method: 'delete'
  })
}

// 获取所有群组（用于下拉选择）
export function getAllGroups() {
  return request({
    url: '/email/group/all',
    method: 'get'
  })
}
