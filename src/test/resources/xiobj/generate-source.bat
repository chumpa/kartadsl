@echo off
set target=..\..\..\main\java
xjc -d %target% -p io.rsug.zatupka.dir -extension -b bindings.xjb -Xinject-code Party.xsd Service.xsd Channel.xsd
