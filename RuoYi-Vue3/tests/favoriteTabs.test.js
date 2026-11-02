import test from 'node:test'
import assert from 'node:assert/strict'
import {readFavoriteTabs, saveFavoriteTabs, getFavoriteMenuLocations} from '../src/utils/favoriteTabs.js'

test('页签收藏独立保存到当前账户，不覆盖其他用户或布局设置', () => {
  const data = new Map([['layout-setting', '{"navType":3}']])
  const storage = {getItem: key => data.get(key) ?? null, setItem: (key, value) => data.set(key, value)}
  saveFavoriteTabs(1, ['/system/user'], storage)
  saveFavoriteTabs(2, ['/system/role'], storage)
  assert.deepEqual(readFavoriteTabs(1, storage), ['/system/user'])
  assert.deepEqual(readFavoriteTabs(2, storage), ['/system/role'])
  assert.deepEqual(readFavoriteTabs(3, storage), [])
  assert.equal(data.get('layout-setting'), '{"navType":3}')
  saveFavoriteTabs(1, [], storage)
  assert.deepEqual(readFavoriteTabs(1, storage), [])
  assert.deepEqual(readFavoriteTabs(2, storage), ['/system/role'])
})

test('损坏、非法或不可用的收藏缓存不阻止启动和本次会话操作', () => {
  for (const value of ['{', 'null', '{}', '"text"']) {
    assert.deepEqual(readFavoriteTabs(1, {getItem: () => value}), [])
  }
  assert.deepEqual(readFavoriteTabs(1, {getItem: () => '["/system/user","/system/user",3,null,"//example.com","https://example.com"]'}), ['/system/user'])
  const blocked = {getItem() {throw new Error('blocked')}, setItem() {throw new Error('blocked')}}
  assert.deepEqual(readFavoriteTabs(1, blocked), [])
  assert.doesNotThrow(() => saveFavoriteTabs(1, ['/system/user'], blocked))
  assert.deepEqual(readFavoriteTabs('', blocked), [])
})

test('收藏恢复仅使用当前授权的站内叶子菜单，排除隐藏项、目录、失效权限和错误参数', () => {
  const routes = [
    {path: '/system', meta: {title: '系统管理'}, children: [
      {path: 'user', meta: {title: '用户管理'}, query: '{"status":"0"}'},
      {path: 'secret', hidden: true, meta: {title: '隐藏页'}},
      {path: 'broken', query: '{', meta: {title: '错误参数'}}
    ]},
    {path: 'https://example.com', meta: {title: '外链'}}
  ]
  const original = structuredClone(routes)
  const paths = ['/system', '/system/user', '/system/secret', '/system/broken', '/removed', 'https://example.com']
  assert.deepEqual(getFavoriteMenuLocations(paths, routes), [{path: '/system/user', title: '用户管理', query: {status: '0'}}])
  assert.deepEqual(getFavoriteMenuLocations(paths, []), [])
  assert.deepEqual(routes, original)
})
