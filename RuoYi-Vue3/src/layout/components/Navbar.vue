<template>
  <header class="navbar" :class="{ 'horizontal-navbar': navType === 3 }">
    <div class="navbar-left">
      <logo v-if="navType === 3 && settingsStore.sidebarLogo" :collapse="false" horizontal class="horizontal-logo"/>
      <button v-if="navType !== 3" type="button" class="navigation-action"
              :aria-label="appStore.sidebar.opened ? '收起导航' : '展开导航'" :aria-expanded="appStore.sidebar.opened"
              @click="toggleSideBar">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
          <rect x="3" y="4" width="18" height="16" rx="2"/>
          <path d="M9 4v16"/>
        </svg>
      </button>
      <button type="button" class="navigation-action refresh-action" aria-label="刷新当前页面" title="刷新当前页面"
              @click="refreshPage">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <polyline points="23 4 23 10 17 10"/>
          <polyline points="1 20 1 14 7 14"/>
          <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
        </svg>
      </button>
      <breadcrumb v-if="navType === 1" id="breadcrumb-container" class="breadcrumb-container"/>
      <top-nav v-else-if="navType === 2" id="topmenu-container" class="topmenu-container"/>
      <top-bar v-else id="topbar-container"/>
    </div>

    <div class="navbar-center">
      <header-search id="header-search"/>
    </div>

    <div class="navbar-right">
      <ai-chat/>
      <button v-if="settingsStore.showSettings" type="button" class="navigation-action" aria-label="布局设置"
              title="布局设置" @click="setLayout">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
          <path d="M4 7h16M4 17h16"/>
          <circle cx="9" cy="7" r="3" fill="var(--navigation-bg)"/>
          <circle cx="15" cy="17" r="3" fill="var(--navigation-bg)"/>
        </svg>
      </button>
      <screenfull v-if="appStore.device !== 'mobile'" id="screenfull" class="screenfull-btn"/>
      <theme-color-panel/>
      <el-dropdown @command="handleCommand" class="avatar-container" trigger="click" popper-class="account-menu-popup">
        <button type="button" class="avatar-wrapper" aria-label="账户菜单">
          <img :src="userStore.avatar" class="user-avatar" alt=""/>
          <el-icon>
            <ArrowDown/>
          </el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <div><span>{{ userStore.nickName }}</span><span class="dropdown-sub">个人中心</span></div>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import {ElMessageBox} from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from './TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'
import Screenfull from '@/components/Screenfull'
import HeaderSearch from '@/components/HeaderSearch'
import AiChat from '@/components/AiChat'
import ThemeColorPanel from '@/components/ThemeColorPanel'
import useAppStore from '@/store/modules/app'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'

defineProps({navType: {type: Number, default: 1}})
const emits = defineEmits(['setLayout'])
const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

function toggleSideBar() {
  appStore.toggleSideBar()
}

function refreshPage() {
  router.replace({path: '/redirect' + route.path, query: route.query})
}

function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      logout()
      break
  }
}

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => userStore.logOut()).then(() => {
    location.href = '/'
  }).catch(() => {
  })
}

function setLayout() {
  emits('setLayout')
}

</script>

<style scoped lang="scss">
.navbar {
  display: flex;
  align-items: center;
  height: var(--app-navbar-height);
  padding: 0 var(--navigation-page-padding);
  gap: 12px;
  border-bottom: 1px solid var(--navigation-line);
  background: var(--navigation-bg);
  font-family: var(--navigation-font);
  position: relative;
  z-index: 10;
  box-shadow: none;
}

.navbar-left {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.horizontal-logo { flex-shrink: 0; }
.horizontal-navbar .navbar-left { min-width: 112px; }
.breadcrumb-container {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
  height: var(--app-navbar-height);
  overflow: hidden;
  white-space: nowrap;

  :deep(.el-breadcrumb__item) {
    display: inline-flex;
    align-items: center;
    flex-shrink: 0;
    float: none;
  }

  :deep(.el-breadcrumb__item:last-child) { min-width: 0; overflow: hidden; }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) { overflow: hidden; text-overflow: ellipsis; }
}

.topmenu-container { flex: 1; min-width: 0; }
.navbar-center { flex: 0 1 240px; min-width: 0; }
.navbar-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 4px;
  padding-left: 6px;
  border-left: 1px solid var(--navigation-line);
}

.navigation-action, .screenfull-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  border-radius: var(--navigation-radius);
  border: none;
  background: transparent;
  color: var(--navigation-text);
  cursor: pointer;
  transition: color 150ms ease, background-color 150ms ease;
  font-size: 18px;

  &:hover { background: var(--navigation-hover-bg); color: var(--navigation-hover-text); }
  &:focus-visible { outline: 3px solid var(--el-color-primary-light-7); outline-offset: 1px; }
}

.refresh-action { width: 32px; height: 32px; border-radius: 50%; }

.avatar-container { margin-left: 4px; }
.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 0;
  padding: 4px;
  background: transparent;
  color: var(--navigation-text);
  cursor: pointer;
  border-radius: var(--navigation-radius);

  &:focus-visible { outline: 3px solid var(--el-color-primary-light-7); outline-offset: 1px; }
  .user-avatar { width: 32px; height: 32px; border-radius: var(--navigation-radius); object-fit: cover; }
  .el-icon { font-size: 12px; }
}

.dropdown-sub { display: block; font-size: 11px; color: var(--navigation-text); margin-top: 1px; }

@media (max-width: 1200px) {
  .horizontal-logo :deep(.horizontal-brand) { display: none; }
  .navbar-center { flex-basis: 180px; }
  .breadcrumb-container :deep(.el-breadcrumb__item:not(:last-child)) { display: none; }
}

@media (max-width: 768px) {
  .navbar { gap: 8px; }
  .navbar-left { gap: 2px; }
  .navbar-center { flex: 0 0 36px; }
  .avatar-wrapper .el-icon { display: none; }
}

@media (max-width: 480px) {
  .navbar-right { gap: 2px; padding-left: 3px; }
  .avatar-container { margin-left: 0; }
}
</style>
