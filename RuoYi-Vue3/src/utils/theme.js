// 处理主题样式
export function handleThemeStyle(theme) {
    const primary = theme
    document.documentElement.style.setProperty('--navigation-primary-rgb', hexToRgb(primary).join(', '))
    document.documentElement.style.setProperty('--el-color-primary', primary)
    for (let i = 1; i <= 9; i++) {
        document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, `${getLightColor(primary, i / 10)}`)
    }
    for (let i = 1; i <= 9; i++) {
        document.documentElement.style.setProperty(`--el-color-primary-dark-${i}`, `${getDarkColor(primary, i / 10)}`)
    }
}

const THEME_PAIRS = {
    light: {'#007AFF': '#0A84FF', '#34C759': '#30D158', '#FF9500': '#FF9F0A', '#FF3B30': '#FF453A', '#AF52DE': '#BF5AF2', '#5AC8FA': '#64D2FF', '#FF2D55': '#FF375F', '#8E8E93': '#98989D'},
    dark: {'#0A84FF': '#007AFF', '#30D158': '#34C759', '#FF9F0A': '#FF9500', '#FF453A': '#FF3B30', '#BF5AF2': '#AF52DE', '#64D2FF': '#5AC8FA', '#FF375F': '#FF2D55', '#98989D': '#8E8E93'}
}

/** 沿用 ruoyi-ui 的深浅色配对表，自定义颜色按同样比例调整亮度。 */
export function getThemePairedColor(currentColor, fromMode, toMode) {
    const color = /^#[\da-f]{6}$/i.test(currentColor) ? currentColor.toUpperCase() : '#007AFF'
    return THEME_PAIRS[fromMode]?.[color] || (toMode === 'dark'
        ? mixHexColors(color, '#ffffff', 0.25)
        : mixHexColors(color, '#000000', 0.12))
}

/** 混合两种十六进制颜色 */
export function mixHexColors(fg, bg, t) {
    const a = hexToRgb(String(fg).replace('#', ''))
    const b = hexToRgb(String(bg).replace('#', ''))
    const out = [0, 1, 2].map((i) => Math.round(a[i] * (1 - t) + b[i] * t))
    return rgbToHex(out[0], out[1], out[2])
}

/** 暗色模式下柔化主题色 */
export function softenPrimaryForDark(theme) {
    return mixHexColors(theme, '#2d3036', 0.34)
}

// hex颜色转rgb颜色
export function hexToRgb(str) {
    str = str.replace('#', '')
    let hexs = str.match(/../g)
    for (let i = 0; i < 3; i++) {
        hexs[i] = parseInt(hexs[i], 16)
    }
    return hexs
}

// rgb颜色转Hex颜色
export function rgbToHex(r, g, b) {
    let hexs = [r.toString(16), g.toString(16), b.toString(16)]
    for (let i = 0; i < 3; i++) {
        if (hexs[i].length == 1) {
            hexs[i] = `0${hexs[i]}`
        }
    }
    return `#${hexs.join('')}`
}

// 变浅颜色值
export function getLightColor(color, level) {
    let rgb = hexToRgb(color)
    for (let i = 0; i < 3; i++) {
        rgb[i] = Math.floor((255 - rgb[i]) * level + rgb[i])
    }
    return rgbToHex(rgb[0], rgb[1], rgb[2])
}

// 变深颜色值
export function getDarkColor(color, level) {
    let rgb = hexToRgb(color)
    for (let i = 0; i < 3; i++) {
        rgb[i] = Math.floor(rgb[i] * (1 - level))
    }
    return rgbToHex(rgb[0], rgb[1], rgb[2])
}
