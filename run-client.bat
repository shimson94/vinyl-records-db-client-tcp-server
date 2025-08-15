@echo off
cd /d "%~dp0"
echo Starting RecordsDatabaseClient...
java --module-path "lib\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp "src;lib\postgresql-42.6.0.jar" RecordsDatabaseClient
pause
