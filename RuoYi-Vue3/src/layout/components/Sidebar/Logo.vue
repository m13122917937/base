<template>
  <div class="sidebar-logo-container" :class="{ collapse, horizontal }">
    <transition name="sidebarLogoFade">
      <router-link v-if="horizontal" key="horizontal" class="sidebar-logo-link" to="/" :aria-label="title + '管理工作台'">
        <span class="sidebar-logo horizontal-monogram" aria-hidden="true">{{ title.slice(0, 1) }}</span>
        <span class="horizontal-brand"><strong>{{ title }}</strong><span>{{ defaultSettings.headerCaption }}</span></span>
      </router-link>
      <router-link v-else-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo"/>
        <h1 v-else class="sidebar-title">{{ title }}</h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo"/>
        <h1 class="sidebar-title">{{ title }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import logo from '@/assets/logo/logo.png'
import useSettingsStore from '@/store/modules/settings'
import variables from '@/assets/styles/variables.module.scss'
import defaultSettings from '@/settings'

const props = defineProps({
  collapse: {
    type: Boolean,
    required: true
  },
  horizontal: {
    type: Boolean,
    default: false
  }
})

const title = computed(() => props.horizontal ? defaultSettings.headerBrand : import.meta.env.VITE_APP_TITLE)
const settingsStore = useSettingsStore()
const sideTheme = computed(() => settingsStore.sideTheme)

// 获取Logo背景色
const getLogoBackground = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-bg)'
  }
  if (props.horizontal) {
    return variables.menuLightBg
  }
  return sideTheme.value === 'theme-dark' ? variables.menuBg : variables.menuLightBg
})

// 获取Logo文字颜色
const getLogoTextColor = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-logo-text)'
  }
  if (props.horizontal) {
    return variables.menuLightText
  }
  return sideTheme.value === 'theme-dark' ? '#fff' : variables.menuLightText
})
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: var(--app-navbar-height);
  line-height: var(--app-navbar-height);
  background: v-bind(getLogoBackground);
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 12px;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: v-bind(getLogoTextColor);
      font-weight: 600;
      line-height: var(--app-navbar-height);
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }

  &.horizontal {
    background: transparent;
    text-align: left;
    line-height: normal;

    .sidebar-logo-link { display: flex; align-items: center; gap: 10px; padding-right: 10px; }
    .sidebar-logo { width: 32px; height: 32px; margin: 0; flex-shrink: 0; }
    .horizontal-monogram { display: grid; place-items: center; border-radius: var(--navigation-radius); background: var(--el-color-primary); color: var(--el-color-white); font-size: 18px; line-height: 1; font-weight: 600; }
    .horizontal-brand { display: flex; flex-direction: column; min-width: 0; }
    .horizontal-brand strong { color: var(--navigation-heading); font-size: 16px; line-height: 1.4; font-weight: 600; white-space: nowrap; }
    .horizontal-brand > span { color: var(--navigation-text); font-size: 10px; line-height: 1.4; letter-spacing: 2px; }
  }
}
</style>
