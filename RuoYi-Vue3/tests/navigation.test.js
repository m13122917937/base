import assert from 'node:assert/strict'
import test from 'node:test'
import {
    buildNavigationMenus,
    findNavigationMenu,
    getMenuLocation,
    getDefaultMenuLocation,
    getVisibleMenuCount,
    resolveMenuPath
} from '../src/utils/navigation.js'
import {readLayoutSettings} from '../src/utils/layoutSettings.js'

test('权限菜单保留任意深度、参数和绝对路径，并排除隐藏目录及子页面', () => {
    const routes = [
        {path: '/', hidden: true},
        {
            path: '/system', meta: {title: '系统管理'}, children: [
                {
                    path: 'users', meta: {title: '用户管理'}, children: [
                        {path: 'list', query: '{"status":"0"}', meta: {title: '用户列表'}},
                        {path: '/absolute', meta: {title: '绝对路径'}},
                        {path: 'detail/:id', hidden: true, meta: {title: '详情'}}
                    ]
                },
                {path: 'https://example.com/docs', meta: {title: '外部文档'}}
            ]
        },
        {path: '/hidden', hidden: true, children: [{path: 'secret', meta: {title: '隐藏菜单'}}]},
        {path: '/empty', meta: {title: '无授权子项'}, children: [{path: 'secret', hidden: true, meta: {title: '隐藏'}}]}
    ]
    const original = structuredClone(routes)
    const menus = buildNavigationMenus(routes)
    assert.deepEqual(menus.map(item => item.title), ['系统管理'])
    assert.equal(findNavigationMenu(menus, '/system/users/list').query, '{"status":"0"}')
    assert.equal(findNavigationMenu(menus, '/absolute').title, '绝对路径')
    assert.equal(findNavigationMenu(menus, 'https://example.com/docs').title, '外部文档')
    for (const path of ['/system', '/hidden/secret', '/empty', '/system/users/detail/:id']) {
        assert.equal(findNavigationMenu(menus, path), null)
    }
    assert.deepEqual(routes, original, '生成菜单不能修改后端路由，切换布局应能继续使用原树')
})

test('菜单解析不重复拼接绝对路径，保留外链 URL 中的双斜线', () => {
    assert.equal(resolveMenuPath('/system/', 'user'), '/system/user')
    assert.equal(resolveMenuPath('/system', '/monitor'), '/monitor')
    assert.equal(resolveMenuPath('', ''), '/')
    assert.equal(resolveMenuPath('/system', 'https://example.com/a'), 'https://example.com/a')
})

test('路由参数只接受合法 JSON 对象，配置错误不能触发导航', () => {
    assert.deepEqual(getMenuLocation({path: '/system/user', query: '{"status":"0"}'}), {
        path: '/system/user',
        query: {status: '0'}
    })
    assert.deepEqual(getMenuLocation({path: '/system/user'}), {path: '/system/user', query: undefined})
    for (const query of ['{', 'null', '[]', '1', '"text"']) {
        assert.throws(() => getMenuLocation({path: '/system/user', query}))
    }
})

test('默认入口按授权顺序选择站内叶子菜单，保留参数且不改写权限树', () => {
    const routes = [
        {path: '/', hidden: true},
        {path: '/hidden', hidden: true, meta: {title: '隐藏页面'}},
        {path: 'https://example.com', meta: {title: '外链'}},
        {path: '/system', meta: {title: '系统管理'}, children: [
            {path: 'detail/:id', meta: {title: '详情'}},
            {path: 'invalid', query: '{', meta: {title: '错误参数'}},
            {path: 'role', query: '{"status":"0"}', meta: {title: '角色管理'}},
            {path: 'user', meta: {title: '用户管理'}}
        ]}
    ]
    const original = structuredClone(routes)
    assert.deepEqual(getDefaultMenuLocation(routes), {path: '/system/role', query: {status: '0'}})
    assert.deepEqual(routes, original)
    assert.deepEqual(getDefaultMenuLocation([]), {path: '/user/profile'})
    assert.deepEqual(getDefaultMenuLocation(routes.slice(0, 3)), {path: '/user/profile'})
})

test('收纳入口和可见菜单始终不超过导航实际可用宽度', () => {
    for (const width of [112, 200, 367, 384, 500, 992, 1440]) {
        for (const count of [0, 1, 3, 8, 20]) {
            const visible = getVisibleMenuCount(count, width)
            assert.ok(visible >= 0 && visible <= count)
            assert.ok(visible * 144 + (visible < count ? 112 : 0) <= width)
            assert.equal(visible + (count - visible), count, '所有被收纳的菜单仍然可访问')
        }
    }
    assert.equal(getVisibleMenuCount(8, 0), 0, '首次测量前所有项目收入更多菜单')
})

test('布局缓存损坏、类型错误、存储不可访问时可正常回退默认布局', () => {
    const read = value => readLayoutSettings({getItem: () => value})
    for (const value of [null, '{', 'null', '[]', 'false']) assert.deepEqual(read(value), {})
    assert.deepEqual(read('{"navType":"3","tagsView":"false","sideTheme":"invalid","theme":"red"}'), {})
    assert.deepEqual(read('{"navType":1,"tagsView":false,"theme":"#1677FF","sideTheme":"theme-light"}'), {
        navType: 1, tagsView: false, sideTheme: 'theme-light', theme: '#1677FF'
    })
    assert.deepEqual(readLayoutSettings({
        getItem: () => {
            throw new Error('SecurityError')
        }
    }), {})
})

test('头部菜单沿用参考项目的 144px 入口和 112px 更多入口，边界尺寸不丢失菜单', () => {
    assert.equal(getVisibleMenuCount(3, 432), 3)
    assert.equal(getVisibleMenuCount(3, 431), 2)
    assert.equal(getVisibleMenuCount(4, 390), 1)
    assert.equal(getVisibleMenuCount(4, 112), 0)
    assert.equal(getVisibleMenuCount(0, 0), 0)
})
