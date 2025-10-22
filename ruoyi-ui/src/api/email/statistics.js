import request from '@/utils/request'

// 获取今日统计数据
export function getTodayStats() {
  return request({
    url: '/email/statistics/today',
    method: 'get'
  })
}

// 获取总体统计数据
export function getTotalStats() {
  return request({
    url: '/email/statistics/total',
    method: 'get'
  })
}

// 获取发送趋势数据
export function getSendTrends(days = 7) {
  return request({
    url: '/email/statistics/trends',
    method: 'get',
    params: { days }
  })
}

// 获取回复率统计
export function getReplyRates() {
  return request({
    url: '/email/statistics/reply-rates',
    method: 'get'
  })
}

// 获取详细统计数据
export function getDetailedStats(params) {
  return request({
    url: '/email/statistics/detailed',
    method: 'get',
    params: params
  })
}

export function exportStatistics(params) {
  return request({
    url: '/email/statistics/export',
    method: 'post',
    data: params,
    responseType: 'blob'
  })
}

// 获取邮箱账号列表
export function getAccounts() {
  return request({
    url: '/email/statistics/accounts',
    method: 'get'
  })
}

