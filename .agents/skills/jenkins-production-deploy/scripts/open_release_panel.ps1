param([switch]$Check)

$ErrorActionPreference = 'Stop'
$panelPath = Join-Path $PSScriptRoot 'jenkins_panel.py'
$pythonCommand = Get-Command python.exe -ErrorAction SilentlyContinue
if (-not $pythonCommand) {
    throw 'Python with Tkinter is required. No software has been installed.'
}
$taskPython = $pythonCommand.Source
& $taskPython -B -c 'import tkinter, ctypes'
if ($LASTEXITCODE -ne 0) {
    throw 'Python Tkinter is unavailable. The release panel was not started.'
}
if (-not (Test-Path -LiteralPath $panelPath -PathType Leaf)) {
    throw 'The release panel script is missing.'
}
if ($Check) {
    Write-Output 'Release panel runtime OK'
    return
}

$taskPythonw = Join-Path (Split-Path -Parent $taskPython) 'pythonw.exe'
$quotedPanel = '"{0}"' -f $panelPath
if (Test-Path -LiteralPath $taskPythonw -PathType Leaf) {
    Start-Process -FilePath $taskPythonw -ArgumentList @('-X', 'utf8', '-B', $quotedPanel) -WorkingDirectory $PSScriptRoot
} else {
    Start-Process -FilePath $taskPython -ArgumentList @('-X', 'utf8', '-B', $quotedPanel) -WorkingDirectory $PSScriptRoot -WindowStyle Hidden
}
