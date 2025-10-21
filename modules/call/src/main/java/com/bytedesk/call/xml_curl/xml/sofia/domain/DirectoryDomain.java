package com.bytedesk.call.xml_curl.xml.sofia.domain;

import com.bytedesk.call.xml_curl.xml.group.Groups;
import com.bytedesk.call.xml_curl.xml.param.Params;
import com.bytedesk.call.xml_curl.xml.variables.Variables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

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
