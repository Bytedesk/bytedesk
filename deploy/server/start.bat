@echo off
set RUN_NAME=bytedesk-starter-0.4.0.jar
set LANG=en_US.UTF-8
set PRG=%0
set APPDIRFILE=%~dp0
cd %APPDIRFILE%
echo %APPDIRFILE%\%RUN_NAME%
for /F "tokens=2 delims= " %%G in ('tasklist ^| findstr /I %RUN_NAME%) do set PID=%%G
echo %PID%
if not "%PID%"=="" (
   echo 已启动,杀掉进程后重启
   echo %PID%
   for /F "tokens=2 delims= " %%G in ('tasklist ^| findstr /I %RUN_NAME%) do (
      endlocal
      taskkill /PID %%G /F
      setlocal enabledelayedexpansion
   )
) else (
   echo bytedesk程序开始启动
)
if exist %RUN_NAME% (
   start "" /B java -jar %RUN_NAME% >nul 2>&1
) else (
   echo %RUN_NAME% not found.
)
