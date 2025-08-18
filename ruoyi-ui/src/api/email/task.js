import request from '@/utils/request'

// 查询邮件发送任务列表
export function listTask(query) {
  return request({
    url: '/email/task/list',
    method: 'get',
    params: query
  })
}

// 查询邮件发送任务详细
export function getTask(taskId) {
  return request({
    url: '/email/task/' + taskId,
    method: 'get'
  })
}

// 新增邮件发送任务
export function addTask(data) {
  return request({
    url: '/email/task',
    method: 'post',
    data: data
  })
}

// 修改邮件发送任务
export function updateTask(data) {
  return request({
    url: '/email/task',
    method: 'put',
    data: data
  })
}

// 删除邮件发送任务
export function delTask(taskId) {
  return request({
    url: '/email/task/' + taskId,
    method: 'delete'
  })
}

// 导出邮件发送任务
export function exportTask(query) {
  return request({
    url: '/email/task/export',
    method: 'post',
    data: query
  })
}

// 启动发送任务
export function startTask(taskId) {
  return request({
    url: '/email/task/start/' + taskId,
    method: 'post'
  })
}

// 暂停发送任务
export function pauseTask(taskId) {
  return request({
    url: '/email/task/pause/' + taskId,
    method: 'post'
  })
}

// 获取任务统计信息
export function getTaskStatistics(taskId) {
  return request({
    url: '/email/task/statistics/' + taskId,
    method: 'get'
  })
}
