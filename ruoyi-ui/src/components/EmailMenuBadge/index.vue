<template>
  <span v-if="count > 0 || forceShow" class="menu-unread-badge">
    {{ count > 99 ? '99+' : count || '0' }}
  </span>
</template>

<script>
import { getInboxUnreadCount, getSentUnreadCount, getStarredCount, getDeletedCount, getSentTotalCount } from "@/api/email/personal";
import { mapGetters } from 'vuex';
import { isGuestUser } from '@/utils/guestUserCheck';

export default {
  name: "EmailMenuBadge",
  props: {
    menuType: {
      type: String,
      required: true,
      validator: value => ['inbox', 'sent', 'starred', 'deleted'].includes(value)
    },
    forceShow: {
      type: Boolean,
      default: false
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
    console.log('EmailMenuBadge mounted, menuType:', this.menuType);
    console.log('EmailMenuBadge roles:', this.roles);
    console.log('EmailMenuBadge isGuestUser:', this.isGuestUser);
    
    // 延迟执行，确保角色信息已加载
    this.$nextTick(() => {
      // 再次检查角色信息
      console.log('EmailMenuBadge $nextTick roles:', this.roles);
      console.log('EmailMenuBadge $nextTick isGuestUser:', this.isGuestUser);
      this.fetchUnreadCount();
    });
    
    // 只有非访客用户才启动定时器
    if (!this.isGuestUser) {
      // 每60秒刷新一次未读数量
      this.timer = setInterval(this.fetchUnreadCount, 60000);
      // 监听全局事件，当邮件状态改变时刷新
      this.$bus.$on('email-status-changed', this.fetchUnreadCount);
      console.log('EmailMenuBadge: 用户有权限，启动定时器和事件监听器，用户角色:', this.roles);
    } else {
      console.log('EmailMenuBadge: 访客用户，跳过定时器和事件监听器');
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    this.$bus.$off('email-status-changed', this.fetchUnreadCount);
  },
  methods: {
    async fetchUnreadCount() {
      try {
        // 如果是访客用户，不请求这些接口，直接返回0
        if (this.isGuestUser) {
          this.count = 0;
          return;
        }

        console.log('Fetching unread count for:', this.menuType);
        let response;
        switch (this.menuType) {
          case 'inbox':
            response = await getInboxUnreadCount();
            break;
          case 'sent':
            response = await getSentTotalCount(); // 发件箱显示总数量
            break;
          case 'starred':
            response = await getStarredCount(); // 星标邮件显示总数量
            break;
          case 'deleted':
            response = await getDeletedCount(); // 已删除邮件显示总数量
            break;
          default:
            return;
        }
        this.count = response.data || 0;
        console.log('Unread count for', this.menuType, ':', this.count);
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
.menu-unread-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  font-size: 11px;
  font-weight: bold;
  text-align: center;
  background-color: #1890ff; /* 改为RuoYi框架统一的蓝色 */
  color: white;
  border-radius: 9px;
  margin-left: 8px;
  vertical-align: middle;
  position: relative;
  top: -1px;
  line-height: 1;
  white-space: nowrap;
  box-sizing: border-box;
}

.menu-unread-badge:hover {
  background-color: #40a9ff; /* 悬停时的浅蓝色 */
}

/* 确保在菜单中的正确显示 */
.el-menu-item .menu-unread-badge {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  margin-left: 0;
}

/* 响应式处理 */
@media (max-width: 768px) {
  .menu-unread-badge {
    min-width: 16px;
    height: 16px;
    font-size: 10px;
    padding: 0 4px;
  }
  
  .el-menu-item .menu-unread-badge {
    right: 15px;
  }
}
</style>

