<template>
  <div style="display: none;"></div>
</template>

<script>
import emailUnreadManager from '@/utils/emailUnreadManager'
import { mapGetters } from 'vuex'
import { isGuestUser } from '@/utils/guestUserCheck'

export default {
  name: 'EmailUnreadInitializer',
  computed: {
    ...mapGetters(['roles']),
    // 检查是否为访客用户
    isGuestUser() {
      console.log('EmailUnreadInitializer roles:', this.roles);
      return isGuestUser(this.roles);
    }
  },
  mounted() {
    console.log('EmailUnreadInitializer mounted, roles:', this.roles);
    console.log('EmailUnreadInitializer isGuestUser:', this.isGuestUser);
    
    // 延迟执行，确保角色信息已加载
    this.$nextTick(() => {
      console.log('EmailUnreadInitializer $nextTick, roles:', this.roles);
      console.log('EmailUnreadInitializer $nextTick isGuestUser:', this.isGuestUser);
      
      // 只有非访客用户才初始化邮件未读数量管理器
      if (!this.isGuestUser) {
        console.log('非访客用户，初始化邮件未读数量管理器，用户角色:', this.roles);
        this.initEmailUnreadManager()
      } else {
        console.log('访客用户，跳过邮件未读数量管理器初始化')
      }
    });
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


