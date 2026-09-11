Write-Host "Building and starting Poker JavaFX client..." -ForegroundColor Cyan
Push-Location $PSScriptRoot
try {
    & "$PSScriptRoot\mvnw.cmd" -q -DskipTests javafx:run
    if ($LASTEXITCODE -ne 0) {
        throw "Poker client exited with code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
