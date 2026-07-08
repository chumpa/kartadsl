@echo off
set xjc=D:\bin\sapjvm_8\bin\xjc.exe
set target=..\..\..\main\java

%xjc% -d %target% -p io.rsug.zatupka.xiobj xobj.xsd
echo После генерации в классе io.rsug.zatupka.xiobj.Content внутри вручную добавить:
echo "@XmlAnyElement public org.w3c.dom.Element dynamicContent;"
