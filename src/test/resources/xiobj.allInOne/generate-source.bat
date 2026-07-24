@echo off
set target=..\..\..\main\java

xjc -d %target% -p io.rsug.zatupka.allinone -b AllInOne.xjb -extension -Xinject-code AllInOne.xsd NSM.xsd
