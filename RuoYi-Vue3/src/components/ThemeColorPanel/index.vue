<template>
<div class="theme-color-panel"><el-popover v-model:visible="visible" trigger="click" placement="bottom-end" :width="240" :show-arrow="false" popper-class="header-theme-popup">
<template #reference><button type="button" ref="triggerRef" aria-label="主题颜色与显示模式" :aria-expanded="visible" @keydown.esc.stop.prevent="closePanel"
      class="icon-btn theme-trigger"
      :title="isDark ? '切换浅色模式' : '切换深色模式'"

    >
      <svg v-if="!isDark" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="5"/>
        <line x1="12" y1="1" x2="12" y2="3"/>
        <line x1="12" y1="21" x2="12" y2="23"/>
        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
        <line x1="1" y1="12" x2="3" y2="12"/>
        <line x1="21" y1="12" x2="23" y2="12"/>
        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
        <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
      </svg>
      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
      </svg>
      <span class="color-dot" :style="{ background: primaryColor }"></span>
    </button></template>
<section class="theme-content" @keydown.esc.stop.prevent="closePanel">
        <!-- Preset Colors -->
        <div class="section">
          <div class="section-title">预设主题色</div>
          <div class="color-grid">
            <button type="button" :aria-label="'主题色' + color" :aria-pressed="color === primaryColor"
              v-for="color in presetColors"
              :key="color"
              class="color-swatch"
              :class="{ active: color === primaryColor }"
              :style="{ background: color }"
              @click="selectColor(color)"
            >
              <svg v-if="color === primaryColor" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="20 6 9 17 4 12"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Custom Color Picker -->
        <div class="section">
          <div class="section-title">自定义颜色</div>
          <div class="custom-color-row">
            <div class="color-picker-wrap">
              <input
                type="color"
                :value="primaryColor"
                @input="selectColor($event.target.value)"
                class="color-input-native"
              />
              <span class="color-preview-fake" :style="{ background: primaryColor }"></span>
            </div>
            <span class="color-hex">{{ primaryColor }}</span>
          </div>
        </div>

        <!-- Actions -->
        <div class="section-actions">
          <button type="button" class="reset-btn" @click="resetDefault">恢复默认</button>
          <button type="button" class="mode-btn" @click="toggleThemeMode">
            <svg v-if="!isDark" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
            </svg>
            <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="5"/>
              <line x1="12" y1="1" x2="12" y2="3"/>
              <line x1="12" y1="21" x2="12" y2="23"/>
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
              <line x1="1" y1="12" x2="3" y2="12"/>
              <line x1="21" y1="12" x2="23" y2="12"/>
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
            </svg>
            <span>{{ isDark ? '浅色模式' : '深色模式' }}</span>
          </button>
        </div>
      </section>
</el-popover></div>
</template>
<script setup name="ThemeColorPanel">
import useSettingsStore from '@/store/modules/settings'
import {handleThemeStyle} from '@/utils/theme'
import {readLayoutSettings} from '@/utils/layoutSettings'

const emits = defineEmits(['theme-change'])
const settingsStore = useSettingsStore()
const visible = ref(false)
const triggerRef = ref(null)
const isDark = computed(() => settingsStore.isDark)
const primaryColor = computed(() => settingsStore.theme)
const presetColors = ['#007AFF', '#34C759', '#FF9500', '#FF3B30', '#AF52DE', '#5AC8FA', '#FF2D55', '#8E8E93']

function saveToLocal() {
  try {
    localStorage.setItem('layout-setting', JSON.stringify({...readLayoutSettings(), theme: settingsStore.theme}))
  } catch {
    // 存储不可用时仍保留本次会话的主题。
  }
}

function selectColor(color) {
  if (!/^#[\da-f]{6}$/i.test(color) || color.toUpperCase() === primaryColor.value.toUpperCase()) return
  settingsStore.theme = color.toUpperCase()
  handleThemeStyle(settingsStore.theme)
  saveToLocal()
  emits('theme-change', settingsStore.theme)
}

function resetDefault() {
  selectColor('#007AFF')
}

function toggleThemeMode() {
  settingsStore.toggleTheme()
  saveToLocal()
}

function closePanel() {
  visible.value = false
  nextTick(() => triggerRef.value?.focus())
}
</script>
<style scoped lang="scss">
.theme-color-panel {
  position: relative;
}

.theme-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: var(--navigation-text);
  cursor: pointer;
  transition: color 180ms, background-color 180ms cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;

  &:hover {
    background: var(--navigation-hover-bg);
    color: var(--navigation-hover-text);
  }
}

.color-dot {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: 1.5px solid var(--navigation-bg);
  pointer-events: none;
}

.theme-content { font-family: var(--navigation-font); }

.section {
  margin-bottom: 10px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--header-tertiary);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.color-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.color-swatch {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 150ms cubic-bezier(0.16, 1, 0.3, 1), box-shadow 150ms cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  border: 2px solid transparent;

  &:hover {
    transform: scale(1.15);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  &.active {
    border-color: var(--header-text);
    box-shadow: 0 0 0 1px var(--navigation-bg), 0 0 0 2px var(--header-text);
  }
}

.custom-color-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-picker-wrap {
  position: relative;
  width: 34px;
  height: 34px;
  overflow: hidden;
  border-radius: 8px;
  border: 1px solid var(--navigation-line);
  flex-shrink: 0;
}

.color-input-native {
  position: absolute;
  top: -6px;
  left: -6px;
  width: calc(100% + 12px);
  height: calc(100% + 12px);
  border: none;
  padding: 0;
  cursor: pointer;
  background: none;
}

.color-hex {
  font-size: 13px;
  color: var(--header-text);
  font-family: 'SF Mono', 'Fira Code', monospace;
  font-weight: 500;
}

.section-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid var(--navigation-line);
  gap: 8px;
}

.reset-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  padding: 0 12px;
  border: 1px solid var(--navigation-line);
  border-radius: 7px;
  background: transparent;
  color: var(--navigation-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: color 150ms, background-color 150ms, border-color 150ms;
  font-family: inherit;

  &:hover {
    background: var(--navigation-hover-bg);
    color: var(--header-text);
    border-color: var(--header-tertiary);
  }
}

.mode-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 30px;
  padding: 0 10px;
  border: none;
  border-radius: 7px;
  background: var(--navigation-hover-bg);
  color: var(--navigation-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: color 150ms, background-color 150ms, border-color 150ms;
  font-family: inherit;

  &:hover {
    background: var(--navigation-hover-bg);
    color: var(--header-text);
  }
}

.panel-fade-enter-active,
.panel-fade-leave-active {
  transition: opacity 150ms ease, transform 150ms ease;
}
.panel-fade-enter,
.panel-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.96);
  transform-origin: top right;
}
</style>

<style lang="scss">
.header-theme-popup.el-popper {
  padding: 10px 12px;
  max-width: calc(100vw - 24px);
  border: 1px solid var(--navigation-line);
  border-radius: 12px;
  background: var(--navigation-bg);
  color: var(--header-text);
  box-shadow: var(--navigation-popup-shadow);
  backdrop-filter: saturate(180%) blur(24px);
}
.header-theme-popup .color-swatch:focus-visible { outline: 3px solid rgba(var(--navigation-primary-rgb), .25); outline-offset: 2px; }
.theme-color-panel .theme-trigger:focus-visible { outline: 3px solid rgba(var(--navigation-primary-rgb), .18); outline-offset: 1px; }
</style>