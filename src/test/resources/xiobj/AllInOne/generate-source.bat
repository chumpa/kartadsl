@echo off
set xjc=D:\bin\sapjvm_8\bin\xjc.exe
set target=..\..\..\..\main\java

%xjc% -d %target% -p io.rsug.zatupka.allinone -b AllInOne.xjb AllInOne.xsd NSM.xsd
