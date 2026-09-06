$JAVAC = "D:\JAVAFX\zulu17.68.17-ca-fx-jdk17.0.20-win_x64\zulu17.68.17-ca-fx-jdk17.0.20-win_x64\bin\javac.exe"
$JAVA  = "D:\JAVAFX\zulu17.68.17-ca-fx-jdk17.0.20-win_x64\zulu17.68.17-ca-fx-jdk17.0.20-win_x64\bin\java.exe"
$MODULE_PATH = "D:\JAVAFX\zulu17.68.17-ca-fx-jdk17.0.20-win_x64\zulu17.68.17-ca-fx-jdk17.0.20-win_x64\jmods"

Write-Host "Compiling JavaFX Poker Game..." -ForegroundColor Cyan
& $JAVAC --module-path $MODULE_PATH --add-modules javafx.controls,javafx.fxml,javafx.media -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName

if ($LASTEXITCODE -eq 0) {
    Write-Host "Launching OpenDecks Poker Game UI..." -ForegroundColor Green
    & $JAVA --module-path $MODULE_PATH --add-modules javafx.controls,javafx.fxml,javafx.media -cp bin poker.Launcher
} else {
    Write-Host "Compilation failed." -ForegroundColor Red
}
