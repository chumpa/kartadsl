
# Средства генерации кода, явно и оффлайново (вне плагинов gradle).

## trang.jar
Генерирует XSD по XML на входе. Умный, но почти нет параметров.

## jaxb-xjc-4.0.9.jar
Современный XJC в jakarta namespace

https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-xjc
implementation("org.glassfish.jaxb:jaxb-xjc:4.0.9")
implementation("org.glassfish.jaxb:codemodel:4.0.9")
implementation("com.sun.istack:istack-commons-runtime:4.2.0")

## Где генерируются исходники
Временно сделано без плагина гредл, в целом ничего не мешает в generated-source.

kartadsl\src\test\resources\hmi\generate-source.bat 	HMI
kartadsl\src\test\resources\xiobj\generate-source.bat	directory
