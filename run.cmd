@echo off
cd /d "%~dp0"
call mvnw.cmd -q -DskipTests package || exit /b 1
java -jar target\pioneer-delivery-part2.jar
