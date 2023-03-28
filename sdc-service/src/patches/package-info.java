@jakarta.xml.bind.annotation.XmlSchema(
        namespace = "http://www.fpml.org/FpML-5/confirmation",
        elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
        xmlns={
                @XmlNs(prefix="", namespaceURI="uri:sdc"),
                @XmlNs(prefix="fpml", namespaceURI="http://www.fpml.org/FpML-5/confirmation"),
                @XmlNs(prefix="sig", namespaceURI="http://www.w3.org/2000/09/xmldsig#")
        })
package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlNs;
