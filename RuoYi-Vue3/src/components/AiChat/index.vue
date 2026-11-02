<template>
  <div class="ai-chat">
    <el-popover v-model:visible="guideVisible" trigger="manual" placement="bottom" :width="268"
      popper-class="ai-assistant-guide" transition="fade-in-linear">
      <section class="guide-content" :aria-label="assistantName + '使用引导'" @keydown.esc.stop.prevent="dismissGuide">
        <div class="guide-header">
          <h3>你的{{ assistantName }}已就位</h3>
          <button type="button" class="icon-button guide-close" :aria-label="'关闭' + assistantName + '引导'" @click="dismissGuide">
            <el-icon aria-hidden="true"><Close/></el-icon>
          </button>
        </div>
        <p>你好，我是{{ assistantName }}。<br>点击上方入口，开始对话。</p>
      </section>
      <template #reference>
      <button ref="launcher" type="button" class="chat-launcher" :class="{ 'is-open': visible }"
        :title="assistantName" :aria-label="(visible ? '关闭' : '打开') + assistantName" :aria-expanded="String(visible)"
        :aria-controls="panelId" @click="toggle" @keydown.esc.stop.prevent="dismissGuide">
        <span class="launcher-logo"><img :src="robotLogo" class="robot-logo" alt="" aria-hidden="true" draggable="false"/></span>
        <span class="launcher-label">{{ assistantName }}</span>
        <span v-if="unread && !visible" class="unread-dot" aria-label="有新回复" />
      </button>
      </template>
    </el-popover>

    <el-drawer v-model="visible" direction="rtl" size="min(420px, 100vw)" :title="assistantName + '对话'"
      append-to-body :with-header="false" class="header-ai-drawer" @opened="focusComposer">
      <section :id="panelId" class="chat-panel">
        <header class="chat-header">
          <div class="chat-brand">
            <span class="brand-logo"><img :src="robotLogo" class="robot-logo" alt="" aria-hidden="true" draggable="false"/></span>
            <div><h2>{{ assistantName }} <span class="ai-label">AI</span></h2><p>{{ replyHandler ? '有什么想法，都可以聊聊' : '你的工作搭档' }}</p></div>
          </div>
          <div class="header-actions">
            <el-tooltip content="清空对话" placement="top">
              <button type="button" class="icon-button" aria-label="清空对话" :disabled="!messages.length" @click="clearConversation">
                <el-icon aria-hidden="true"><Delete/></el-icon>
              </button>
            </el-tooltip>
            <button type="button" class="icon-button" aria-label="关闭对话" @click="close">
              <el-icon aria-hidden="true"><Close/></el-icon>
            </button>
          </div>
        </header>

        <div ref="messageList" class="chat-content" role="log" aria-live="polite" aria-relevant="additions text" :aria-busy="sending">
          <div v-if="!messages.length" class="chat-welcome">
            <div class="welcome-logo"><img :src="robotLogo" class="robot-logo" alt="" aria-hidden="true" draggable="false"/></div>
            <span class="welcome-eyebrow">LET'S TALK</span>
            <h3>你好，有什么可以帮你？</h3>
            <p>一个问题、一个想法，都可以成为对话的开始。</p>
            <div class="prompt-list">
              <button v-for="prompt in prompts" :key="prompt" type="button" @click="usePrompt(prompt)">
                <el-icon aria-hidden="true"><ChatLineRound/></el-icon><span>{{ prompt }}</span><el-icon aria-hidden="true"><TopRight/></el-icon>
              </button>
            </div>
          </div>

          <div v-for="message in messages" :key="message.id" class="chat-message" :class="'is-' + message.role">
            <span v-if="message.role === 'assistant'" class="message-avatar"><img :src="robotLogo" class="robot-logo" alt="" aria-hidden="true" draggable="false"/></span>
            <div class="message-body">
              <span class="message-name">{{ message.role === 'user' ? '你' : assistantName }}</span>
              <div class="message-bubble" :class="{ 'is-error': message.status === 'error' }">
                <span v-if="message.status === 'pending'" class="reply-pending"><el-icon aria-hidden="true"><Loading/></el-icon> 正在思考…</span>
                <template v-else>{{ message.content }}</template>
              </div>
              <button v-if="message.status === 'error'" type="button" class="retry-button" :disabled="sending" @click="retry(message)">
                <el-icon aria-hidden="true"><RefreshRight/></el-icon> 重新获取回复
              </button>
            </div>
          </div>
        </div>

        <footer class="chat-footer">
          <div v-if="!replyHandler" class="preview-notice"><span class="preview-dot" />界面预览 · AI 服务尚未接入</div>
          <div class="composer" :class="{ 'is-composing': !!draft.trim() }">
            <label class="sr-only" :for="inputId">输入消息</label>
            <el-input :id="inputId" ref="composer" v-model="draft" type="textarea" :rows="2" :maxlength="2000"
              resize="none" placeholder="说说你想聊什么…" :disabled="sending" @keydown="onInputKeydown" />
            <div class="composer-toolbar">
              <span>{{ draft.length }}/2000</span>
              <el-button type="primary" size="small" :disabled="sending || !draft.trim()" :loading="sending" @click="send">
                发送 <el-icon v-if="!sending" aria-hidden="true"><Top/></el-icon>
              </el-button>
            </div>
          </div>
          <p class="composer-hint">Enter 发送 · Shift + Enter 换行<span>对话仅在当前页面保留</span></p>
        </footer>
      </section>
    </el-drawer>
  </div>
</template>

<script setup name="AiChat">
import {useId} from 'vue'
import {claimAssistantGuide} from '@/utils/aiAssistantGuide'
import robotLogo from '@/assets/images/ai-agent-logo-ui.png'
import defaultSettings from '@/settings'

const assistantName = defaultSettings.assistantName

const props = defineProps({replyHandler: {type: Function, default: null}})
const visible = ref(false)
const guideVisible = ref(false)
const draft = ref('')
const messages = ref([])
const sending = ref(false)
const unread = ref(false)
const launcher = ref(null)
const composer = ref(null)
const messageList = ref(null)
const id = useId()
const panelId = 'ai-chat-panel-' + id
const inputId = 'ai-chat-input-' + id
const prompts = ['帮我推单', '帮我查账单', '我想看看昨天的经营情况']
let sequence = 0
let requestVersion = 0
let abortController = null
let alive = true

function focusComposer() {
  nextTick(() => {
    if (alive && visible.value) composer.value?.focus()
  })
}

function toggle() {
  guideVisible.value = false
  if (visible.value) return close()
  visible.value = true
  unread.value = false
  focusComposer()
}

function close() {
  visible.value = false
  nextTick(() => { if (alive) launcher.value?.focus() })
}

function dismissGuide() {
  guideVisible.value = false
  nextTick(() => { if (alive) launcher.value?.focus() })
}

function usePrompt(prompt) {
  draft.value = prompt
  focusComposer()
}

function onInputKeydown(event) {
  if (event.key !== 'Enter' || event.shiftKey || event.isComposing || event.keyCode === 229) return
  event.preventDefault()
  send()
}

function addMessage(role, content, extra = {}) {
  const message = {id: ++sequence, role, content, status: 'done', preview: false, ...extra}
  messages.value.push(message)
  return messages.value[messages.value.length - 1]
}

async function send() {
  const content = draft.value.trim()
  if (!content || sending.value) return
  const user = addMessage('user', content)
  draft.value = ''
  const reply = addMessage('assistant', '', {replyTo: user.id})
  if (!props.replyHandler) {
    reply.content = '已在当前页面记录你的消息。AI 服务尚未接入，暂时无法生成智能回复；接入后，你可以直接在这里继续对话。'
    reply.preview = true
    focusComposer()
    return
  }
  await requestReply(user, reply)
}

async function retry(reply) {
  if (sending.value || !props.replyHandler || reply.status !== 'error') return
  const user = messages.value.find(message => message.id === reply.replyTo)
  if (user) await requestReply(user, reply)
}

async function requestReply(user, reply) {
  const version = ++requestVersion
  const index = messages.value.findIndex(message => message.id === user.id)
  const history = messages.value.slice(0, index).filter(message => message.status === 'done' && !message.preview)
    .slice(-20).map(({role, content}) => ({role, content}))
  abortController = new AbortController()
  sending.value = true
  reply.status = 'pending'
  reply.content = ''
  try {
    const content = await props.replyHandler({message: user.content, history, signal: abortController.signal})
    if (!alive || version !== requestVersion) return
    if (typeof content !== 'string' || !content.trim()) throw new Error('Empty reply')
    reply.content = content.trim().slice(0, 20000)
    reply.status = 'done'
    unread.value = !visible.value
  } catch {
    if (!alive || version !== requestVersion) return
    reply.content = '暂时未能获取回复，请稍后重试。'
    reply.status = 'error'
  } finally {
    if (alive && version === requestVersion) {
      sending.value = false
      abortController = null
      focusComposer()
    }
  }
}

function cancelReply() {
  requestVersion++
  abortController?.abort()
  abortController = null
  sending.value = false
}

function clearConversation() {
  cancelReply()
  messages.value = []
  draft.value = ''
  unread.value = false
  focusComposer()
}

watch(messages, () => nextTick(() => {
  if (messageList.value) messageList.value.scrollTop = messageList.value.scrollHeight
}), {deep: true})
onMounted(() => { guideVisible.value = claimAssistantGuide() })
onBeforeUnmount(() => { alive = false; cancelReply() })
</script>
<style scoped lang="scss">
.robot-logo { display: block; width: 100%; height: 100%; object-fit: contain; }
.ai-chat { display: inline-flex; flex: none; }
.chat-launcher {
  position: relative; display: inline-flex; align-items: center; justify-content: center; gap: 10px;
  width: 132px; height: 40px; padding: 0 10px; margin-right: var(--navigation-page-padding); box-sizing: border-box;
  color: var(--header-ai-ink); background: linear-gradient(110deg, var(--header-ai-start), var(--header-ai-end));
  border: 1px solid var(--header-ai-border); border-radius: 10px; box-shadow: var(--header-card-shadow);
  font-family: var(--navigation-font); cursor: pointer; transition: filter .2s, box-shadow .2s;
  &:hover, &.is-open { filter: brightness(1.04); box-shadow: var(--navigation-popup-shadow); }
  &:focus-visible { outline: 3px solid rgba(var(--navigation-primary-rgb), .18); outline-offset: 3px; }
}
.launcher-logo { flex: none; width: 32px; height: 32px; padding: 1px; box-sizing: border-box; border-radius: var(--navigation-radius); background: var(--header-ai-icon-bg); }
.launcher-label { font-size: 16px; font-weight: 600; white-space: nowrap; }
.guide-header { display: flex; align-items: center; justify-content: space-between; gap: 6px; }
.guide-header h3 { margin: 0; color: var(--navigation-heading); font-size: 16px; font-weight: 600; }
.guide-header .guide-close { flex: none; width: 24px; height: 24px; }
.guide-content { font-family: var(--navigation-font); }
.guide-content p { margin: 10px 0 0; color: var(--header-tertiary); font-size: 13px; line-height: 1.8; }
.unread-dot { position: absolute; top: 3px; right: 3px; width: 10px; height: 10px; background: var(--el-color-primary); border: 2px solid var(--navigation-bg); border-radius: 50%; }
.header-ai-drawer :deep(.el-drawer) { background: var(--navigation-bg); box-shadow: var(--navigation-popup-shadow); }
.header-ai-drawer :deep(.el-drawer__body) { min-height: 0; overflow: hidden; }
.chat-panel {
  display: flex; flex-direction: column; width: 100%; height: 100vh; height: 100dvh;
  color: var(--header-text); background: var(--navigation-bg); overflow: hidden; font-family: var(--navigation-font);
}
.chat-header { display: flex; align-items: center; justify-content: space-between; gap: 10px; padding: 16px 18px; border-bottom: 1px solid var(--navigation-line); }
.chat-brand { display: flex; align-items: center; gap: 10px; min-width: 0; }
.brand-logo, .message-avatar { flex: none; color: var(--el-color-primary); background: var(--navigation-active-bg); border-radius: 10px; }
.brand-logo { width: 42px; height: 42px; padding: 4px; }
.chat-brand h2 { display: flex; align-items: center; gap: 8px; margin: 0; color: var(--navigation-heading); font-size: 16px; font-weight: 600; }
.chat-brand p { margin: 5px 0 0; color: var(--header-tertiary); font-size: 12px; }
.ai-label { color: var(--el-color-primary); background: var(--navigation-active-bg); border: 1px solid rgba(var(--navigation-primary-rgb), .16); border-radius: 4px; padding: 2px 4px; font-size: 9px; letter-spacing: 1px; }
.header-actions { display: flex; gap: 2px; }
.icon-button { width: 30px; height: 30px; padding: 0; color: var(--header-tertiary); background: transparent; border: 0; border-radius: var(--navigation-radius); font-size: 16px; cursor: pointer; }
.icon-button:hover { color: var(--el-color-primary); background: var(--navigation-active-bg); }
.icon-button:disabled { color: var(--header-disabled); cursor: default; background: transparent; }
.chat-content { flex: 1; min-height: 0; padding: 20px 18px; overflow-y: auto; background: var(--header-canvas); overscroll-behavior: contain; }
.chat-welcome { text-align: center; padding: 4px 6px; }
.welcome-logo { width: 68px; height: 68px; margin: 0 auto 8px; padding: 6px; color: var(--el-color-primary); border: 1px solid rgba(var(--navigation-primary-rgb), .14); border-radius: 23px; background: linear-gradient(140deg, rgba(var(--navigation-primary-rgb), .12), var(--navigation-bg)); }
.welcome-eyebrow { display: block; color: var(--el-color-primary); font-size: 10px; letter-spacing: 3px; }
.chat-welcome h3 { margin: 10px 0 8px; color: var(--navigation-heading); font-size: 21px; font-weight: 600; }
.chat-welcome p { margin: 0; color: var(--header-tertiary); font-size: 13px; line-height: 1.8; }
.prompt-list { display: grid; gap: 9px; margin-top: 18px; }
.prompt-list button { display: flex; align-items: center; gap: 9px; padding: 11px 12px; color: var(--navigation-secondary); background: var(--navigation-bg); border: 1px solid var(--navigation-line); border-radius: 10px; text-align: left; cursor: pointer; transition: border-color .2s, color .2s; }
.prompt-list button:hover { color: var(--el-color-primary); border-color: rgba(var(--navigation-primary-rgb), .4); }
.prompt-list button span { flex: 1; }
.chat-message { display: flex; gap: 8px; align-items: flex-start; margin-bottom: 18px; }
.message-avatar { width: 30px; height: 30px; padding: 3px; }
.message-body { min-width: 0; max-width: calc(100% - 38px); }
.message-name { display: block; margin-bottom: 6px; color: var(--header-tertiary); font-size: 11px; }
.message-bubble { padding: 11px 13px; color: var(--header-text); background: var(--navigation-bg); border: 1px solid var(--navigation-line); border-radius: 2px 13px 13px 13px; white-space: pre-wrap; overflow-wrap: anywhere; font-size: 13px; line-height: 1.75; }
.is-user { justify-content: flex-end; }
.is-user .message-name { text-align: right; }
.is-user .message-bubble { border-color: rgba(var(--navigation-primary-rgb), .16); background: var(--navigation-active-bg); border-radius: 13px 2px 13px 13px; }
.is-error { border-color: var(--el-color-warning); }
.reply-pending { color: var(--header-tertiary); }
.retry-button { margin-top: 7px; padding: 0; color: var(--el-color-primary); background: transparent; border: 0; font-size: 12px; cursor: pointer; }
.retry-button:disabled { color: var(--header-disabled); cursor: default; }
.chat-footer { flex: none; padding: 12px 16px calc(10px + env(safe-area-inset-bottom, 0px)); border-top: 1px solid var(--navigation-line); background: var(--navigation-bg); }
.preview-notice { display: flex; align-items: center; gap: 6px; margin-bottom: 9px; color: var(--header-tertiary); font-size: 11px; }
.preview-dot { width: 5px; height: 5px; background: var(--header-tertiary); border-radius: 50%; }
.composer { padding: 2px 6px 7px; border: 1px solid var(--navigation-line); border-radius: 10px; background: var(--navigation-bg); transition: border-color .2s; }
.composer:focus-within, .composer.is-composing { border-color: rgba(var(--navigation-primary-rgb), .55); }
.composer :deep(.el-textarea__inner) { min-height: 48px !important; padding: 9px 5px 3px; border: 0 !important; box-shadow: none !important; color: var(--header-text); background: transparent !important; font-size: 13px; line-height: 1.6; }
.composer-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 8px; padding-left: 5px; }
.composer-toolbar > span { color: var(--header-disabled); font-size: 10px; }
.composer-toolbar .el-button { border-radius: var(--navigation-radius); }
.composer-hint { display: flex; justify-content: space-between; gap: 6px; margin: 8px 0 0; color: var(--header-tertiary); font-size: 10px; line-height: 1.5; }
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0; }
:global(html.dark) .brand-logo,
:global(html.dark) .message-avatar,
:global(html.dark) .welcome-logo,
:global(html.dark) .welcome-eyebrow,
:global(html.dark) .ai-label { color: var(--el-color-primary-light-2); }
@media (max-width: 768px) {
  .chat-launcher { width: 40px; padding: 3px; margin-right: 0; }
  .launcher-label { display: none; }
}
@media (max-width: 480px) {
  .chat-header { padding: 13px 14px; }
  .chat-content { padding: 16px 12px; }
  .chat-welcome { padding-top: 4px; }
  .welcome-logo { width: 64px; height: 64px; margin-bottom: 8px; padding: 5px; border-radius: 22px; }
  .chat-welcome h3 { font-size: 19px; }
  .prompt-list { margin-top: 17px; }
  .composer-hint { flex-wrap: wrap; }
}
@media (prefers-reduced-motion: reduce) {
  .chat-launcher, .header-ai-drawer :deep(.el-drawer__container), .header-ai-drawer :deep(.el-drawer) { transition: none; animation: none; }
}
</style>

<!-- Popover 挂载到 body，浮层外壳及箭头需用独立类限定样式。 -->
<style lang="scss">
.el-popover.ai-assistant-guide {
  max-width: calc(100vw - 24px); padding: 16px; box-sizing: border-box;
  background: var(--navigation-bg); border: 1px solid var(--header-ai-border);
  border-radius: 12px; box-shadow: var(--navigation-popup-shadow);
  &[x-placement^="bottom"] .popper__arrow {
    border-bottom-color: var(--header-ai-border);
    &::after { border-bottom-color: var(--navigation-bg); }
  }
}
</style>

<style lang="scss">
.header-ai-drawer.el-drawer { background: var(--navigation-bg); box-shadow: var(--navigation-popup-shadow); }
.header-ai-drawer .el-drawer__body { min-height: 0; padding: 0; overflow: hidden; }
</style>
