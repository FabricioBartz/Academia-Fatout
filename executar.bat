@echo off
cls
echo ================================================
echo    EXECUTANDO ACADEMIA FATOUT
echo ================================================

REM Configurar ambiente
set JAVA_HOME=C:\Users\valta\.jdk\jdk-21.0.8
set "MAVEN_HOME=C:\Program Files\Maven\apache-maven-3.9.11"
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

REM Ir para a pasta do projeto
cd /d "C:\Users\valta\Desktop\academia-fatout"

REM Executar Maven
echo Executando: mvn clean spring-boot:run
echo.
mvn clean spring-boot:run

pause