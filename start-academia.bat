@echo off
title 🏋️ Academia FatOut - Iniciando...
cls

echo ================================================
echo    INICIANDO SISTEMA ACADEMIA FATOUT
echo ================================================

echo.
echo 1. Configurando ambiente Java/Maven...
set JAVA_HOME=C:\Users\valta\.jdk\jdk-21.0.8
set "MAVEN_HOME=C:\Program Files\Maven\apache-maven-3.9.11"
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo    Java: %JAVA_HOME%
echo    Maven: %MAVEN_HOME%
mvn -version

echo.
echo 2. Executando projeto Academia FatOut...
echo ================================================

mvn clean spring-boot:run

pause