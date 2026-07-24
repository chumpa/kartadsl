@echo off
set target=..\..\..\main\java
copy xiObj.xsd %target%\..\resources\io.rsug.zatupka.xsd\
xjc -d %target% -p io.rsug.zatupka.xiobj -b xiObj.xjb -extension xiObj.xsd
rem chcp 65001 
rem - неактуально После генерации в классе io.rsug.zatupka.xiobj.Content внутри вручную добавить поле:
rem - неактуально @XmlAnyElement public org.w3c.dom.Element dynamicContent;
