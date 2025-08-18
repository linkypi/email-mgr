// 模拟数据，用于演示批量发送功能
// 在实际项目中，这些数据应该从后端API获取

export const mockTemplates = [
  {
    templateId: 1,
    templateName: '产品推广邮件模板',
    subject: '新产品发布通知',
    content: '<p>尊敬的客户：</p><p>我们很高兴地通知您，我们的新产品已经正式发布...</p>',
    useCount: 24,
    createTime: '2023-06-15 10:30:00'
  },
  {
    templateId: 2,
    templateName: '会议邀请模板',
    subject: '关于召开2023下半年产品策略会议的通知',
    content: '<p>尊敬的各位领导、同事：</p><p>为规划公司2023下半年产品发展战略与市场策略，定于2023年7月25日（下周二）下午14:00在公司大会议室召开产品策略会议。</p>',
    useCount: 18,
    createTime: '2023-06-10 14:20:00'
  },
  {
    templateId: 3,
    templateName: '月度报表通知',
    subject: '2023年6月月度报表已生成',
    content: '<p>各位同事：</p><p>2023年6月月度报表已生成完成，请及时查看...</p>',
    useCount: 12,
    createTime: '2023-06-01 09:00:00'
  },
  {
    templateId: 4,
    templateName: '公司内部通告模板',
    subject: '公司内部重要通知',
    content: '<p>全体员工：</p><p>根据公司发展需要，现发布以下重要通知...</p>',
    useCount: 8,
    createTime: '2023-05-20 16:45:00'
  },
  {
    templateId: 5,
    templateName: '客户关怀邮件模板',
    subject: '感谢您的支持与信任',
    content: '<p>尊敬的客户：</p><p>感谢您一直以来对我们公司的支持与信任...</p>',
    useCount: 15,
    createTime: '2023-05-15 11:30:00'
  }
]

export const mockAccounts = [
  {
    accountId: 1,
    emailAddress: 'marketing@company.com',
    accountName: '市场部邮箱',
    dailyLimit: 100,
    status: 'enabled',
    createTime: '2023-01-15 10:00:00'
  },
  {
    accountId: 2,
    emailAddress: 'support@company.com',
    accountName: '客户支持邮箱',
    dailyLimit: 80,
    status: 'enabled',
    createTime: '2023-01-20 14:30:00'
  },
  {
    accountId: 3,
    emailAddress: 'public@company.com',
    accountName: '公司公共邮箱',
    dailyLimit: 120,
    status: 'enabled',
    createTime: '2023-02-01 09:15:00'
  },
  {
    accountId: 4,
    emailAddress: 'hr@company.com',
    accountName: '人力资源邮箱',
    dailyLimit: 60,
    status: 'enabled',
    createTime: '2023-02-10 16:20:00'
  },
  {
    accountId: 5,
    emailAddress: 'finance@company.com',
    accountName: '财务部邮箱',
    dailyLimit: 50,
    status: 'enabled',
    createTime: '2023-02-15 11:45:00'
  }
]

export const mockGroups = [
  {
    groupId: 1,
    groupName: '管理层',
    contactCount: 18,
    description: '公司管理层人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    groupId: 2,
    groupName: '销售部',
    contactCount: 56,
    description: '销售部门人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    groupId: 3,
    groupName: '技术部',
    contactCount: 42,
    description: '技术部门人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    groupId: 4,
    groupName: '人力资源',
    contactCount: 12,
    description: '人力资源部门人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    groupId: 5,
    groupName: '财务部',
    contactCount: 14,
    description: '财务部门人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    groupId: 6,
    groupName: '客户服务',
    contactCount: 24,
    description: '客户服务部门人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    groupId: 7,
    groupName: '研发中心',
    contactCount: 36,
    description: '研发中心人员',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  }
]

export const mockTags = [
  {
    tagId: 1,
    tagName: '重要联系人',
    contactCount: 25,
    color: '#F56C6C',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    tagId: 2,
    tagName: 'VIP客户',
    contactCount: 15,
    color: '#E6A23C',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    tagId: 3,
    tagName: '合作伙伴',
    contactCount: 32,
    color: '#67C23A',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    tagId: 4,
    tagName: '潜在客户',
    contactCount: 48,
    color: '#409EFF',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  },
  {
    tagId: 5,
    tagName: '老客户',
    contactCount: 28,
    color: '#909399',
    status: 'enabled',
    createTime: '2023-01-01 00:00:00'
  }
]

export const mockContacts = [
  {
    contactId: 1,
    name: '张经理',
    email: 'zhang.manager@company.com',
    company: '公司A',
    level: '一级',
    groupId: 1,
    tags: [1, 2]
  },
  {
    contactId: 2,
    name: '王晓明',
    email: 'wang.xiaoming@company.com',
    company: '公司B',
    level: '三级',
    groupId: 2,
    tags: [4]
  },
  {
    contactId: 3,
    name: '李思思',
    email: 'li.sisi@company.com',
    company: '公司C',
    level: '二级',
    groupId: 4,
    tags: [1]
  },
  {
    contactId: 4,
    name: '赵工程师',
    email: 'zhao.engineer@company.com',
    company: '公司D',
    level: '二级',
    groupId: 3,
    tags: [3]
  },
  {
    contactId: 5,
    name: '刘总监',
    email: 'liu.director@company.com',
    company: '公司E',
    level: '一级',
    groupId: 1,
    tags: [1, 2]
  }
]

export const mockStats = {
  templateCount: 12,
  accountCount: 5,
  contactCount: 1245,
  avgReplyRate: 64.8
}

export const mockDrafts = [
  {
    draftId: 1,
    subject: '产品推广邮件',
    recipientCount: 145,
    createTime: '2023-07-15 14:30:00',
    templateId: 1,
    content: '产品推广内容...'
  },
  {
    draftId: 2,
    subject: '会议邀请通知',
    recipientCount: 89,
    createTime: '2023-07-14 10:20:00',
    templateId: 2,
    content: '会议邀请内容...'
  },
  {
    draftId: 3,
    subject: '月度报表通知',
    recipientCount: 67,
    createTime: '2023-07-13 16:45:00',
    templateId: 3,
    content: '月度报表内容...'
  }
]

// 模拟API响应
export const mockApiResponse = {
  success: true,
  code: 200,
  message: '操作成功',
  data: null
}

// 模拟进度数据
export const mockProgress = {
  taskId: 'task_001',
  status: 'RUNNING',
  sentCount: 45,
  totalCount: 145,
  successCount: 43,
  failedCount: 2,
  startTime: '2023-07-18 15:30:00',
  estimatedEndTime: '2023-07-18 16:00:00'
}
