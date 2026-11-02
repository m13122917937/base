import {buildNavigationMenus, getMenuLocation} from './navigation.js'
import {isExternal} from './validate.js'

const KEY_PREFIX = 'cf-favorite-tabs:'

/** 按账户读取收藏路径；损坏缓存和不可用存储不阻止页面启动。 */
export function readFavoriteTabs(userId, storage) {
  if (userId === '' || userId === undefined || userId === null) return []
  try {
    const paths = JSON.parse((storage || globalThis.localStorage).getItem(KEY_PREFIX + encodeURIComponent(userId)))
    if (!Array.isArray(paths)) return []
    return [...new Set(paths.filter(path => typeof path === 'string' && path.startsWith('/') && !path.startsWith('//')))]
  } catch {
    return []
  }
}

/** 仅持久化当前账户的收藏路径，不改写其他布局偏好。 */
export function saveFavoriteTabs(userId, paths, storage) {
  if (userId === '' || userId === undefined || userId === null) return
  try {
    const targetStorage = storage || globalThis.localStorage
    targetStorage.setItem(KEY_PREFIX + encodeURIComponent(userId), JSON.stringify(paths))
  } catch {
    // 存储受限时仍允许本次会话使用收藏。
  }
}

/** 只恢复当前授权树中的站内叶子菜单，并保留其受控默认参数。 */
export function getFavoriteMenuLocations(paths, routes) {
  const favorites = new Set(paths)
  function collect(menus) {
    return menus.flatMap(menu => {
      if (menu.children.length) return collect(menu.children)
      if (!favorites.has(menu.path) || isExternal(menu.path)) return []
      try {
        return [{...getMenuLocation(menu), title: menu.title}]
      } catch {
        return []
      }
    })
  }
  return collect(buildNavigationMenus(routes))
}
