<template>
  <div id="tags-view-container" class="tags-view-container"
       :class="{ 'tags-view-container--chrome': tagsViewStyle === 'chrome' }">
    <!-- 左切换箭头 -->
    <button v-show="canScrollLeft || canScrollRight" type="button" class="tags-nav-btn tags-nav-btn--left" :class="{ disabled: !canScrollLeft }"
            :disabled="!canScrollLeft" aria-label="向左滚动页签" @click="scrollLeft">
      <el-icon><arrow-left/></el-icon>
    </button>

    <!-- 标签滚动区 -->
    <scroll-pane ref="scrollPaneRef" class="tags-view-wrapper" @scroll="handleScroll" @update-arrows="updateArrowState">
      <router-link
          v-for="tag in visitedViews"
          :key="tag.path"
          :data-path="tag.path"
          :class="{ 'active': isActive(tag), 'has-icon': tagsIcon }"
          :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
          class="tags-view-item"
          @click.middle="!isAffix(tag) ? closeSelectedTag(tag) : ''"
          @contextmenu.prevent="openMenu(tag, $event)"
      >
        <span class="tab-star" :class="{ 'is-favorite': isFavorite(tag) }" role="button" tabindex="0"
              :aria-label="(isFavorite(tag) ? '取消收藏' : '收藏') + tag.title" :aria-pressed="isFavorite(tag)"
              @click.prevent.stop="toggleFavoriteTag(tag)" @keydown.enter.prevent.stop="toggleFavoriteTag(tag)"
              @keydown.space.prevent.stop="toggleFavoriteTag(tag)">
          <el-icon><StarFilled v-if="isFavorite(tag)"/><Star v-else/></el-icon>
        </span>
        <svg-icon v-if="tagsIcon && tag.meta && tag.meta.icon && tag.meta.icon !== '#'" :icon-class="tag.meta.icon"
                  style="margin-right: 3px;"/>
        <span class="tab-label">{{ tag.title }}</span>
        <span v-if="!isAffix(tag)" @click.prevent.stop="closeSelectedTag(tag)" class="tags-close-btn"
              role="button" tabindex="0" :aria-label="'关闭' + tag.title"
              @keydown.enter.prevent.stop="closeSelectedTag(tag)" @keydown.space.prevent.stop="closeSelectedTag(tag)">
          <close class="el-icon-close"/>
        </span>
      </router-link>
    </scroll-pane>

    <!-- 右切换箭头 -->
    <button v-show="canScrollLeft || canScrollRight" type="button" class="tags-nav-btn tags-nav-btn--right" :class="{ disabled: !canScrollRight }"
            :disabled="!canScrollRight" aria-label="向右滚动页签" @click="scrollRight">
      <el-icon><arrow-right/></el-icon>
    </button>

    <!-- 下拉操作菜单 -->
    <el-dropdown v-if="tagsViewStyle === 'chrome'" class="tags-action-dropdown" trigger="click" placement="bottom-end" @command="handleDropdownCommand">
      <button type="button" class="tags-action-btn" aria-label="页签操作">
        <el-icon><arrow-down/></el-icon>
      </button>
      <template #dropdown>
        <el-dropdown-menu class="tags-dropdown-menu">
          <el-dropdown-item command="refresh">
            <refresh-right style="width: 1em; height: 1em;"/>
            刷新页面
          </el-dropdown-item>
          <el-dropdown-item v-if="!isAffix(selectedDropdownTag)" command="close">
            <close style="width: 1em; height: 1em;"/>
            关闭当前
          </el-dropdown-item>
          <el-dropdown-item command="closeOthers">
            <circle-close style="width: 1em; height: 1em;"/>
            关闭其他
          </el-dropdown-item>
          <el-dropdown-item command="closeLeft" :disabled="isFirstView()">
            <back style="width: 1em; height: 1em;"/>
            关闭左侧
          </el-dropdown-item>
          <el-dropdown-item command="closeRight" :disabled="isLastView()">
            <right style="width: 1em; height: 1em;"/>
            关闭右侧
          </el-dropdown-item>
          <el-dropdown-item command="closeAll">
            <circle-close style="width: 1em; height: 1em;"/>
            全部关闭
          </el-dropdown-item>
          <el-dropdown-item command="fullscreen" divided>
            <template v-if="!isFullscreen">
              <full-screen style="width: 1em; height: 1em;"/>
              全屏显示
            </template>
            <template v-else>
              <close style="width: 1em; height: 1em;"/>
              退出全屏
            </template>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 右键上下文菜单 -->
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">
        <refresh-right style="width: 1em; height: 1em;"/>
        刷新页面
      </li>
      <li @click="toggleFavoriteTag(selectedTag)">
        <el-icon><StarFilled v-if="isFavorite(selectedTag)"/><Star v-else/></el-icon>
        {{ isFavorite(selectedTag) ? '取消收藏' : '收藏此页' }}
      </li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">
        <close style="width: 1em; height: 1em;"/>
        关闭当前
      </li>
      <li @click="closeOthersTags">
        <circle-close style="width: 1em; height: 1em;"/>
        关闭其他
      </li>
      <li v-if="!isFirstView()" @click="closeLeftTags">
        <back style="width: 1em; height: 1em;"/>
        关闭左侧
      </li>
      <li v-if="!isLastView()" @click="closeRightTags">
        <right style="width: 1em; height: 1em;"/>
        关闭右侧
      </li>
      <li @click="closeAllTags(selectedTag)">
        <circle-close style="width: 1em; height: 1em;"/>
        全部关闭
      </li>
      <li @click="toggleFullscreen"><el-icon><FullScreen/></el-icon>{{ isFullscreen ? '退出全屏' : '全屏显示' }}</li>
    </ul>
  </div>
</template>

<script setup>
import ScrollPane from './ScrollPane'
import {getNormalPath} from '@/utils/ruoyi'
import useTagsViewStore from '@/store/modules/tagsView'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
import useUserStore from '@/store/modules/user'
import {ElMessageBox} from 'element-plus'
import {readFavoriteTabs, saveFavoriteTabs, getFavoriteMenuLocations} from '@/utils/favoriteTabs'

const visible = ref(false)
const top = ref(0)
const left = ref(0)
const selectedTag = ref({})
const affixTags = ref([])
const scrollPaneRef = ref(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)
const isFullscreen = ref(false)
const hiddenElements = ref([])

const {proxy} = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const settingsStore = useSettingsStore()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const favoriteTabs = ref([])

const visitedViews = computed(() => useTagsViewStore().visitedViews)
const routes = computed(() => usePermissionStore().routes)
const tagsIcon = computed(() => useSettingsStore().tagsIcon)
const tagsViewPersist = computed(() => useSettingsStore().tagsViewPersist)
const tagsViewStyle = computed(() => useSettingsStore().tagsViewStyle)

// 下拉菜单针对当前激活的 tag
const selectedDropdownTag = computed(() => visitedViews.value.find(v => isActive(v)) || {})

watch(route, () => {
  addTags()
  moveToCurrentTag()
})

watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

watch(() => visitedViews.value.map(view => [view.path, view.title]), () => {
  nextTick(() => updateArrowState())
})

watch([() => userStore.id, () => permissionStore.defaultRoutes], () => {
  favoriteTabs.value = readFavoriteTabs(userStore.id)
  restoreFavoriteTabs()
}, {immediate: true})

onMounted(() => {
  initTags()
  addTags()
  window.addEventListener('resize', updateArrowState)
  window.addEventListener('keydown', handleKeyDown)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateArrowState)
  window.removeEventListener('keydown', handleKeyDown)
  document.body.removeEventListener('click', closeMenu)
})

function handleKeyDown(event) {
  // 当按下Esc键且处于全屏状态时，退出全屏
  if (event.key === 'Escape' && isFullscreen.value) {
    toggleFullscreen()
  }
}

function isActive(r) {
  return r.path === route.path
}

function isAffix(tag) {
  return tag && tag.meta && tag.meta.affix
}

function isFavorite(tag) {
  return favoriteTabs.value.includes(tag.path)
}

function toggleFavoriteTag(tag) {
  if (!tag.path) return
  favoriteTabs.value = isFavorite(tag)
    ? favoriteTabs.value.filter(path => path !== tag.path)
    : [...favoriteTabs.value, tag.path]
  saveFavoriteTabs(userStore.id, favoriteTabs.value)
}

function restoreFavoriteTabs() {
  for (const menu of getFavoriteMenuLocations(favoriteTabs.value, permissionStore.defaultRoutes)) {
    const resolved = router.resolve({path: menu.path, query: menu.query})
    if (resolved.name) {
      useTagsViewStore().addView({...resolved, meta: {...resolved.meta, title: menu.title}})
    }
  }
}

function isFirstView() {
  try {
    const tag = selectedTag.value && selectedTag.value.fullPath ? selectedTag.value : selectedDropdownTag.value
    return tag.fullPath === visitedViews.value[0]?.fullPath
  } catch (err) {
    return false
  }
}

function isLastView() {
  try {
    const tag = selectedTag.value && selectedTag.value.fullPath ? selectedTag.value : selectedDropdownTag.value
    return tag.fullPath === visitedViews.value[visitedViews.value.length - 1].fullPath
  } catch (err) {
    return false
  }
}

function filterAffixTags(routes, basePath = '') {
  let tags = []
  routes.forEach(route => {
    if (route.meta && route.meta.affix) {
      const tagPath = getNormalPath(basePath + '/' + route.path)
      tags.push({
        fullPath: tagPath,
        path: tagPath,
        name: route.name,
        meta: {...route.meta}
      })
    }
    if (route.children) {
      const tempTags = filterAffixTags(route.children, route.path)
      if (tempTags.length >= 1) {
        tags = [...tags, ...tempTags]
      }
    }
  })
  return tags
}

function initTags() {
  if (tagsViewPersist.value) {
    useTagsViewStore().loadPersistedViews()
  }
  const res = filterAffixTags(routes.value)
  affixTags.value = res
  for (const tag of res) {
    if (tag.name) {
      useTagsViewStore().addAffixView(tag)
    }
  }
}

function addTags() {
  const {name} = route
  if (name) {
    useTagsViewStore().addView(route)
  }
}

function moveToCurrentTag() {
  nextTick(() => {
    for (const r of visitedViews.value) {
      if (r.path === route.path) {
        scrollPaneRef.value.moveToTarget(r)
        if (r.fullPath !== route.fullPath) {
          useTagsViewStore().updateVisitedView(route)
        }
      }
    }
  })
}

function scrollLeft() {
  if (!canScrollLeft.value) return
  scrollPaneRef.value.scrollToStart()
}

function scrollRight() {
  if (!canScrollRight.value) return
  scrollPaneRef.value.scrollToEnd()
}

function updateArrowState() {
  nextTick(() => {
    if (scrollPaneRef.value) {
      const state = scrollPaneRef.value.getScrollState()
      canScrollLeft.value = state.canLeft
      canScrollRight.value = state.canRight
    }
  })
}

function toggleFullscreen() {
  const mainContainer = document.querySelector('.main-container')
  const navbar = document.querySelector('.navbar')
  const sidebar = document.querySelector('.sidebar-container')
  if (!mainContainer) return

  if (!isFullscreen.value) {
    mainContainer.classList.add('fullscreen-mode')
    document.body.style.overflow = 'hidden'
    const elementsToHide = [{el: navbar, originalDisplay: navbar?.style.display || ''}, {
      el: sidebar,
      originalDisplay: sidebar?.style.display || ''
    }]
    elementsToHide.forEach(item => {
      if (item.el && item.el.style.display !== 'none') {
        item.originalDisplay = item.el.style.display
        item.el.style.display = 'none'
        hiddenElements.value.push(item)
      }
    })
    isFullscreen.value = true
  } else {
    mainContainer.classList.remove('fullscreen-mode')
    document.body.style.overflow = ''
    hiddenElements.value.forEach(item => {
      if (item.el) {
        item.el.style.display = item.originalDisplay
      }
    })
    hiddenElements.value = []
    document.querySelector('.tags-action-btn')?.blur()
    isFullscreen.value = false
  }
}

function handleDropdownCommand(command) {
  const tag = selectedDropdownTag.value
  selectedTag.value = tag
  switch (command) {
    case 'refresh':
      refreshSelectedTag(tag);
      break
    case 'fullscreen':
      toggleFullscreen();
      break
    case 'close':
      closeSelectedTag(tag);
      break
    case 'closeOthers':
      closeOthersTags();
      break
    case 'closeLeft':
      closeLeftTags();
      break
    case 'closeRight':
      closeRightTags();
      break
    case 'closeAll':
      closeAllTags(tag);
      break
  }
}

function refreshSelectedTag(view) {
  proxy.$tab.refreshPage(view)
  if (route.meta.link) {
    useTagsViewStore().delIframeView(route)
  }
}

function closeSelectedTag(view) {
  if (isFavorite(view)) {
    ElMessageBox.confirm('该标签页已收藏，确定要关闭吗？', '提示', {
      confirmButtonText: '确定关闭', cancelButtonText: '取消', type: 'warning'
    }).then(() => doCloseSelectedTag(view)).catch(() => {})
    return
  }
  doCloseSelectedTag(view)
}

function doCloseSelectedTag(view) {
  proxy.$tab.closePage(view).then(({visitedViews}) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}

function closeRightTags() {
  proxy.$tab.closeRightPage(selectedTag.value).then(visitedViews => {
    if (!visitedViews.find(i => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}

function closeLeftTags() {
  proxy.$tab.closeLeftPage(selectedTag.value).then(visitedViews => {
    if (!visitedViews.find(i => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}

function closeOthersTags() {
  router.push(selectedTag.value).catch(() => {
  })
  proxy.$tab.closeOtherPage(selectedTag.value).then(() => {
    moveToCurrentTag()
  })
}

function closeAllTags(view) {
  proxy.$tab.closeAllPage().then(({visitedViews}) => {
    if (affixTags.value.some(tag => tag.path === route.path)) {
      return
    }
    toLastView(visitedViews, view)
  })
}

function toLastView(visitedViews, view) {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.fullPath)
  } else {
    router.push('/').then(addTags)
  }
}

function openMenu(tag, e) {
  left.value = e.clientX
  top.value = e.clientY
  visible.value = true
  selectedTag.value = tag
}

function closeMenu() {
  visible.value = false
}

function handleScroll() {
  closeMenu()
  updateArrowState()
}
</script>

<style lang="scss" scoped>
$tags-bar-height: var(--app-tags-height);

.tags-view-container {
  height: $tags-bar-height;
  width: 100%;
  background: var(--navigation-bg);
  font-family: var(--navigation-font);
  border-bottom: 1px solid var(--navigation-line);
  display: flex;
  align-items: center;
  padding: 0 var(--navigation-page-padding);
  position: relative;
  z-index: 5;
  overflow: hidden;

  $btn-width: 32px;
  $btn-color: var(--navigation-text);
  $btn-hover-bg: var(--el-fill-color-light);
  $btn-hover-color: var(--el-text-color-primary);
  $btn-disabled-color: var(--el-text-color-placeholder);
  $divider: 1px solid var(--navigation-line);

  button { padding: 0; border: 0; background: transparent; font: inherit; }
  button:focus-visible, .tags-view-item:focus-visible { outline: 2px solid var(--el-color-primary); outline-offset: -2px; }

  .tags-nav-btn {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: $btn-width;
    height: $tags-bar-height;
    cursor: pointer;
    color: $btn-color;
    font-size: 13px;
    user-select: none;
    transition: background 0.15s, color 0.15s;

    &:hover:not(.disabled) {
      background: $btn-hover-bg;
      color: $btn-hover-color;
    }

    &.disabled {
      color: $btn-disabled-color;
      cursor: not-allowed;
    }

    &--left {
      border-right: $divider;
    }

    &--right {
      border-left: $divider;
    }
  }

  .tags-view-wrapper {
    flex: 1;
    min-width: 0;
    height: 100%;

    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      gap: 6px;
      height: 32px;
      line-height: 1;
      border: 1px solid transparent;
      color: var(--navigation-text);
      background: transparent;
      padding: 0 10px;
      font-size: 13px;
      margin: 0 6px 0 0;
      border-radius: var(--navigation-radius) var(--navigation-radius) 0 0;
      flex-shrink: 0;
      font-weight: 500;
      text-decoration: none;
      vertical-align: middle;
      padding-top: 0;

      &:hover { background: var(--navigation-hover-bg); color: var(--navigation-hover-text); }

      &:first-of-type {
        margin-left: 0;
      }

      &:last-of-type {
        margin-right: 6px;
      }
    }
  }

  &:not(.tags-view-container--chrome) .tags-view-wrapper .tags-view-item.active {
    background-color: var(--navigation-active-bg);
    color: var(--navigation-active-text);
    font-weight: 600;
    box-shadow: inset 0 -2px var(--el-color-primary);

    &::before {
      content: none;
    }
  }

  &:not(.tags-view-container--chrome) .tags-view-wrapper .tags-view-item.active.has-icon::before {
    content: none !important;
  }

  .tags-action-dropdown {
    flex-shrink: 0;
    display: flex;
    align-items: center;
  }

  .tags-action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: $btn-width;
    height: $tags-bar-height;
    cursor: pointer;
    color: $btn-color;
    font-size: 13px;
    border-left: $divider;
    user-select: none;
    transition: background 0.15s, color 0.15s;

    &:hover {
      background: $btn-hover-bg;
      color: $btn-hover-color;
    }
  }

  .contextmenu {
    margin: 0;
    background: var(--navigation-bg);
    z-index: 3000;
    position: fixed;
    list-style-type: none;
    padding: 4px;
    border-radius: 14px;
    font-size: 13px;
    font-weight: 500;
    color: var(--navigation-text);
    box-shadow: var(--navigation-popup-shadow);
    border: 1px solid var(--navigation-line);
    min-width: 140px;

    li {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;
      padding: 7px 12px;
      border-radius: 8px;
      cursor: pointer;

      &:hover {
        background: var(--navigation-hover-bg);
        color: var(--navigation-active-text);
      }
    }
  }

  &.tags-view-container--chrome {
    --chrome-strip-bg: var(--navigation-bg);
    --chrome-strip-border: var(--el-border-color-lighter, #e4e7ed);
    --chrome-tab-active-bg: var(--el-color-primary-light-9);
    --chrome-tab-text: var(--el-text-color-regular, #606266);
    --chrome-tab-text-active: var(--el-color-primary);
    --chrome-wing-r: 10px;

    overflow: visible;
    background: var(--chrome-strip-bg);
    border-bottom: 1px solid var(--chrome-strip-border);
    align-items: flex-end;

    .tags-nav-btn {
      align-self: stretch;
      height: auto;
      min-height: $tags-bar-height;
      border-color: var(--chrome-strip-border);
    }

    .tags-action-btn {
      border-color: var(--chrome-strip-border);
    }

    .tags-view-wrapper {
      .tags-view-item {
        display: inline-flex !important;
        align-items: center;
        justify-content: center;
        position: relative;
        z-index: 1;
        height: 30px;
        min-height: 30px;
        margin: 0 0 -1px;
        padding: 0 12px;
        font-size: 13px;
        font-weight: 400;
        line-height: 1.2;
        border: none !important;
        border-radius: 0;
        background: transparent !important;
        color: var(--chrome-tab-text);
        padding-top: 0 !important;
        box-shadow: none !important;
        transition: background 0.12s ease, color 0.12s ease, border-radius 0.12s ease;

        &::before,
        &::after {
          content: '' !important;
          display: block !important;
          position: absolute;
          bottom: 0;
          width: var(--chrome-wing-r);
          height: var(--chrome-wing-r);
          margin: 0 !important;
          pointer-events: none;
          background: transparent !important;
          border-radius: 0 !important;
          transition: box-shadow 0.12s ease;
        }

        &::before {
          left: calc(-1 * var(--chrome-wing-r));
          border-bottom-right-radius: var(--chrome-wing-r) !important;
          box-shadow: none;
        }

        &::after {
          right: calc(-1 * var(--chrome-wing-r));
          border-bottom-left-radius: var(--chrome-wing-r) !important;
          box-shadow: none;
        }

        &:first-of-type {
          margin-left: 6px;
        }

        &:last-of-type {
          margin-right: 10px;
        }

        &:not(.active) + .tags-view-item:not(.active) {
          border-left: 1px solid var(--el-border-color-lighter, #e4e7ed);
          padding-left: 11px;
        }

        &:hover:not(.active) {
          background: var(--el-fill-color-light, #f5f7fa) !important;
          border-radius: 6px 6px 0 0;
          color: var(--el-text-color-primary, #303133);
        }

        &.active {
          height: 31px;
          min-height: 31px;
          padding: 0 14px;
          color: var(--chrome-tab-text-active) !important;
          font-weight: 500;
          background: var(--chrome-tab-active-bg) !important;
          border: none !important;
          border-radius: var(--chrome-wing-r) var(--chrome-wing-r) 0 0;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);

          &::before {
            box-shadow: calc(var(--chrome-wing-r) * 0.5) calc(var(--chrome-wing-r) * 0.5) 0 calc(var(--chrome-wing-r) * 0.5) var(--chrome-tab-active-bg);
          }

          &::after {
            box-shadow: calc(var(--chrome-wing-r) * -0.5) calc(var(--chrome-wing-r) * 0.5) 0 calc(var(--chrome-wing-r) * 0.5) var(--chrome-tab-active-bg);
          }
        }
      }
    }
  }
}
</style>

<style lang="scss">
.tags-view-wrapper {
  .tags-view-item {
    .tab-star {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 18px;
      height: 18px;
      flex-shrink: 0;
      opacity: 0;
      color: var(--navigation-text);
      transition: opacity 150ms ease;
      cursor: pointer;

      &.is-favorite { opacity: 1; color: var(--navigation-star-color); }
      &:focus-visible { opacity: 1; outline: 2px solid var(--el-color-primary); }
      .el-icon { font-size: 14px; }
    }
    &:hover .tab-star:not(.is-favorite) { opacity: 0.6; }
    .tab-label { line-height: 1; }
    .tags-close-btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 18px;
      height: 18px;
      margin-left: 0;
      border-radius: 4px;
      color: var(--navigation-text);
      transition: background-color 150ms ease, color 150ms ease;
      cursor: pointer;

      .el-icon-close {
        width: 1em;
        height: 1em;
        vertical-align: 0;
        line-height: 1;
        display: inline-flex;
        align-items: center;
        justify-content: center;
      }

      &:hover {
        background-color: var(--navigation-hover-bg);

        .el-icon-close {
          color: var(--navigation-secondary);
        }
      }
    }
  }
}

/* 页签全屏模式样式 */
.main-container.fullscreen-mode {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin-left: 0 !important;
  transition: none !important;
}

.main-container.fullscreen-mode .fixed-header {
  display: block !important;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100% !important;
  z-index: 1000;
  transition: none !important;
}

.main-container.fullscreen-mode .fixed-header .navbar {
  display: none !important;
}

.main-container.fullscreen-mode .app-main {
  position: fixed;
  top: var(--app-tags-height);
  left: 0;
  right: 0;
  bottom: 0;
  margin: 0 !important;
  padding: 0 !important;
  height: calc(100vh - var(--app-tags-height)) !important;
  min-height: calc(100vh - var(--app-tags-height)) !important;
  overflow: auto;
}
</style>
