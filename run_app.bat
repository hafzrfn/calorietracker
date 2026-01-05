@echo off
echo Building and Installing Debug APK...
call .\gradlew.bat installDebug --stacktrace
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)

echo Launching App...
adb shell am start -n com.example.calorietracker/.SplashActivity
echo Done!
