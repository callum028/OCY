@echo off

set ocypath=%~dp0%
set filepath=%cd%

java -jar %ocypath%ocy.jar %filepath%\%1