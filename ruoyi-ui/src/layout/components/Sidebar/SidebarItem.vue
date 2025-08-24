<template>
  <div v-if="!item.hidden">
    <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)&&!item.alwaysShow">
      <!-- 所有菜单都使用原有的AppLink -->
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{'submenu-title-noDropdown':!isNest}">
          <item 
            :icon="onlyOneChild.meta.icon||(item.meta&&item.meta.icon)" 
            :title="onlyOneChild.meta.title" 
            :badge-type="getBadgeType(onlyOneChild)"
          />
        </el-menu-item>
      </app-link>
    </template>

    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <item 
          v-if="item.meta" 
          :icon="item.meta && item.meta.icon" 
          :title="item.meta.title" 
          :badge-type="getBadgeType(item)"
        />
      </template>
      <sidebar-item
        v-for="(child, index) in item.children"
        :key="child.path + index"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-submenu>
  </div>
</template>

<script>
import path from 'path'
import { isExternal } from '@/utils/validate'
import Item from './Item'
import AppLink from './Link'
import FixiOSBug from './FixiOSBug'

export default {
  name: 'SidebarItem',
  components: { Item, AppLink },
  mixins: [FixiOSBug],
  props: {
    // route object
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
  data() {
    this.onlyOneChild = null
    return {}
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      if (!children) {
        children = []
      }
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        }
        // Temp set(will be used if only has one showing child)
        this.onlyOneChild = item
        return true
      })

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = { ... parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    },
    resolvePath(routePath, routeQuery) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(this.basePath)) {
        return this.basePath
      }
      if (routeQuery) {
        let query = JSON.parse(routeQuery)
        return { path: path.resolve(this.basePath, routePath), query: query }
      }
      return path.resolve(this.basePath, routePath)
    },
    // 获取菜单项的badge类型
    getBadgeType(menuItem) {
      if (!menuItem) return ''
      
      // 通过菜单名称识别邮件类型
      const menuName = menuItem.meta ? menuItem.meta.title : menuItem.name
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
  }
}
</script>
