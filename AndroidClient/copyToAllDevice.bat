set NAME="AndroidClient"
set HERE=%cd%

cd %HERE%\android\%NAME%
cd bin
REM adb -s RKTKHU6PZPTC55OZ install -r %NAME%-debug.apk 
REM adb -s 62B83A2E566D7818D1427C684E0783E install -r %NAME%-debug.apk 
adb -s 001969c271b25e install -r %NAME%-debug.apk  
cd ..\..\..\
REM start startLog2.bat  
D:
copy /Y nul: D:\temp\tel.txt
REM moj telefon
REM adb -s 62B83A2E566D7818D1427C684E0783E logcat > D:\temp\tel.txt

REM telefon Magdy
adb -s 001969c271b25e logcat > D:\temp\tel.txt

REM tablet
REM adb -s RKTKHU6PZPTC55OZ logcat > D:\temp\tel.txt

F: