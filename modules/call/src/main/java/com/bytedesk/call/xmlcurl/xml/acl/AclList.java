package com.bytedesk.call.xmlcurl.xml.acl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
public class AclList implements Serializable {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlProperty(localName = "default", isAttribute = true)
    private String aclDefault;

    @JacksonXmlElementWrapper(localName = "node", useWrapping = false)
    private List<AclNode> node;


}
