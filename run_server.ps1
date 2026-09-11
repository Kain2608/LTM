Write-Host "Building and starting Poker backend..." -ForegroundColor Cyan
Push-Location $PSScriptRoot
try {
    & "$PSScriptRoot\mvnw.cmd" -q -DskipTests compile exec:java "-Dexec.mainClass=poker.server.ServerMain"
    if ($LASTEXITCODE -ne 0) {
        throw "Poker backend exited with code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
