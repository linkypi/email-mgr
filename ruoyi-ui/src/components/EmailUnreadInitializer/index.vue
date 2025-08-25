<template>
  <div style="display: none;"></div>
</template>

<script>
import emailUnreadManager from '@/utils/emailUnreadManager'

export default {
  name: 'EmailUnreadInitializer',
  mounted() {
    // 初始化邮件未读数量管理器
    this.initEmailUnreadManager()
  },
  beforeDestroy() {
    // 销毁管理器
    emailUnreadManager.destroy()
  },
  methods: {
    async initEmailUnreadManager() {
      try {
        await emailUnreadManager.init()
        console.log('邮件未读数量管理器初始化成功')
      } catch (error) {
        console.error('邮件未读数量管理器初始化失败:', error)
      }
    }
  }
}
</script>


