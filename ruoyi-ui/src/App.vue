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

export default {
  name: "App",
  components: { 
    ThemePicker,
    EmailUnreadInitializer
  }
}
</script>
<style scoped>
#app .theme-picker {
  display: none;
}
</style>
