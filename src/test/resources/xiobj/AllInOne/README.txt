
Ico1.xml - версия 130, асинхрон, большой набор условий, Value@isTable=true
Ico2.xml - версия 130, синхрон, нет /ReceiverAssignmentList@mode
Ico3.xml - версия 130, параметры меппинга
Ico4.xml - version 100, missing noReceiverBehaviour
Ico5.xml - version 130, extended RD
Ico6.xml - version 100, //ReceiverConnectivityList/ReceiverConnectivity пустой -- странная икоха, может быть битая
Ico7.xml - version 110, нет //ReceiverAssignmentList/@mode

makexsd.bat - генерация схем через Microsoft Windows SDK .Net xsd.exe, удобно смотреть варианты
trang.bat - генерация одной схемы по списку файлов через trang.jar, работает отлично но для xs:import требует напильника

# Процедура генерации
1. Запустить trang.bat
2. В AllInOne.xsd заменить импорт p2.xsd на:

  <xs:import namespace="http://sap.com/xi/ib/prefix" schemaLocation="NSM.xsd"/>

3. В AllInOne.xsd заменить:

<xs:element name="COMPOP" type="xs:NCName"/>
на
<xs:element name="COMPOP" type="CONDLINE_COMPOP_ENUM"/>

<xs:element name="TYPE" type="xs:NCName"/>
на
<xs:element name="TYPE" type="TRD_EXTRACTOR_TYPEENUM"/>

и в конце AllInOne.xsd вставить определения из enum.xsd

4. generate-source.bat

5. copy NSM.xsd AllInOne.xsd %WORKSPACE%\kartadsl\src\main\resources\io.rsug.zatupka.xsd\



## enum.xsd
Для некоторых полей вручную сделаны перечисления, но trang.jar их конечно автоматически не генерирует и не использует.


## Важные замечания

* В src/main/resources/io.rsug.zatupka.xsd сюда складываем очищенное после тестов.
  Если есть проблемы с валидацией, пишем новый XML-пример в Ico#.xml, вносим в trang.bat 

* эти xsd используются в рантайме в валидации, без которой уверенности в результате нет

* trang.jar генерирует /AllInOne/NamespaceMapping/p2:NSM/definition не 0..unbounded а 1..1, надо не забывать руками проставить

* все ссылающиеся друг на друга схемы надо грузить вместе, причём порядок важен (ds2 NSM.xsd первым):
  javax.xml.validation.Schema schemas = factory.newSchema(new DOMSource[]{new DOMSource(ds2),new DOMSource(ds1)});

* trang.jar сделает p2.xsd, его использовать не надо, эталон в src/main/resources/io.rsug.zatupka.xsd/NSM.xsd, на который
  в AllInOne.xsd надо сделать ссылку:

  <xs:import namespace="http://sap.com/xi/ib/prefix" schemaLocation="NSM.xsd"/>

* импорт из NSM.xsd схемы AllInOne.xsd не требуется, т.к. <definition ... /> определяется в xmlns="" на месте

* генерация исходников

## Требуется протестировать ещё
* Extended RD с параметрами меппинга
* пример с параметром канала-таблицей



