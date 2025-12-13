@echo off
title 🏋️ Academia FatOut - EXECUTANDO...
cls

echo ================================================
echo    EXECUTANDO ACADEMIA FATOUT (NAO FECHA)
echo ================================================
echo.

REM 1. Configurar ambiente
echo Configurando Java e Maven...
set JAVA_HOME=C:\Users\valta\.jdk\jdk-21.0.8
set "MAVEN_HOME=C:\Program Files\Maven\apache-maven-3.9.11"
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo Java: %JAVA_HOME%
echo Maven: %MAVEN_HOME%
mvn --version

echo.
echo ================================================
echo 2. Indo para o projeto...
cd /d "C:\Users\valta\Desktop\academia-fatout"
echo Diretorio: %cd%

echo.
echo ================================================
echo 3. Executando Academia FatOut...
echo Comando: mvn clean spring-boot:run
echo ================================================
echo.

REM Executar e MANTER aberto mesmo com erro
mvn clean spring-boot:run

echo.
echo ================================================
echo PROCESSO TERMINOU. Verifique mensagens acima.
echo ================================================
pause