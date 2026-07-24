package io.rsug.zatupka;

import io.rsug.zatupka.xiobj.XiObj;

import java.nio.file.Path;

public record XiObjFile(XiObj xiObj, String typeID, Path pathXml, Object content) {
}
