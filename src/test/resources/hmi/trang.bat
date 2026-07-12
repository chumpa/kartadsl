@echo off
rem Trang.jar is in libs folder
set TRANG=..\..\..\..\libs\trang.jar
set PARAM=-o disable-abstract-elements -o any-process-contents=strict -o any-attribute-process-contents=strict
java -jar %TRANG% %PARAM% 00.xml 01.xml 02.xml 03.xml hmi.xsd
