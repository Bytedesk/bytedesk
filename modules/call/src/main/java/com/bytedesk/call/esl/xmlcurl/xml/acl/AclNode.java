package com.bytedesk.call.esl.xmlcurl.xml.acl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AclNode implements Serializable {

    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;

    @JacksonXmlProperty(localName = "domain", isAttribute = true)
    private String domain;

    @JacksonXmlProperty(localName = "cidr", isAttribute = true)
    private String cidr;
}
