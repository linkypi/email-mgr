import request from '@/utils/request'

// 查询销售数据列表
export function listSales(query) {
  return request({
    url: '/email/sales/list',
    method: 'get',
    params: query
  })
}

// 查询销售数据详细
export function getSales(salesId) {
  return request({
    url: '/email/sales/' + salesId,
    method: 'get'
  })
}

// 新增销售数据
export function addSales(data) {
  return request({
    url: '/email/sales',
    method: 'post',
    data: data
  })
}

// 修改销售数据
export function updateSales(data) {
  return request({
    url: '/email/sales',
    method: 'put',
    data: data
  })
}

// 删除销售数据
export function delSales(salesId) {
  return request({
    url: '/email/sales/' + salesId,
    method: 'delete'
  })
}

// 导出销售数据
export function exportSales(query) {
  return request({
    url: '/email/sales/export',
    method: 'post',
    data: query
  })
}

// 根据联系人ID查询销售数据
export function getSalesByContactId(contactId) {
  return request({
    url: '/email/sales/contact/' + contactId,
    method: 'get'
  })
}

// 批量导入销售数据
export function importSales(data) {
  return request({
    url: '/email/sales/importData',
    method: 'post',
    data: data
  })
}
