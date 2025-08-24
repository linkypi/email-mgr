<template>
  <div class="email-menu-item">
    <el-menu-item 
      :index="resolvePath(item.path)" 
      :class="{'submenu-title-noDropdown': !isNest}"
      @click="handleClick"
    >
      <div class="menu-content">
        <div class="menu-icon" v-if="item.meta && item.meta.icon">
          <svg-icon :icon-class="item.meta.icon" />
        </div>
        <div class="menu-title">
          <span>{{ item.meta ? item.meta.title : item.name }}</span>
        </div>
        <email-menu-badge 
          v-if="badgeType" 
          :menu-type="badgeType" 
          class="menu-badge"
        />
      </div>
    </el-menu-item>
  </div>
</template>

<script>
import path from 'path'
import { isExternal } from '@/utils/validate'
import EmailMenuBadge from '@/components/EmailMenuBadge'

export default {
  name: 'EmailMenuItem',
  components: { EmailMenuBadge },
  props: {
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  computed: {
    badgeType() {
      if (!this.item) return ''
      
      // 通过菜单名称识别邮件类型
      const menuName = this.item.meta ? this.item.meta.title : this.item.name
      if (!menuName) return ''
      
      switch (menuName) {
        case '收件箱':
          return 'inbox'
        case '发件箱':
          return 'sent'
        case '星标邮件':
          return 'starred'
        case '已删除':
          return 'deleted'
        default:
          return ''
      }
    }
  },
  methods: {
    resolvePath(routePath) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(this.basePath)) {
        return this.basePath
      }
      return path.resolve(this.basePath, routePath)
    },
    handleClick() {
      // 处理菜单点击事件
      this.$router.push(this.resolvePath(this.item.path))
    }
  }
}
</script>

<style scoped>
.email-menu-item {
  position: relative;
}

.menu-content {
  display: flex;
  align-items: center;
  width: 100%;
  position: relative;
}

.menu-icon {
  margin-right: 8px;
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

.menu-title {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-badge {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  flex-shrink: 0;
}

/* 确保菜单项内容不被截断 */
.el-menu-item {
  padding-right: 40px !important;
}

/* 响应式处理 */
@media (max-width: 768px) {
  .el-menu-item {
    padding-right: 35px !important;
  }
}
</style>
