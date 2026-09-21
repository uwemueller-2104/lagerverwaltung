@echo off
:: Pfade relativ oder absolut setzen (hier absolut für Eindeutigkeit)
:: set "JAVA_HOME=C:\Users\UweMueller\dev\jdk-27"
set "JAVA_HOME=C:\Users\UweMueller\dev\jdk-21.0.12.1"
set "M2_HOME=C:\Users\UweMueller\portableApps\apache-maven-3.9.16"

:: Umgebungsvariablen für diese Sitzung aktualisieren
set "PATH=%JAVA_HOME%\bin;%M2_HOME%\bin;%PATH%"

:: Konsole geöffnet halten und Versionen prüfen
echo Portable Java und Maven Umgebung geladen!
echo ----------------------------------------
java -version
echo ----------------------------------------
mvn -version
echo ----------------------------------------
cmd /k