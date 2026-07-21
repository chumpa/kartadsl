@echo off
set xjc=D:\bin\sapjvm_8\bin\xjc.exe
set target=..\..\..\..\main\java

%xjc% -d %target% -p io.rsug.zatupka.xiobj -b xiObj.xjb xiObj.xsd
copy xiObj.xsd %target%\..\resources\io.rsug.zatupka.xsd\
chcp 65001 
echo После генерации в классе io.rsug.zatupka.xiobj.Content внутри вручную добавить поле:
echo @XmlAnyElement public org.w3c.dom.Element dynamicContent;
