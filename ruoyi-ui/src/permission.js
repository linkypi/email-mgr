import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

// 检查是否为访客用户
const checkIsGuestUser = (roles) => {
  if (!Array.isArray(roles)) return false
  return roles.some(role => {
    if (typeof role === 'string') {
      return role === 'guest' || role.includes('guest')
    } else if (typeof role === 'object' && role !== null) {
      return role.roleKey === 'guest' || role.roleName === '访客账号' || 
             (role.roleName && role.roleName.includes('访客'))
    }
    return false
  })
}

// 获取第一个可访问的路由
const getFirstAccessibleRoute = (routes) => {
  if (!Array.isArray(routes)) {
    console.log('getFirstAccessibleRoute: routes is not array', routes)
    return null
  }
  
  console.log('getFirstAccessibleRoute: searching in routes', routes)
  
  // 递归查找第一个非隐藏的路由
  const findFirstRoute = (routeList, parentPath = '') => {
    for (const route of routeList) {
      console.log('checking route:', route.path, 'hidden:', route.hidden)
      
      // 跳过隐藏的路由和根路径
      if (route.hidden || route.path === '/' || route.path === '/index') {
        continue
      }
      
      // 构建完整路径
      const fullPath = parentPath ? `${parentPath}/${route.path}` : route.path
      
      // 如果有子路由，递归查找
      if (route.children && route.children.length > 0) {
        const childRoute = findFirstRoute(route.children, fullPath)
        if (childRoute) return childRoute
      } else if (route.path && route.path !== '/') {
        // 找到第一个有效的路由
        console.log('found first accessible route:', fullPath)
        return fullPath
      }
    }
    return null
  }
  
  const result = findFirstRoute(routes)
  console.log('getFirstAccessibleRoute result:', result)
  return result
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            
            // 检查是否为访客用户访问根路径
            if (to.path === '/' || to.path === '/index') {
              const isGuestUser = checkIsGuestUser(store.getters.roles)
              console.log('isGuestUser:', isGuestUser, 'roles:', store.getters.roles)
              if (isGuestUser) {
                // 访客用户访问根路径，跳转到第一个有权限的页面
                const firstAccessibleRoute = getFirstAccessibleRoute(accessRoutes)
                console.log('firstAccessibleRoute:', firstAccessibleRoute)
                if (firstAccessibleRoute) {
                  console.log('redirecting guest user to:', firstAccessibleRoute)
                  next({ path: firstAccessibleRoute, replace: true })
                  NProgress.done()
                  return
                } else {
                  console.log('no accessible route found for guest user, redirecting to 401')
                  // 如果没有可访问的路由，跳转到401页面
                  next({ path: '/401', replace: true })
                  NProgress.done()
                  return
                }
              }
            }
            
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
      } else {
        // 检查是否为访客用户访问根路径
        if (to.path === '/' || to.path === '/index') {
          const isGuestUser = checkIsGuestUser(store.getters.roles)
          console.log('isGuestUser (second check):', isGuestUser, 'roles:', store.getters.roles)
          if (isGuestUser) {
            // 访客用户访问根路径，跳转到第一个有权限的页面
            const firstAccessibleRoute = getFirstAccessibleRoute(store.getters.addRoutes)
            console.log('firstAccessibleRoute (second check):', firstAccessibleRoute)
            if (firstAccessibleRoute) {
              console.log('redirecting guest user to (second check):', firstAccessibleRoute)
              next({ path: firstAccessibleRoute, replace: true })
              NProgress.done()
              return
            } else {
              console.log('no accessible route found for guest user (second check), redirecting to 401')
              // 如果没有可访问的路由，跳转到401页面
              next({ path: '/401', replace: true })
              NProgress.done()
              return
            }
          }
        }
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
