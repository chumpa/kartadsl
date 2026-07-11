@echo off
rem Trang.jar is in libs folder
set TRANG=..\..\..\..\..\libs\trang.jar
java -jar %TRANG% -o disable-abstract-elements -o any-process-contents=strict -o any-attribute-process-contents=strict Ico1.xml Ico2.xml Ico3.xml Ico4.xml Ico5.xml Ico6.xml ICo7.xml AllInOne.xsd
