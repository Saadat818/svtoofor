@echo off
cd /d "%~dp0"
echo Starting TrafficLight Server...
java -cp target\svetoofor-1.0-SNAPSHOT.jar incuat.kg.svetoofor.TrafficLightServer
pause
