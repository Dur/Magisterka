set NAME="AndroidClient"
set HERE=%cd%

cd %HERE%\android\%NAME%
cd bin
adb install -r %NAME%-debug.apk  
C:
copy /Y nul: C:\temp\log.txt
E:
cd %HERE%
call startLog.bat

