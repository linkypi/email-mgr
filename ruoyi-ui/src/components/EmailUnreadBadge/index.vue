<template>
  <span v-if="count > 0" class="email-unread-badge">
    {{ count > 99 ? '99+' : count }}
  </span>
</template>

<script>
import { getInboxUnreadCount, getSentUnreadCount, getStarredUnreadCount, getDeletedUnreadCount } from "@/api/email/personal";

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
  mounted() {
    this.fetchUnreadCount();
    // 每30秒刷新一次未读数量
    this.timer = setInterval(this.fetchUnreadCount, 30000);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    async fetchUnreadCount() {
      try {
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
        console.error('获取未读数量失败:', error);
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

