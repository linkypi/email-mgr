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

// 导出邮件模板
export function exportTemplate(query) {
  return request({
    url: '/email/template/export',
    method: 'post',
    data: query
  })
}

// 预览邮件模板
export function previewTemplate(templateId) {
  return request({
    url: '/email/template/preview/' + templateId,
    method: 'get'
  })
}

// 获取所有模板列表
export function getAllTemplates() {
  return request({
    url: '/email/template/all',
    method: 'get'
  })
}
