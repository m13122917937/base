<template>
  <div ref="containerRef" :class="{ 'hidden': hidden }" class="pagination-container">
    <el-pagination
        :background="background"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :layout="layout"
        :page-sizes="pageSizes"
        :pager-count="pagerCount"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import {scrollTo} from '@/utils/scroll-to'

const props = defineProps({
  total: {
    required: true,
    type: Number
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 50
  },
  pageSizes: {
    type: Array,
    default() {
      return [50, 100, 200]
    }
  },
  // 移动端页码按钮的数量端默认值5
  pagerCount: {
    type: Number,
    default: document.body.clientWidth < 992 ? 5 : 7
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  background: {
    type: Boolean,
    default: true
  },
  autoScroll: {
    type: Boolean,
    default: true
  },
  hidden: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits()
const containerRef = ref(null)
const currentPage = computed({
  get() {
    return props.page
  },
  set(val) {
    emit('update:page', val)
  }
})
const pageSize = computed({
  get() {
    return props.limit
  },
  set(val) {
    emit('update:limit', val)
  }
})

function handleSizeChange(val) {
  currentPage.value = 1
  emit('pagination', {page: 1, limit: val})
  resetScroll()
}

function handleCurrentChange(val) {
  emit('pagination', {page: val, limit: pageSize.value})
  resetScroll()
}

function resetScroll() {
  if (!props.autoScroll) return
  nextTick(() => {
    const panel = containerRef.value?.closest('.el-dialog, .el-drawer, .list-page')
    const contents = panel?.querySelectorAll('.list-page__content, .el-table__body-wrapper .el-scrollbar__wrap, .content-inner')
    if (contents?.length) {
      contents.forEach(content => content.scrollTo({top: 0}))
    } else {
      scrollTo(0, 800)
    }
  })
}
</script>

<style scoped>
.pagination-container {
  background: var(--el-bg-color);
}

.pagination-container.hidden {
  display: none;
}
</style>
