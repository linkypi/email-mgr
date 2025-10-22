import Layout from '@/layout'

const emailRouter = {
  path: '/email',
  component: Layout,
  redirect: '/email/personal',
  name: 'Email',
  hidden: true, // 隐藏邮件管理菜单
  meta: {
    title: '邮件管理',
    icon: 'email'
  },
  children: [
    {
      path: 'statistics',
      component: () => import('@/views/email/statistics/index'),
      name: 'EmailStatistics',
      meta: { title: '邮件统计', icon: 'chart' },
      hidden: true // 隐藏邮件统计路由，避免与首页重复
    },
    {
      path: 'personal',
      component: () => import('@/views/email/personal/index'),
      name: 'PersonalEmail',
      meta: { title: '个人邮件', icon: 'email' }
    },
    {
      path: 'personal/inbox',
      component: () => import('@/views/email/personal/inbox'),
      name: 'Inbox',
      meta: { title: '收件箱', icon: 'inbox' }
    },
    {
      path: 'personal/sent',
      component: () => import('@/views/email/personal/sent'),
      name: 'Sent',
      meta: { title: '发件箱', icon: 'sent' }
    },
    {
      path: 'personal/starred',
      component: () => import('@/views/email/personal/starred'),
      name: 'Starred',
      meta: { title: '星标邮件', icon: 'star' }
    },
    {
      path: 'personal/deleted',
      component: () => import('@/views/email/personal/deleted'),
      name: 'Deleted',
      meta: { title: '已删除', icon: 'delete' }
    },
    {
      path: 'personal/compose',
      component: () => import('@/views/email/personal/compose'),
      name: 'Compose',
      meta: { title: '写邮件', icon: 'edit' },
      hidden: true
    }
  ]
}

export default emailRouter


