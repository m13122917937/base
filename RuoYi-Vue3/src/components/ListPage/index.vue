<template>
  <div class="list-page">
    <div class="list-page__content" tabindex="0" aria-label="列表内容">
      <slot/>
    </div>
    <div v-if="$slots.footer" class="list-page__footer">
      <slot name="footer"/>
    </div>
  </div>
</template>

<style scoped lang="scss">
.list-page {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  min-height: 0;
  min-width: 0;
  overflow: hidden;
}

.list-page__content {
  flex: 1;
  min-height: 0;
  min-width: 0;
  overflow: auto;
  overscroll-behavior: contain;

  &:focus-visible {
    outline: 2px solid var(--el-color-primary);
    outline-offset: -2px;
  }
}

.list-page__footer {
  flex-shrink: 0;
  min-width: 0;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-light);
  background: var(--workspace-surface, var(--el-bg-color));

  :deep(.pagination-container) {
    margin: 0;
  }
}
</style>
