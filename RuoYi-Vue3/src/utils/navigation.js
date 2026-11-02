import {isExternal} from './validate.js'

/** 解析权限菜单路径，保留绝对路径、外链和内嵌页面的路由语义。 */
export function resolveMenuPath(base, path = '') {
    if (isExternal(path)) return path
    if (isExternal(base)) return base
    if (path.startsWith('/')) return path
    return `${base || ''}/${path}`.replace(/\/+/g, '/').replace(/\/$/, '') || '/'
}

/** 将已授权路由转换为独立导航树，不改写 Pinia 中的路由和子菜单顺序。 */
export function buildNavigationMenus(routes = [], base = '') {
    return routes.flatMap(route => {
        if (route.hidden) return []
        const path = resolveMenuPath(base, route.path)
        const children = buildNavigationMenus(route.children || [], path)
        if (!route.meta?.title) return children
        if (route.children?.length && !children.length) return []
        return [{path, title: route.meta.title, icon: route.meta.icon, query: route.query, children}]
    })
}

/** 只查找可导航的叶子菜单，目录和隐藏路由不产生跳转目标。 */
export function findNavigationMenu(menus, path) {
    for (const menu of menus) {
        if (menu.path === path && !menu.children.length) return menu
        const child = findNavigationMenu(menu.children, path)
        if (child) return child
    }
    return null
}

/** 将菜单的可选 JSON 参数转换为路由目标；错误配置由调用方展示错误提示。 */
export function getMenuLocation(menu) {
    const query = menu.query ? JSON.parse(menu.query) : undefined
    if (query !== undefined && (!query || typeof query !== 'object' || Array.isArray(query))) {
        throw new Error('菜单参数必须是 JSON 对象')
    }
    return {path: menu.path, query}
}

/** 默认进入首个授权的站内叶子菜单；无可用菜单时进入公共个人中心。 */
export function getDefaultMenuLocation(routes = []) {
    function findLocation(menus) {
        for (const menu of menus) {
            if (menu.children.length) {
                const location = findLocation(menu.children)
                if (location) return location
            } else if (!isExternal(menu.path) && !['/', '/index'].includes(menu.path) && !menu.path.includes(':')) {
                try {
                    return getMenuLocation(menu)
                } catch {
                    // 跳过参数配置错误的菜单，继续寻找可访问入口。
                }
            }
        }
    }
    return findLocation(buildNavigationMenus(routes)) || {path: '/user/profile'}
}

/** 按导航实际可用宽度收纳菜单，为“更多菜单”预留一个入口。 */
export function getVisibleMenuCount(count, width, itemWidth = 144, overflowWidth = 112) {
    if (count * itemWidth <= width) return count
    return Math.max(0, Math.floor((width - overflowWidth) / itemWidth))
}

