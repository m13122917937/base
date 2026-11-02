import assert from 'node:assert/strict'
import {readFile} from 'node:fs/promises'
import test from 'node:test'

test('旧库已移除页面不再注册，其余授权路由和展示树保持完整', async () => {
    const source = await readFile(new URL('../src/store/modules/permission.js', import.meta.url), 'utf8')
    const functions = source.slice(source.indexOf('function filterAsyncRouter('), source.indexOf('export function filterDynamicRoutes('))
    const filter = new Function('Layout', 'ParentView', 'InnerLink', 'loadView', functions + '\nreturn filterAsyncRouter')(
        'layout-component', 'parent-component', 'link-component', view => () => view
    )
    const routes = [{
        path: '/tool', component: 'Layout', children: [
            {path: 'swagger', component: 'tool/swagger/index', meta: {title: '系统接口'}},
            {path: 'gen', component: 'tool/gen/index', permissions: ['tool:gen:list']},
            {path: 'build', component: 'tool/build/index'},
            {path: 'druid', component: 'monitor/druid/index', permissions: ['monitor:druid:list']},
            {path: 'server', component: 'monitor/server/index', permissions: ['monitor:server:list']},
            {path: 'cache', component: 'monitor/cache/index', permissions: ['monitor:cache:list']},
            {path: 'cacheList', component: 'monitor/cache/list', permissions: ['monitor:cache:list']},
            {path: 'online', component: 'monitor/online/index', permissions: ['monitor:online:list']},
            {path: 'post', component: 'system/post/index', permissions: ['system:post:list']},
            {path: 'custom', component: 'custom/tool/index', permissions: ['custom:tool:list']}
        ]
    }]
    const unchanged = structuredClone(routes)
    for (const flatten of [false, true]) {
        const result = filter(structuredClone(routes), false, flatten)
        assert.equal(result.length, 1)
        assert.deepEqual(result[0].children.map(route => route.path), ['custom'])
        assert.deepEqual(result[0].children[0].permissions, ['custom:tool:list'])
        const retiredOnly = structuredClone(routes)
        retiredOnly[0].children.pop()
        assert.deepEqual(filter(structuredClone(retiredOnly), false, flatten), [])
        const nested = [{path: '/tools', component: 'Layout', children: [{
            path: 'group', component: 'ParentView', children: structuredClone(retiredOnly[0].children)
        }]}]
        assert.deepEqual(filter(nested, false, flatten), [])
    }
    assert.deepEqual(routes, unchanged)
})
