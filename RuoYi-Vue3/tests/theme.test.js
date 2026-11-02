import test from 'node:test'
import assert from 'node:assert/strict'
import {getThemePairedColor} from '../src/utils/theme.js'

test('切换显示模式使用参考项目的成对主题色，切回浅色恢复原预设', () => {
  const pairs = [['#007AFF', '#0A84FF'], ['#34C759', '#30D158'], ['#FF9500', '#FF9F0A'], ['#FF3B30', '#FF453A'], ['#AF52DE', '#BF5AF2'], ['#5AC8FA', '#64D2FF'], ['#FF2D55', '#FF375F'], ['#8E8E93', '#98989D']]
  for (const [light, dark] of pairs) {
    assert.equal(getThemePairedColor(light.toLowerCase(), 'light', 'dark'), dark)
    assert.equal(getThemePairedColor(dark.toLowerCase(), 'dark', 'light'), light)
  }
})

test('自定义主题在深色模式提亮，在浅色模式降低亮度，非法缓存使用默认蓝色', () => {
  assert.equal(getThemePairedColor('#123456', 'light', 'dark'), '#4d6780')
  assert.equal(getThemePairedColor('#123456', 'dark', 'light'), '#102e4c')
  assert.equal(getThemePairedColor('invalid', 'light', 'dark'), '#0A84FF')
})
