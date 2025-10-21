package com.bytedesk.call.esl.xmlcurl.xml.dialplan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author danmo
 */
@Data
@JacksonXmlRootElement(localName = "context")
public class Context implements Serializable {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlElementWrapper(localName = "extension", useWrapping = false)
    private List<Extension> extension;

    public String toXmlString() throws JsonProcessingException {
        ObjectMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(this);
    }
}
