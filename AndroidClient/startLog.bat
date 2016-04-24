type NUL > C:\temp\log.txt
REM adb logcat > C:\temp\log.txt
adb logcat | findstr /c:"###" > C:\temp\log.txt
F: