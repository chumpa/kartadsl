
# Генерация схемы после trang.jar
//TODO -- автоматизировать или переписать trang.jar под себя, второе лучше!
вывод схемы взять txw2 из jaxb

## канонические определения xiObj, content, inner заменить на:
```xml
  <xs:element name="xiObj">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="p1:idInfo"/>
        <xs:element ref="p1:generic"/>
        <xs:element ref="p1:content"/>
        <xs:element ref="p1:inner" minOccurs="0"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="content" type="xs:anyType"/>
  <xs:element name="inner">
    <xs:complexType>
      <xs:sequence>
        <xs:element minOccurs="0" maxOccurs="unbounded" ref="p1:xiObj"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
```

## xs:NCName заменить на xs:string

## Часть прикладных объектов в пустой области имён находятся прямо здесь
```xml
        <xs:element name="AlertRule">
          <xs:complexType form="unqualified">
            <xs:sequence>
              <xs:element name="ErrorLabels" form="unqualified">
                <xs:complexType/>
              </xs:element>
              <xs:element name="Consumers" form="unqualified">
                <xs:complexType/>
              </xs:element>
              <xs:element name="Runtimes" form="unqualified">
                <xs:complexType>
                  <xs:sequence>
                    <xs:element maxOccurs="unbounded" name="RuntimeName" form="unqualified" type="xs:string"/>
                  </xs:sequence>
                </xs:complexType>
              </xs:element>
              <xs:element name="Severity" form="unqualified" type="xs:string"/>
              <xs:element name="PayloadInAlert" form="unqualified" type="xs:string"/>
              <xs:element name="RuntimeState" form="unqualified" type="xs:string"/>
              <xs:element name="RuleType" form="unqualified" type="xs:string"/>
              <xs:element name="BlockTime" form="unqualified" type="xs:integer"/>
              <xs:element name="SuppressionTime" form="unqualified" type="xs:integer"/>
              <xs:element name="HeaderRules" form="unqualified">
                <xs:complexType/>
              </xs:element>
              <xs:element name="UDSAttributes" form="unqualified">
                <xs:complexType>
                  <xs:sequence>
                    <xs:element name="UDSAttribute" form="unqualified">
                      <xs:complexType>
                        <xs:sequence>
                          <xs:element name="Name" form="unqualified">
                            <xs:complexType/>
                          </xs:element>
                          <xs:element name="Operator" form="unqualified">
                            <xs:complexType/>
                          </xs:element>
                          <xs:element name="Value" form="unqualified">
                            <xs:complexType/>
                          </xs:element>
                        </xs:sequence>
                      </xs:complexType>
                    </xs:element>
                  </xs:sequence>
                </xs:complexType>
              </xs:element>
            </xs:sequence>
          </xs:complexType>
        </xs:element>
        <xs:element name="Valuemapping">
          <xs:complexType form="unqualified">
            <xs:sequence>
              <xs:element maxOccurs="unbounded" name="Representation" form="unqualified">
                <xs:complexType>
                  <xs:sequence>
                    <xs:element name="system" form="unqualified" type="xs:string"/>
                    <xs:element name="objectTypeName" form="unqualified" type="xs:string"/>
                    <xs:element name="value" form="unqualified" type="xs:string"/>
                  </xs:sequence>
                </xs:complexType>
              </xs:element>
            </xs:sequence>
            <xs:attribute name="groupName" use="required" type="xs:string"/>
            <xs:attribute name="groupid" use="required"/>
          </xs:complexType>
        </xs:element>
        <xs:element name="attribs">
          <xs:complexType form="unqualified">
            <xs:sequence>
              <xs:element name="name" form="unqualified" type="xs:string"/>
              <xs:element name="ns" form="unqualified">
                <xs:complexType/>
              </xs:element>
            </xs:sequence>
          </xs:complexType>
        </xs:element>
```
