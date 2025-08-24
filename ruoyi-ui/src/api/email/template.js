import request from '@/utils/request'

// 查询邮件模板列表
export function listTemplate(query) {
  return request({
    url: '/email/template/list',
    method: 'get',
    params: query
  })
}

// 查询邮件模板详细
export function getTemplate(templateId) {
  return request({
    url: '/email/template/' + templateId,
    method: 'get'
  })
}

// 新增邮件模板
export function addTemplate(data) {
  return request({
    url: '/email/template',
    method: 'post',
    data: data
  })
}

// 修改邮件模板
export function updateTemplate(data) {
  return request({
    url: '/email/template',
    method: 'put',
    data: data
  })
}

// 删除邮件模板
export function delTemplate(templateId) {
  return request({
    url: '/email/template/' + templateId,
    method: 'delete'
  })
}

// 获取所有模板（用于下拉选择）
export function getAllTemplates() {
  return request({
    url: '/email/template/all',
    method: 'get'
  })
}

// 预览模板
export function previewTemplate(data) {
  return request({
    url: '/email/template/preview',
    method: 'post',
    data: data
  })
}
