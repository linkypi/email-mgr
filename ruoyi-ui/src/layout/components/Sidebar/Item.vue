<script>
import EmailMenuBadge from '@/components/EmailMenuBadge'

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

    // 添加未读数量标签
    if (badgeType && ['inbox', 'sent', 'starred', 'deleted'].includes(badgeType)) {
      console.log('Adding badge for:', badgeType, 'title:', title)
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
    }

    return vnodes
  }
}
</script>
