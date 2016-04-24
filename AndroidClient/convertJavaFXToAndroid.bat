set ANDROID_SDK=E:/tools/Android
set HERE=%cd%
set DALVIK_SDK=E:/tools/dalvik-sdk
set JAVAFX_APP_DIR=%HERE%/app
set PATH=%ANDROID_SDK%/tools;%PATH%
set WORKDIR=%HERE%/android
set PACKAGE="pl.dur.client"
set NAME="AndroidClient"
set MAINCLASS="pl.dur.client.MainClass"
set TARGET=15
set FX_PROJECT=E:\Projects\Magisterka\StadaloneClient\target\StandaloneClient.jar

adb kill-server
copy  /y  %FX_PROJECT% %HERE%\app
IF EXIST %HERE%\android RMDIR %HERE%\android /S /Q
call gradlew.bat --info createProject -PDEBUG -PDIR=%WORKDIR% -PPACKAGE=%PACKAGE% -PNAME=%NAME% -PANDROID_SDK=%ANDROID_SDK% -PJFX_SDK=%DALVIK_SDK% -PJFX_APP=%JAVAFX_APP_DIR% -PJFX_MAIN=%MAINCLASS% -PTARGET=%TARGET%
cd %HERE%
copy  /y  AndroidManifest.xml %HERE%\android\AndroidClient
cd %HERE%\android\%NAME%
echo %cd%
call ant clean debug
cd %HERE%
REM call copyToAllDevice.bat
call copyToDevice.bat
REM cd bin
REM adb install -r %NAME%-debug.apk
REM D:
REM copy /Y nul: D:\temp\log.txt
REM adb logcat > D:\temp\log.txt
REM F:
REM start F:\tools\ALV_1.9.28.1000\Viewer.exe

