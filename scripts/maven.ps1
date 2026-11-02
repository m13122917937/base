<#
.SYNOPSIS
使用指定 JDK 17 和显式 Maven settings 运行构建，不修改任何环境变量。
.EXAMPLE
./scripts/maven.ps1 -MavenArguments @('-pl', 'ruoyi-admin', '-am', 'test')
#>
[CmdletBinding()]
param(
    [string]$JavaHome = 'D:\software\Java\jdk-17.0.20.1+1',
    [string]$SettingsPath = 'D:\project\cj\df-bak\setting.xml',
    [string[]]$MavenArguments = @('verify')
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$javaExecutable = Join-Path $JavaHome 'bin\java.exe'
if (-not (Test-Path -LiteralPath $javaExecutable)) {
    throw "找不到 JDK：$javaExecutable。请通过 -JavaHome 指定 JDK 17 目录。"
}
if ((Get-Content -LiteralPath (Join-Path $JavaHome 'release') -Raw) -notmatch 'JAVA_VERSION="17\.') {
    throw '本项目需要 JDK 17。'
}
if (-not (Test-Path -LiteralPath $SettingsPath)) {
    throw "找不到 Maven settings：$SettingsPath。请通过 -SettingsPath 指定已有配置。"
}
$mavenCommand = Get-Command mvn.cmd -ErrorAction Stop
$mavenHome = Split-Path -Parent (Split-Path -Parent $mavenCommand.Source)
$classworlds = @(Get-ChildItem -LiteralPath (Join-Path $mavenHome 'boot') -Filter 'plexus-classworlds-*.jar')
if ($classworlds.Count -ne 1) {
    throw "无法定位 Maven 启动器：$mavenHome"
}
$launcherArguments = @(
    '-Dfile.encoding=UTF-8',
    "-Dmaven.multiModuleProjectDirectory=$projectRoot",
    "-Dmaven.home=$mavenHome",
    "-Dmaven.conf=$(Join-Path $mavenHome 'conf')",
    "-Dclassworlds.conf=$(Join-Path $mavenHome 'bin\m2.conf')",
    '-classpath', $classworlds[0].FullName,
    'org.codehaus.plexus.classworlds.launcher.Launcher',
    '-s', $SettingsPath
) + $MavenArguments

Push-Location $projectRoot
try {
    & $javaExecutable @launcherArguments
    $mavenExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $mavenExitCode
