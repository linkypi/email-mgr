import request from '@/utils/request'

// 查询销售数据列表
export function getSalesDataList(query) {
  return request({
    url: '/email/sales/list',
    method: 'get',
    params: query
  })
}

// 查询销售数据详细
export function getSalesData(salesId) {
  return request({
    url: '/email/sales/' + salesId,
    method: 'get'
  })
}

// 新增销售数据
export function addSalesData(data) {
  return request({
    url: '/email/sales',
    method: 'post',
    data: data
  })
}

// 修改销售数据
export function updateSalesData(data) {
  return request({
    url: '/email/sales',
    method: 'put',
    data: data
  })
}

// 删除销售数据
export function delSalesData(salesId) {
  return request({
    url: '/email/sales/' + salesId,
    method: 'delete'
  })
}

// 批量删除销售数据
export function delSalesDataBatch(salesIds) {
  return request({
    url: '/email/sales/batch',
    method: 'delete',
    data: salesIds
  })
}

// 导出销售数据
export function exportSalesData(query) {
  return request({
    url: '/email/sales/export',
    method: 'post',
    data: query
  })
}

// 获取销售数据统计
export function getSalesStatistics(query) {
  return request({
    url: '/email/sales/statistics',
    method: 'get',
    params: query
  })
}