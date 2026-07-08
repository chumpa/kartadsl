@echo off
set xjc=D:\bin\sapjvm_8\bin\xjc.exe
set target=..\..\..\main\java

rem %xjc% -d %target% -p io.rsug.zatupka.xiobj xobj.xsd
rem echo После генерации в классе io.rsug.zatupka.xiobj.Content внутри вручную добавить:
rem echo "@XmlAnyElement public org.w3c.dom.Element dynamicContent;"

%xjc% -d %target% -p io.rsug.zatupka.allinone -b AllInOne.xjb NSM.xsd
