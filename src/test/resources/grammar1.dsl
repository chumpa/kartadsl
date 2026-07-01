
from("sapjdbc:P_PARTY|BS_DMS|CC_JDBCSender_Tags")
   .setAddress("{urn:dummy_namespace}SI_Tag_OutAsync")
   .filter().xpath("count(//*) > 100")
   .transform("{urn:mappings}OM_Mapping")
   .setAddress("{urn:smart_namespace}SI_Tag_InAsync")
   .to("sapxi:ABD100|CC_XIRecv");

config("sapxi:ABD100|CC_XIRecv")
   .set("destination", "XI_SOAP_RECEIVER_ABD");

config("sapnwa:destination:XI_SOAP_RECEIVER_ABD")
   .set("targetUrl", "http://abd100/sap/xi/engine")
   .set("auth", "BASIC");

from("seda:ordersInput")
   .filter().simple("${body.amount} > 100")
   .to("seda:outputQueueA");

from("timer://dbPoller?period=5000&fixedRate=true") // Таймер для периодического опроса
    .log("Запуск опроса базы данных")
    
    // ШАГ 1: Читаем из базы данных
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
    
