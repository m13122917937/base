/** 读取并校验布局偏好；损坏缓存或禁用存储时返回默认配置。 */
export function readLayoutSettings(storage) {
    try {
        const saved = JSON.parse((storage || localStorage).getItem('layout-setting'))
        if (!saved || typeof saved !== 'object' || Array.isArray(saved)) return {}
        const settings = {}
        if ([1, 2, 3].includes(saved.navType)) settings.navType = saved.navType
        const booleanKeys = ['tagsView', 'tagsViewPersist', 'tagsIcon', 'fixedHeader', 'sidebarLogo', 'dynamicTitle', 'footerVisible']
        booleanKeys.forEach(key => {
            if (typeof saved[key] === 'boolean') settings[key] = saved[key]
        })
        if (['theme-dark', 'theme-light'].includes(saved.sideTheme)) settings.sideTheme = saved.sideTheme
        if (['card', 'chrome'].includes(saved.tagsViewStyle)) settings.tagsViewStyle = saved.tagsViewStyle
        if (typeof saved.theme === 'string' && /^#[\da-f]{6}$/i.test(saved.theme)) settings.theme = saved.theme
        return settings
    } catch {
        return {}
    }
}
