<script>
import EmailMenuBadge from '@/components/EmailMenuBadge'
import store from '@/store'
import { isGuestUser } from '@/utils/guestUserCheck'

export default {
  name: 'MenuItem',
  functional: true,
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    },
    badgeType: {
      type: String,
      default: ''
    }
  },
  render(h, context) {
    const { icon, title, badgeType } = context.props
    const vnodes = []

    if (icon) {
      vnodes.push(<svg-icon icon-class={icon}/>)
    }

    if (title) {
      if (title.length > 5) {
        vnodes.push(<span slot='title' title={(title)}>{(title)}</span>)
      } else {
        vnodes.push(<span slot='title'>{(title)}</span>)
      }
    }

    // 添加未读数量标签 - 只有非访客用户才显示
    if (badgeType && ['inbox', 'sent', 'starred', 'deleted'].includes(badgeType)) {
      const roles = store.getters.roles || [];
      const isGuest = isGuestUser(roles);
      const rolesLoaded = roles && roles.length > 0;
      
      console.log('Sidebar Item - Adding badge for:', badgeType, 'title:', title, 'isGuest:', isGuest, 'rolesLoaded:', rolesLoaded);
      
      // 只有非访客用户且角色已加载才渲染EmailMenuBadge组件
      if (!isGuest && rolesLoaded) {
        console.log('Sidebar Item - 用户有权限，渲染EmailMenuBadge组件');
        vnodes.push(h(EmailMenuBadge, {
          props: {
            menuType: badgeType,
            forceShow: true // 强制显示用于测试
          },
          style: {
            position: 'absolute',
            right: '20px',
            top: '50%',
            transform: 'translateY(-50%)'
          }
        }))
      } else {
        console.log('Sidebar Item - 访客用户或角色未加载，跳过EmailMenuBadge渲染');
      }
    }

    return vnodes
  }
}
</script>
