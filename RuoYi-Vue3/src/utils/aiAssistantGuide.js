const storageKey = 'ai-assistant-guide-shown'
let claimed = false

// 仅保存当前标签页是否展示过引导，不保存用户标识或对话内容。
export function claimAssistantGuide() {
  if (claimed) return false
  claimed = true
  try {
    if (sessionStorage.getItem(storageKey) === '1') return false
    sessionStorage.setItem(storageKey, '1')
  } catch {
    // 存储不可用时，仍通过内存避免组件重建后反复展示。
  }
  return true
}

export function resetAssistantGuide() {
  claimed = false
  try {
    sessionStorage.removeItem(storageKey)
  } catch {
    // 引导标记不可用不能阻断登录或退出。
  }
}
