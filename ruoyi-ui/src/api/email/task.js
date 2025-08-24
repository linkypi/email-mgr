import request from '@/utils/request'

// 查询任务列表
export function listTask(query) {
  return request({
    url: '/email/task/list',
    method: 'get',
    params: query
  })
}

// 查询任务详细
export function getTask(taskId) {
  return request({
    url: '/email/task/' + taskId,
    method: 'get'
  })
}

// 新增任务
export function addTask(data) {
  return request({
    url: '/email/task',
    method: 'post',
    data: data
  })
}

// 修改任务
export function updateTask(data) {
  return request({
    url: '/email/task',
    method: 'put',
    data: data
  })
}

// 删除任务
export function delTask(taskId) {
  return request({
    url: '/email/task/' + taskId,
    method: 'delete'
  })
}

// 重新执行任务
export function restartTask(taskId) {
  return request({
    url: '/email/task/restart/' + taskId,
    method: 'post'
  })
}

// 复制任务
export function copyTask(taskId) {
  return request({
    url: '/email/task/copy/' + taskId,
    method: 'post'
  })
}

// 停止任务
export function stopTask(taskId) {
  return request({
    url: '/email/task/stop/' + taskId,
    method: 'post'
  })
}

// 获取任务执行记录
export function getTaskExecutions(taskId) {
  return request({
    url: '/email/task/executions/' + taskId,
    method: 'get'
  })
}

// 创建邮件发送任务（用于批量发送页面）
export function createSendTask(data) {
  return request({
    url: '/email/task/create',
    method: 'post',
    data: data
  })
}
