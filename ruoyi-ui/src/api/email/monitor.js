import request from '@/utils/request'

// 查询邮件服务监控列表
export function listMonitor(query) {
  return request({
    url: '/email/monitor/list',
    method: 'get',
    params: query
  })
}

// 查询邮件服务监控详细
export function getMonitor(id) {
  return request({
    url: '/email/monitor/' + id,
    method: 'get'
  })
}

// 启动全局监控
export function startGlobalMonitor() {
  return request({
    url: '/email/monitor/start',
    method: 'post'
  })
}

// 停止全局监控
export function stopGlobalMonitor() {
  return request({
    url: '/email/monitor/stop',
    method: 'post'
  })
}

// 启动指定账号监控
export function startAccountMonitor(accountId) {
  return request({
    url: '/email/monitor/start/' + accountId,
    method: 'post'
  })
}

// 停止指定账号监控
export function stopAccountMonitor(accountId) {
  return request({
    url: '/email/monitor/stop/' + accountId,
    method: 'post'
  })
}



// 重启指定账号监控
export function restartAccountMonitor(accountId) {
  return request({
    url: '/email/monitor/restart/' + accountId,
    method: 'post'
  })
}



// 获取监控状态
export function getMonitorStatus() {
  return request({
    url: '/email/monitor/status',
    method: 'get'
  })
}

// 获取账号监控状态
export function getAccountMonitorStatus(accountId) {
  return request({
    url: '/email/monitor/status/' + accountId,
    method: 'get'
  })
}

// 查询监控记录列表
export function listMonitorLog(query) {
  return request({
    url: '/email/monitor/log/list',
    method: 'get',
    params: query
  })
}

// 获取账号监控记录
export function getAccountMonitorLogs(accountId, params) {
  return request({
    url: '/email/monitor/log/' + accountId,
    method: 'get',
    params: params
  })
}

// 获取监控统计
export function getMonitorStats(accountId) {
  return request({
    url: '/email/monitor/stats/' + accountId,
    method: 'get'
  })
}

// 导出邮件服务监控
export function exportMonitor(query) {
  return request({
    url: '/email/monitor/export',
    method: 'get',
    params: query
  })
}

// 导出监控记录
export function exportMonitorLog(query) {
  return request({
    url: '/email/monitor/log/export',
    method: 'get',
    params: query
  })
}



// 获取指定账号的连接状态
export function getAccountConnectionStatus(accountId) {
  return request({
    url: '/email/monitor/connection/status/' + accountId,
    method: 'get'
  })
}

// 获取所有账号的连接状态
export function getAllConnectionStatus() {
  return request({
    url: '/email/monitor/connection/status/all',
    method: 'get'
  })
}

// 启动EmailListener服务
export function startEmailListener() {
  return request({
    url: '/email/monitor/listener/start',
    method: 'post'
  })
}

// 停止EmailListener服务
export function stopEmailListener() {
  return request({
    url: '/email/monitor/listener/stop',
    method: 'post'
  })
}

// 重启EmailListener服务
export function restartEmailListener() {
  return request({
    url: '/email/monitor/listener/restart',
    method: 'post'
  })
}

// 获取EmailListener服务状态
export function getEmailListenerStatus() {
  return request({
    url: '/email/monitor/listener/status',
    method: 'get'
  })
}
