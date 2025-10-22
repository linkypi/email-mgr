import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

// 获取邮件统计数据
export function getEmailStats() {
  return request({
    url: '/dashboard/email/data',
    method: 'get'
  })
}

// 获取系统信息
export function getSystemInfo() {
  return request({
    url: '/dashboard/system/info',
    method: 'get'
  })
}

// 获取最近活动
export function getRecentActivities() {
  return request({
    url: '/dashboard/activities',
    method: 'get'
  })
}

// 获取邮件发送趋势
export function getEmailTrends(params) {
  return request({
    url: '/email/dashboard/trends',
    method: 'get',
    params: params
  })
}


