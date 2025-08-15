@echo off
cd /d "%~dp0"
echo Starting RecordsDatabaseServer...
java -cp "src;lib\postgresql-42.6.0.jar" RecordsDatabaseServer
pause
