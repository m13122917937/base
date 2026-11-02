import assert from 'node:assert/strict'
import test from 'node:test'
import {buildMenuSearchIndex, searchMenus} from '../src/utils/menuSearch.js'

test('菜单搜索只索引授权叶子，保留完整层级、绝对路径、外链和 query', () => {
  const routes = [{path: '/system', meta: {title: '系统管理'}, children: [
    {path: 'users', meta: {title: '组织与权限'}, children: [
      {path: 'list', meta: {title: '用户管理'}, query: '{"status":"0"}'},
      {path: '/roles', meta: {title: '角色管理'}},
      {path: 'hidden', hidden: true, meta: {title: '隐藏菜单'}}
    ]},
    {path: 'https://example.com/docs', meta: {title: '文档'}}
  ]}]
  const before = structuredClone(routes)
  const menus = buildMenuSearchIndex(routes)
  assert.equal(menus.length, 3)
  assert.deepEqual(menus[0], {path: '/system/users/list', title: '用户管理', parents: ['系统管理', '组织与权限'], query: '{"status":"0"}'})
  assert.equal(menus[1].path, '/roles')
  assert.equal(menus[2].path, 'https://example.com/docs')
  assert.deepEqual(routes, before)
})

test('搜索支持名称、父级和路径，忽略首尾空格及大小写，空输入不列出菜单', () => {
  const menus = [
    {title: '用户管理', path: '/system/User', parents: ['组织与权限']},
    {title: '角色管理', path: '/system/Role', parents: ['组织与权限']}
  ]
  assert.deepEqual(searchMenus(menus, ' 用户 '), [menus[0]])
  assert.deepEqual(searchMenus(menus, ' /SYSTEM/ROLE '), [menus[1]])
  assert.deepEqual(searchMenus(menus, '组织'), menus)
  for (const input of ['', '   ', '无匹配菜单', '<img src=x onerror=alert(1)>']) {
    assert.deepEqual(searchMenus(menus, input), [])
  }
})

test('搜索最多显示八项，保持授权菜单顺序', () => {
  const menus = Array.from({length: 12}, (_, index) => ({title: `用户${index}`, path: `/user/${index}`, parents: []}))
  assert.deepEqual(searchMenus(menus, '用户'), menus.slice(0, 8))
})
