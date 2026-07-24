@echo off
set TRANG=..\..\..\..\source-tools\trang.jar
set PARA=-o disable-abstract-elements -o any-process-contents=strict -o any-attribute-process-contents=strict
set src=01.xml 02.xml 03.xml 04.xml 05.xml 06.xml 07.xml 08.xml 09.xml 10.xml 11.xml 12.xml 13.xml 14.xml 15.xml 16.xml
rem 17.xml 18.xml 19.xml 20.xml 21.xml 22.xml 23.xml
java -jar %TRANG% %PARA% %src% xiObj.xsd

rem chcp 65001
rem echo После генерации, в xiObj.xsd поменять xs:choice на xs:sequence согласно README.md
