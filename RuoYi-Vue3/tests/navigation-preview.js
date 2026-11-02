// 仅供 Vite 开发服务下的布局验收；不进入生产入口、不访问真实后端或绕过生产鉴权。
import {createApp, h} from 'vue'
import {createMemoryHistory, createRouter, RouterView, useRoute} from 'vue-router'
import ElementPlus, {ElAlert} from 'element-plus'
import locale from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/styles/index.scss'
import 'virtual:svg-icons-register'
import '@/router'
import Layout from '@/layout/index.vue'
import ParentView from '@/components/ParentView'
import SvgIcon from '@/components/SvgIcon'
import elementIcons from '@/components/SvgIcon/svgicon'
import store from '@/store'
import plugins from '@/plugins'
import request from '@/utils/request'
import usePermissionStore from '@/store/modules/permission'
import useUserStore from '@/store/modules/user'
import avatar from '@/assets/images/profile.jpg'

request.defaults.adapter = async config => ({
    config, request: {responseType: 'json'}, status: 200, statusText: 'OK', headers: {},
    data: {code: 200, data: [], rows: [], total: 0, unreadCount: 0}
})

const PreviewPage = {
    setup() {
        const route = useRoute()
        return () => h('main', {class: 'app-container'}, [
            h(ElAlert, {title: '导航布局预览 · 测试数据 · 未连接真实后端', type: 'info', closable: false}),
            h('h2', route.meta.title),
            h('p', '当前页面：' + route.path),
            h('p', '通过顶部菜单、布局设置或缩小窗口检查导航。当前功能菜单沿用若依结构。')
        ])
    }
}

const page = (path, title, icon, extra = {}) => ({
    path,
    name: title,
    component: PreviewPage,
    meta: {title, icon, ...extra}
})
const menus = [
    {
        path: '/system', component: ParentView, meta: {title: '系统管理', icon: 'system'}, children: [
            page('user', '用户管理', 'user'), page('role', '角色管理', 'peoples'), page('menu', '菜单管理', 'tree-table'),
            page('dept', '部门管理', 'tree'), page('dict', '字典管理', 'dict'),
            page('config', '参数设置', 'edit'), page('notice', '通知公告', 'message'),
            {
                path: 'log',
                component: ParentView,
                meta: {title: '日志管理', icon: 'log'},
                children: [page('operlog', '操作日志', 'form'), page('logininfor', '登录日志', 'logininfor')]
            }
        ]
    },
    {
        path: '/monitor',
        component: ParentView,
        meta: {title: '系统监控', icon: 'monitor'},
        children: [page('job', '定时任务', 'job')]
    },
    {...page('/system/user-auth/role/1', '分配角色', 'user', {activeMenu: '/system/user'}), hidden: true},
    {path: '/user', component: ParentView, hidden: true, children: [page('profile', '个人中心', 'user')]}
]
const router = createRouter({history: createMemoryHistory(), routes: [{path: '/', component: Layout, children: menus}]})
const app = createApp({render: () => h(RouterView)})
app.use(store)
app.use(router)
app.use(ElementPlus, {locale})
app.use(elementIcons)
app.use(plugins)
app.component('svg-icon', SvgIcon)
const permission = usePermissionStore()
permission.routes = menus
permission.defaultRoutes = menus
permission.sidebarRouters = menus
permission.topbarRouters = menus
useUserStore().nickName = '预览用户'
useUserStore().avatar = avatar
await router.push('/system/user')
await router.isReady()
app.mount('#app')
