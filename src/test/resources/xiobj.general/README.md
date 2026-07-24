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
