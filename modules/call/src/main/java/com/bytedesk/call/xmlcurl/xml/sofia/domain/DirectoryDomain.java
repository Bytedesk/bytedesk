package com.bytedesk.call.xmlcurl.xml.sofia.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.bytedesk.call.xmlcurl.xml.group.Groups;
import com.bytedesk.call.xmlcurl.xml.param.Params;
import com.bytedesk.call.xmlcurl.xml.variables.Variables;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "domain")
public class DirectoryDomain implements Serializable {
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlProperty(localName = "params", isAttribute = true)
    private Params params;

    @JacksonXmlProperty(localName = "variables", isAttribute = true)
    private Variables variables;

    @JacksonXmlProperty(localName = "groups", isAttribute = true)
    private Groups groups;

    public String toXmlString() throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(this);
    }
}
