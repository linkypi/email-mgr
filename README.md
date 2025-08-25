


sendWithLongConnection 方法无需再重新建立长连接，而是应该使用 EmailServiceMonitorService 中的 accountMonitorStates 中的长连接来发送邮件



email/task/create 接口发送邮件应该使用  EmailServiceMonitorService 中的
accountMonitorStates 长连接来发送，同时当接口sendMode参数不是 immediate 时，
应该需要基于时间轮算法来实现一个定时发送的功能，同时为保证任务不会因为服务宕机而丢失，
那么这些定时任务应该放到redis zset 中，只有任务执行完成后才可以删除
    