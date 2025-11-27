/**
 * 访客用户状态管理
 * 用于解决角色信息加载时机问题
 */

import store from '@/store'
import { isGuestUser } from './guestUserCheck'

class GuestUserState {
  constructor() {
    this.isGuest = false
    this.rolesLoaded = false
    this.listeners = []
    this.checkInterval = null
  }

  // 初始化状态检查
  init() {
    console.log('GuestUserState: 初始化访客用户状态检查')
    this.checkGuestStatus()
    
    // 每100ms检查一次角色状态，直到角色加载完成
    this.checkInterval = setInterval(() => {
      this.checkGuestStatus()
      if (this.rolesLoaded) {
        clearInterval(this.checkInterval)
        this.checkInterval = null
        console.log('GuestUserState: 角色加载完成，停止检查')
      }
    }, 100)
  }

  // 检查访客状态
  checkGuestStatus() {
    const roles = store.getters.roles || []
    const wasLoaded = this.rolesLoaded
    const wasGuest = this.isGuest
    
    this.rolesLoaded = roles.length > 0
    this.isGuest = isGuestUser(roles)
    
    // 如果状态发生变化，通知监听器
    if (wasLoaded !== this.rolesLoaded || wasGuest !== this.isGuest) {
      console.log('GuestUserState: 状态变化', {
        rolesLoaded: this.rolesLoaded,
        isGuest: this.isGuest,
        roles: roles
      })
      this.notifyListeners()
    }
  }

  // 添加监听器
  addListener(callback) {
    this.listeners.push(callback)
  }

  // 移除监听器
  removeListener(callback) {
    const index = this.listeners.indexOf(callback)
    if (index > -1) {
      this.listeners.splice(index, 1)
    }
  }

  // 通知所有监听器
  notifyListeners() {
    this.listeners.forEach(callback => {
      try {
        callback({
          isGuest: this.isGuest,
          rolesLoaded: this.rolesLoaded
        })
      } catch (error) {
        console.error('GuestUserState: 通知监听器失败:', error)
      }
    })
  }

  // 获取当前状态
  getState() {
    return {
      isGuest: this.isGuest,
      rolesLoaded: this.rolesLoaded
    }
  }

  // 销毁
  destroy() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval)
      this.checkInterval = null
    }
    this.listeners = []
  }
}

// 创建单例实例
const guestUserState = new GuestUserState()

export default guestUserState













