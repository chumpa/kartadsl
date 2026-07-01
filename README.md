# kartadsl
карта всего сущего

Берём за основу Camel Java DSL но парсим его не явой а самописным парсером на ANTLRv4. 
Можно и явой в приципе тоже, так как синтаксис делается тем же, но придётся писать
независимый от camel-core.

В отличии от CamelJavaDSL вводим новую инструкцию `config`, возможно ещё `documentation` или `project`.
Для каждого вида транспортов и объектов пишем каким-то образом строгую типизацию.

После интерпретации этой простыни можно строить where-used lists кросссистемно и тд.
```java
from("sapjdbc:P_PARTY|BS_DMS|CC_JDBCSender_Tags")
.setAddress("{urn:dummy_namespace}SI_Tag_OutAsync")    пишем интерфейс икохи
.filter().xpath("count(//tag) > 100")
.transform("{urn:mappings}OM_Mapping")
.setAddress("{urn:smart_namespace}SI_Tag_InAsync")
.to("sapxi:ABD100|CC_XIRecv");

//Это описание конфига канала:
config("sapxi:ABD100|CC_XIRecv")
.set("HTTPdestination", "XI_SOAP_RECEIVER_ABD");

//Это описание конфига NWA HTTP Dest:
config("sapnwa:destination:XI_SOAP_RECEIVER_ABD")
.set("targetUrl", "http://abd100/sap/xi/engine")
.set("auth", "BASIC")
.set("login", "techuser")
.setPasswordUsingKeypass("в кипасе лежит как XI_SOAP_RECEIVER_ABD_USER");
```

А теперь стандартный кэмел для сравнения:

```java
from("seda:ordersInput")
.filter().simple("${body.amount} > 100")
.to("seda:outputQueueA");

from("timer://dbPoller?period=5000&fixedRate=true")
.log("Запуск опроса базы данных")
    // Читаем из базы данных
    .setBody(constant("SELECT * FROM orders WHERE processed = false"))
    .to("jdbc:myDataSource?outputType=SelectList&outputClass=java.util.Map")
    .log("Получено ${body.size()} записей из БД")
    .process(convertToXml)
    .choice()
     // Ветка 1: Если элементов > 100, шлем в hundred
     .when().xpath("count(/orders/order) > 100")
         .log("Найдено ${xpath:count(/orders/order)} элементов (больше 100)")
         .setHeader("CamelHttpMethod", constant("POST"))
         .setHeader("Content-Type", constant("application/xml"))
         .to("http://hundred/api/orders")
         .log("Отправлено в http://hundred")
     // Ветка 2: Все остальные шлем в constant
     .otherwise()
         .log("Найдено ${xpath:count(/orders/order)} элементов (не более 100)")
         .setHeader("CamelHttpMethod", constant("POST"))
         .setHeader("Content-Type", constant("application/xml"))
         .to("http://constant/api/orders")
         .log("Отправлено в http://constant")
    .end();
```

Технологический стек: ANTLR грамматика для парсера, plantuml с макросами EIP для генерации картинок.
А также любая БД для вывода туда распаршенного контекста: перечни объектов, связи между ними для быстрых отчётов.

