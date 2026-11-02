<template>
  <button type="button" class="screenfull-trigger" :aria-label="isFullscreen ? '退出全屏' : '全屏'" @click="toggle">
    <svg-icon :icon-class="isFullscreen ? 'exit-fullscreen' : 'fullscreen'"/>
  </button>
</template>

<script setup>
import {useFullscreen} from '@vueuse/core'

const {isFullscreen, toggle} = useFullscreen()
</script>

<style lang='scss' scoped>
.screenfull-trigger {
  border: 0;
  background: transparent;
  color: var(--navigation-text);
  cursor: pointer;
  &:focus-visible { outline: 3px solid var(--el-color-primary-light-7); outline-offset: 1px; }
}
.screenfull-svg {
  display: inline-block;
  cursor: pointer;
  fill: #5a5e66;
  width: 20px;
  height: 20px;
  vertical-align: 10px;
}
</style>
