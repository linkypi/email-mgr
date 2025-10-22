<template>
  <span v-if="count > 0" class="email-unread-badge">
    {{ count > 99 ? '99+' : count }}
  </span>
</template>

<script>
import { getInboxUnreadCount, getSentUnreadCount, getStarredUnreadCount, getDeletedUnreadCount } from "@/api/email/personal";
import { mapGetters } from 'vuex';
import { isGuestUser } from '@/utils/guestUserCheck';

export default {
  name: "EmailUnreadBadge",
  props: {
    type: {
      type: String,
      required: true,
      validator: value => ['inbox', 'sent', 'starred', 'deleted'].includes(value)
    }
  },
  data() {
    return {
      count: 0,
      timer: null
    };
  },
  computed: {
    ...mapGetters(['roles']),
    // 检查是否为访客用户
    isGuestUser() {
      return isGuestUser(this.roles);
    }
  },
  mounted() {
    console.log('EmailUnreadBadge mounted, type:', this.type);
    console.log('EmailUnreadBadge roles:', this.roles);
    console.log('EmailUnreadBadge isGuestUser:', this.isGuestUser);
    
    // 延迟执行，确保角色信息已加载
    this.$nextTick(() => {
      // 再次检查角色信息
      console.log('EmailUnreadBadge $nextTick roles:', this.roles);
      console.log('EmailUnreadBadge $nextTick isGuestUser:', this.isGuestUser);
      this.fetchUnreadCount();
    });
    
    // 只有非访客用户才启动定时器
    if (!this.isGuestUser) {
      // 每30秒刷新一次未读数量
      this.timer = setInterval(this.fetchUnreadCount, 30000);
      console.log('EmailUnreadBadge: 用户有权限，启动定时器，用户角色:', this.roles);
    } else {
      console.log('EmailUnreadBadge: 访客用户，跳过定时器');
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    async fetchUnreadCount() {
      try {
        // 如果是访客用户，不请求这些接口，直接返回0
        if (this.isGuestUser) {
          this.count = 0;
          return;
        }

        let response;
        switch (this.type) {
          case 'inbox':
            response = await getInboxUnreadCount();
            break;
          case 'sent':
            response = await getSentUnreadCount();
            break;
          case 'starred':
            response = await getStarredUnreadCount();
            break;
          case 'deleted':
            response = await getDeletedUnreadCount();
            break;
          default:
            return;
        }
        this.count = response.data || 0;
      } catch (error) {
        // 如果是访客用户，静默处理错误，不显示错误提示
        if (this.isGuestUser) {
          console.log('访客用户获取未读数量失败，静默处理:', error);
        } else {
          console.error('获取未读数量失败:', error);
        }
        this.count = 0;
      }
    },
    // 手动刷新未读数量
    refresh() {
      this.fetchUnreadCount();
    }
  }
};
</script>

<style scoped>
.email-unread-badge {
  display: inline-block;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  padding: 0 6px;
  font-size: 12px;
  font-weight: bold;
  text-align: center;
  background-color: #1890ff; /* 改为RuoYi框架统一的蓝色 */
  color: white;
  border-radius: 9px;
  margin-left: 8px;
  vertical-align: middle;
}

.email-unread-badge:hover {
  background-color: #40a9ff; /* 悬停时的浅蓝色 */
}
</style>

