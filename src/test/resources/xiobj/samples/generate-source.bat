@echo off
set target=..\..\..\..\main\java

xjc -d %target% -p io.rsug.zatupka.xiobj -b xiObj.xjb -extension xiObj.xsd
rem copy xiObj.xsd %target%\..\resources\io.rsug.zatupka.xsd\
rem chcp 65001 
rem echo После генерации в классе io.rsug.zatupka.xiobj.Content внутри вручную добавить поле:
rem echo @XmlAnyElement public org.w3c.dom.Element dynamicContent;
