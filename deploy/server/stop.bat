@echo off
set RUN_NAME=bytedesk-starter-0.4.4.jar
set LANG=en_US.UTF-8
set PRG=%0
set APPDIRFILE=%~dp0
echo %APPDIRFILE%\%RUN_NAME%
for /F "tokens=2 delims= " %%G in ('tasklist ^| findstr /I %RUN_NAME%') do set PID=%%G
echo %PID%
if "%PID%"=="" (
   echo bytedesk程序未启动
) else (
   echo 已杀掉进程
   echo %PID%
   taskkill /PID %PID% /F
)
