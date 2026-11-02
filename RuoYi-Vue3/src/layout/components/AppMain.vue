<template>
  <section class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition name="fade-transform" mode="out-in">
        <keep-alive :include="tagsViewStore.cachedViews">
          <component v-if="!route.meta.link" :is="Component" :key="route.path"/>
        </keep-alive>
      </transition>
    </router-view>
    <iframe-toggle/>
    <copyright/>
  </section>
</template>

<script setup>
import copyright from "./Copyright/index"
import iframeToggle from "./IframeToggle/index"
import useTagsViewStore from '@/store/modules/tagsView'

const route = useRoute()
const tagsViewStore = useTagsViewStore()

onMounted(() => {
  addIframe()
})

watchEffect(() => {
  addIframe()
})

function addIframe() {
  if (route.meta.link) {
    useTagsViewStore().addIframeView(route)
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  background: var(--workspace-canvas);
  /* 高度与导航、页签共用同一组尺寸变量。 */
  min-height: calc(100vh - var(--app-navbar-height));
  width: 100%;
  position: relative;
  overflow: hidden;
}

.fixed-header + .app-main {
  overflow-y: auto;
  scrollbar-gutter: auto;
  height: calc(100vh - var(--app-navbar-height));
  min-height: 0px;
}

.app-main:has(.copyright) {
  padding-bottom: 36px;
}

.fixed-header + .app-main {
  margin-top: var(--app-navbar-height);
}

.hasTagsView {
  .app-main {
    /* 导航与页签共同占用的高度。 */
    min-height: calc(100vh - var(--app-navbar-height) - var(--app-tags-height));
  }

  .fixed-header + .app-main {
    margin-top: calc(var(--app-navbar-height) + var(--app-tags-height));
    height: calc(100vh - var(--app-navbar-height) - var(--app-tags-height));
    min-height: 0px;
  }
}

/* 移动端fixed-header优化 */
@media screen and (max-width: 991px) {
  .fixed-header + .app-main {
    padding-bottom: max(60px, calc(constant(safe-area-inset-bottom) + 40px));
    padding-bottom: max(60px, calc(env(safe-area-inset-bottom) + 40px));
    overscroll-behavior-y: none;
  }

  .hasTagsView .fixed-header + .app-main {
    padding-bottom: max(60px, calc(constant(safe-area-inset-bottom) + 40px));
    padding-bottom: max(60px, calc(env(safe-area-inset-bottom) + 40px));
    overscroll-behavior-y: none;
  }
}

@supports (-webkit-touch-callout: none) {
  @media screen and (max-width: 991px) {
    .fixed-header + .app-main {
      padding-bottom: max(17px, calc(constant(safe-area-inset-bottom) + 10px));
      padding-bottom: max(17px, calc(env(safe-area-inset-bottom) + 10px));
      height: calc(100svh - var(--app-navbar-height));
      height: calc(100dvh - var(--app-navbar-height));
    }

    .hasTagsView .fixed-header + .app-main {
      padding-bottom: max(17px, calc(constant(safe-area-inset-bottom) + 10px));
      padding-bottom: max(17px, calc(env(safe-area-inset-bottom) + 10px));
      height: calc(100svh - var(--app-navbar-height) - var(--app-tags-height));
      height: calc(100dvh - var(--app-navbar-height) - var(--app-tags-height));
    }
  }
}
/* 列表页占满工作区，由列表内容滚动，底部分页始终留在视口内。 */
.hasTagsView .app-main {
  --list-page-header-height: calc(var(--app-navbar-height) + var(--app-tags-height));
}

.app-main:has(> .list-page),
.hasTagsView .app-main:has(> .list-page) {
  display: flex;
  flex-direction: column;
  height: calc(100dvh - var(--list-page-header-height, var(--app-navbar-height)));
  min-height: 0;
  overflow: hidden;
  padding-bottom: env(safe-area-inset-bottom, 0px);

  > .list-page {
    flex: 1;
  }

  &:has(.copyright) {
    padding-bottom: calc(36px + env(safe-area-inset-bottom, 0px));
  }
}
</style>

<style lang="scss">
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}
</style>
