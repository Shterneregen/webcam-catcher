set jarName=WebViewer.jar
echo f | xcopy /y ..\dist\%jarName% .\%jarName%
REM pause