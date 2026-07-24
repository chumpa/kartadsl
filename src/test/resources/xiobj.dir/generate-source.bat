@echo off
set target=..\..\..\main\java
xjc -d %target% -p io.rsug.zatupka.dir -b bindings.xjb -Xinject-code -extension Party.xsd Service.xsd Channel.xsd
