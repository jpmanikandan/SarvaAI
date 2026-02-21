$port = 8080
$process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1
if ($process) {
    Write-Host "Stopping process $process running on port $port..."
    Stop-Process -Id $process -Force
    Write-Host "Application stopped."
}
else {
    Write-Host "No application found running on port $port."
}
