package io.rsug.zatupka.abap;

public enum RfcDestinationEnum {
    // первый символ это тип RFC-дестинейшена
    TFRONTEND,
    TINBOUND,
    TEXPLICIT,
    TAPPLICATION,
    HTTPABAP,
    GHTTP,
    LOCAL,
    INTERNAL,
    ABAP3,                  // type 3
    WEBSOCKETS,
    XABAPDRIVER
}
