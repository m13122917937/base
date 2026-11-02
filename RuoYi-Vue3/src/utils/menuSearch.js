import {buildNavigationMenus} from './navigation.js'

/** 从完整授权路由提取可访问的叶子菜单，保留父级、路径及路由参数。 */
export function buildMenuSearchIndex(routes = []) {
  function collect(menus, parents = []) {
    return menus.flatMap(menu => menu.children.length
      ? collect(menu.children, [...parents, menu.title])
      : [{path: menu.path, title: menu.title, parents, query: menu.query}])
  }
  return collect(buildNavigationMenus(routes))
}

/** 按菜单名称、所属层级或路径匹配，空关键词不展示结果，最多返回八项。 */
export function searchMenus(menus, input) {
  const keyword = input.trim().toLowerCase()
  if (!keyword) return []
  return menus.filter(menu => [menu.title, menu.path, ...menu.parents]
    .some(value => value.toLowerCase().includes(keyword))).slice(0, 8)
}
