<template>
  <el-sub-menu v-if="item.children.length" :index="item.path" popper-class="navigation-popup"
               :show-timeout="100" :hide-timeout="160" :popper-offset="6" :teleported="true">
    <template #title>
      <svg-icon v-if="item.icon && item.icon !== '#'" :icon-class="item.icon"/>
      <span class="navigation-title" :title="item.title">{{ item.title }}</span>
    </template>
    <navigation-item v-for="child in item.children" :key="child.path" :item="child"/>
  </el-sub-menu>
  <el-menu-item v-else :index="item.path">
    <svg-icon v-if="item.icon && item.icon !== '#'" :icon-class="item.icon"/>
    <span class="navigation-title" :title="item.title">{{ item.title }}</span>
  </el-menu-item>
</template>

<script setup>
defineOptions({name: 'NavigationItem'})
defineProps({item: {type: Object, required: true}})
</script>

<style scoped lang="scss">
.navigation-title {
  min-width: 0;
  max-width: 8em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.svg-icon {
  flex-shrink: 0;
  width: 16px;
  height: 16px;
  margin-right: 0;
}
</style>
