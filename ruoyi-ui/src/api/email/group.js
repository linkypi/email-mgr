import request from '@/utils/request'

// 查询联系人群组列表
export function listGroup(query) {
  return request({
    url: '/email/group/list',
    method: 'get',
    params: query
  })
}

// 查询联系人群组详细
export function getGroup(groupId) {
  return request({
    url: '/email/group/' + groupId,
    method: 'get'
  })
}

// 新增联系人群组
export function addGroup(data) {
  return request({
    url: '/email/group',
    method: 'post',
    data: data
  })
}

// 修改联系人群组
export function updateGroup(data) {
  return request({
    url: '/email/group',
    method: 'put',
    data: data
  })
}

// 删除联系人群组
export function delGroup(groupId) {
  return request({
    url: '/email/group/' + groupId,
    method: 'delete'
  })
}

// 导出联系人群组
export function exportGroup(query) {
  return request({
    url: '/email/group/export',
    method: 'post',
    data: query
  })
}

// 获取所有群组列表
export function getAllGroups() {
  return request({
    url: '/email/group/all',
    method: 'get'
  })
}
