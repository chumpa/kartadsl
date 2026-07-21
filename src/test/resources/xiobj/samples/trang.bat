@echo off
set TRANG=..\..\..\..\..\libs\trang.jar
set PARA=-o disable-abstract-elements -o any-process-contents=strict -o any-attribute-process-contents=strict
java -jar %TRANG% %PARA% 01.xml 02.xml 03.xml 04.xml 05.xml 06.xml 07.xml 08.xml 09.xml 10.xml 11.xml 12.xml 13.xml 14.xml 15.xml xiObj.xsd

chcp 65001
echo После генерации, в xiObj.xsd поменять xs:choice на xs:sequence согласно README.md
