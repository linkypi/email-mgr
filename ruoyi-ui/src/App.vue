<template>
  <div id="app">
    <router-view />
    <theme-picker />
    <email-unread-initializer />
  </div>
</template>

<script>
import ThemePicker from "@/components/ThemePicker"
import EmailUnreadInitializer from "@/components/EmailUnreadInitializer"
import guestUserState from "@/utils/guestUserState"

export default {
  name: "App",
  components: { 
    ThemePicker,
    EmailUnreadInitializer
  },
  mounted() {
    // 初始化访客用户状态管理器
    guestUserState.init()
    console.log('App: 访客用户状态管理器已初始化')
  },
  beforeDestroy() {
    // 销毁访客用户状态管理器
    guestUserState.destroy()
  }
}
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
