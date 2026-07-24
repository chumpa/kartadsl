@echo off
rem xjc 4.0.9
setlocal enabledelayedexpansion
set "ALL_ARGS=%*"
set f=d:\bin\xjc
set cp=%f%\jaxb-xjc-4.0.9.jar;%f%\jaxb-runtime-4.0.9.jar;%f%\jaxb-core-4.0.9.jar;%f%\codemodel-4.0.9.jar;%f%\istack-commons-runtime-4.2.0.jar;%f%\xsom-4.0.9.jar;%f%\rngom-4.0.9.jar;%f%\relaxng-datatype-4.0.9.jar;%f%\jakarta.activation-api-2.1.4.jar;%f%\jakarta.xml.bind-api-4.0.5.jar;%f%\txw2-4.0.9.jar
java -cp %cp% com.sun.tools.xjc.XJCFacade !ALL_ARGS!
