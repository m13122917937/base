<template>
  <nav ref="navigationRef" class="horizontal-navigation" aria-label="主导航">
    <el-menu
        :key="visibleCount"
        class="horizontal-menu"
        :default-active="activeMenu"
        :ellipsis="false"
        mode="horizontal"
        @select="handleSelect"
    >
      <navigation-item v-for="item in visibleMenus" :key="item.path" :item="item"/>
      <el-sub-menu v-if="overflowMenus.length" index="navigation-more" class="more-menu"
                   popper-class="navigation-popup" :teleported="true">
        <template #title>更多菜单</template>
        <navigation-item v-for="item in overflowMenus" :key="item.path" :item="item"/>
      </el-sub-menu>
    </el-menu>
  </nav>
</template>

<script setup>
import {useResizeObserver} from '@vueuse/core'
import {ElMessage} from 'element-plus'
import NavigationItem from './NavigationItem.vue'
import {isExternal} from '@/utils/validate'
import {buildNavigationMenus, findNavigationMenu, getMenuLocation, getVisibleMenuCount} from '@/utils/navigation'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()
const navigationRef = ref(null)
const availableWidth = ref(0)
// defaultRoutes 包含公共路由和后端按权限返回的路由，不受混合布局侧栏切换影响。
const menus = computed(() => buildNavigationMenus(permissionStore.defaultRoutes))
const activeMenu = computed(() => route.meta.activeMenu || route.path)
const visibleCount = computed(() => getVisibleMenuCount(menus.value.length, availableWidth.value))
const visibleMenus = computed(() => menus.value.slice(0, visibleCount.value))
const overflowMenus = computed(() => menus.value.slice(visibleCount.value))

useResizeObserver(navigationRef, entries => {
  availableWidth.value = entries[0].contentRect.width
})

function handleSelect(path) {
  const menu = findNavigationMenu(menus.value, path)
  if (!menu) return
  if (isExternal(path)) {
    window.open(path, '_blank', 'noopener,noreferrer')
    return
  }
  let location
  try {
    location = getMenuLocation(menu)
  } catch {
    ElMessage.error('菜单参数无效，请联系管理员')
    return
  }
  router.push(location)
}
</script>

<style scoped lang="scss">
.horizontal-navigation {
  position: relative;
  flex: 1;
  min-width: 112px;
  overflow: hidden;
}

.horizontal-menu {
  --el-menu-horizontal-height: var(--app-navbar-height);
  --el-menu-bg-color: var(--navigation-bg);
  --el-menu-text-color: var(--navigation-text);
  --el-menu-hover-bg-color: var(--navigation-hover-bg);
  --el-menu-hover-text-color: var(--navigation-hover-text);
  border-bottom: 0;

  :deep(> .el-menu-item),
  :deep(> .el-sub-menu) {
    flex: 0 0 144px;
    width: 144px;
    min-width: 0;
  }

  :deep(> .more-menu) {
    flex-basis: 112px;
    width: 112px;
  }

  :deep(> .el-sub-menu > .el-sub-menu__title),
  :deep(> .el-menu-item) {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 0 12px;
    gap: 0;
    font-size: 14px;
    font-weight: 400;
    border-bottom-width: 2px;
    color: var(--navigation-text);
  }

  :deep(> .el-sub-menu.is-active > .el-sub-menu__title),
  :deep(> .el-menu-item.is-active) {
    color: var(--navigation-active-text);
    font-weight: 600;
    background: transparent;
  }

  :deep(> .el-sub-menu > .el-sub-menu__title .el-sub-menu__icon-arrow) {
    position: absolute;
    right: 6px;
    margin: -6px 0 0;
    width: 12px;
    height: 12px;
    color: inherit;
    font-size: 12px;
  }

  :deep(> .el-sub-menu > .el-sub-menu__title .svg-icon),
  :deep(> .el-menu-item > .svg-icon) {
    width: 14px;
    height: 14px;
    margin: 0 6px 0 0;
  }

  :deep(.navigation-title) {
    margin: 0;
    max-width: 5em;
  }
}

@media (prefers-reduced-motion: reduce) {
  .horizontal-menu :deep(.el-sub-menu__icon-arrow) {
    transition: none;
  }
}
</style>
