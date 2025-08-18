import request from '@/utils/request'

// 查询邮件标签列表
export function listTag(query) {
  return request({
    url: '/email/tag/list',
    method: 'get',
    params: query
  })
}

// 查询邮件标签详细
export function getTag(tagId) {
  return request({
    url: '/email/tag/' + tagId,
    method: 'get'
  })
}

// 新增邮件标签
export function addTag(data) {
  return request({
    url: '/email/tag',
    method: 'post',
    data: data
  })
}

// 修改邮件标签
export function updateTag(data) {
  return request({
    url: '/email/tag',
    method: 'put',
    data: data
  })
}

// 删除邮件标签
export function delTag(tagId) {
  return request({
    url: '/email/tag/' + tagId,
    method: 'delete'
  })
}

// 导出邮件标签
export function exportTag(query) {
  return request({
    url: '/email/tag/export',
    method: 'post',
    data: query
  })
}

// 获取所有标签列表
export function getAllTags() {
  return request({
    url: '/email/tag/all',
    method: 'get'
  })
}
