@echo off
cd /d "%~dp0"
echo Starting TrafficLight Server...
start /B java -cp target\svetoofor-1.0-SNAPSHOT.jar incuat.kg.svetoofor.TrafficLightServer
timeout /t 2 /nobreak > nul
echo Starting TrafficLight Application...
java -jar target\svetoofor-1.0-SNAPSHOT.jar
echo.
echo Application closed. Press any key to exit...
pause
