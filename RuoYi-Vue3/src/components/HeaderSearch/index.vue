<template>
  <div class="header-search">
    <button type="button" class="search-trigger" aria-label="搜索菜单或功能" title="搜索菜单或功能（Ctrl+K）"
            @click.stop="openSearch">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
      <span class="search-trigger-label">搜索菜单或功能</span>
      <kbd>Ctrl+K</kbd>
    </button>
    <el-dialog
        v-model="show"
        width="min(560px, calc(100vw - 32px))"
        class="menu-search-dialog"
        title="搜索菜单或功能"
        :show-close="false"
        append-to-body
        @opened="focusSearchInput"
        @close="resetSearch"
    >
      <template #header>
        <div class="search-dialog-heading">
          <span>搜索菜单或功能</span>
          <span class="search-dialog-hint">Esc 关闭</span>
        </div>
      </template>
      <el-input
          v-model="search"
          ref="searchInputRef"
          size="large"
          prefix-icon="Search"
          placeholder="输入菜单名称、功能或路径"
          aria-label="搜索菜单或功能"
          clearable
          @keydown.enter.prevent="selectActiveResult"
          @keydown.up.prevent="navigateResult(-1)"
          @keydown.down.prevent="navigateResult(1)"
          @keydown.esc.stop="closeSearch"
      />
      <div class="search-results">
        <template v-if="keyword && results.length">
          <button
              v-for="(item, index) in results"
              :key="item.path"
              :ref="element => resultRefs[index] = element"
              type="button"
              class="result-item"
              :class="{ 'is-active': index === activeIndex }"
              @mouseenter="activeIndex = index"
              @click="selectItem(item)"
          >
            <span class="result-icon" aria-hidden="true"><el-icon><Right/></el-icon></span>
            <span class="result-content">
              <span class="result-title">{{ item.title }}</span>
              <span class="result-path">{{ item.parents.join(' / ') || '菜单' }}</span>
            </span>
          </button>
        </template>
        <div v-else class="search-empty" role="status">
          {{ keyword ? '未找到相关菜单' : '输入关键词，查询你有权限访问的菜单和功能' }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ElMessage} from 'element-plus'
import {buildMenuSearchIndex, searchMenus} from '@/utils/menuSearch'
import {getMenuLocation} from '@/utils/navigation'
import {isHttp} from '@/utils/validate'
import usePermissionStore from '@/store/modules/permission'

const show = ref(false)
const search = ref('')
const activeIndex = ref(0)
const searchInputRef = ref(null)
const resultRefs = ref([])
const router = useRouter()
const route = useRoute()
const routes = computed(() => usePermissionStore().defaultRoutes)
const searchPool = computed(() => buildMenuSearchIndex(routes.value))
const keyword = computed(() => search.value.trim())
const results = computed(() => searchMenus(searchPool.value, search.value))

function openSearch() {
  if (!show.value) resetSearch()
  show.value = true
}

function closeSearch() {
  show.value = false
}

function resetSearch() {
  search.value = ''
  activeIndex.value = 0
  resultRefs.value = []
}

function focusSearchInput() {
  searchInputRef.value?.focus()
}

async function selectItem(item) {
  let location
  try {
    location = getMenuLocation(item)
  } catch {
    ElMessage.error('菜单参数无效，请联系管理员')
    return
  }
  if (isHttp(item.path)) {
    window.open(item.path, '_blank', 'noopener,noreferrer')
  } else {
    await router.push(location)
  }
  closeSearch()
}

function navigateResult(step) {
  const count = results.value.length
  if (!count) return
  activeIndex.value = (activeIndex.value + step + count) % count
  nextTick(() => resultRefs.value[activeIndex.value]?.scrollIntoView({block: 'nearest'}))
}

function selectActiveResult() {
  const item = results.value[activeIndex.value]
  if (item) selectItem(item)
}

function handleSearchShortcut(event) {
  if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') {
    event.preventDefault()
    if (!event.repeat) openSearch()
  }
}

watch(results, () => { activeIndex.value = 0 })
watch(() => route.fullPath, closeSearch)
onMounted(() => window.addEventListener('keydown', handleSearchShortcut))
onBeforeUnmount(() => window.removeEventListener('keydown', handleSearchShortcut))
</script>

<style scoped lang="scss">
.header-search {
  display: flex;
  align-items: center;
  width: 100%;
}

.search-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  height: 36px;
  padding: 0 10px;
  border: 1px solid var(--navigation-line);
  border-radius: var(--navigation-radius);
  background: var(--navigation-search-bg);
  color: var(--navigation-secondary);
  font: inherit;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 150ms ease, border-color 150ms ease;

  .svg-icon { width: 16px; height: 16px; flex-shrink: 0; }
  .search-trigger-label { flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; text-align: left; white-space: nowrap; }
  kbd { padding: 2px 6px; border: 1px solid var(--navigation-line); border-radius: 5px; background: var(--navigation-bg); font-size: 11px; white-space: nowrap; }
  &:hover { border-color: var(--el-color-primary); background: var(--navigation-search-hover-bg); }
  &:focus-visible { outline: 2px solid var(--el-color-primary); outline-offset: 2px; }
}

.search-dialog-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 600;
}

.search-dialog-hint {
  flex-shrink: 0;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
}

.search-results {
  min-height: 160px;
  max-height: min(360px, 45vh);
  overflow-y: auto;
  overscroll-behavior: contain;
  margin-top: 12px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  min-height: 60px;
  padding: 10px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--el-text-color-primary);
  font: inherit;
  text-align: left;
  cursor: pointer;

  &:hover, &.is-active { background: var(--navigation-hover-bg); }
  &:focus-visible { outline: 2px solid var(--el-color-primary); outline-offset: -2px; }
}

.result-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  border-radius: var(--navigation-radius);
  background: var(--navigation-active-bg);
  color: var(--el-color-primary);
}

.result-content { display: flex; flex-direction: column; min-width: 0; }
.result-title, .result-path { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.result-title { color: var(--el-text-color-primary); font-size: 14px; font-weight: 500; }
.result-path { margin-top: 2px; color: var(--el-text-color-secondary); font-size: 12px; }
.search-empty { padding: 58px 16px; color: var(--el-text-color-secondary); font-size: 13px; text-align: center; }

@media (max-width: 768px) {
  .search-trigger {
    justify-content: center;
    width: 36px;
    height: 36px;
    padding: 0;
    .search-trigger-label, kbd { display: none; }
  }
}
</style>
