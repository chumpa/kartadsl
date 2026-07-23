@echo off
set target=..\..\..\main\java

xjc -d %target% -p io.rsug.zatupka.hmi hmi.xsd

