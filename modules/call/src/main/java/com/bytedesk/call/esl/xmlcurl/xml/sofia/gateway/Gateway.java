package com.bytedesk.call.esl.xmlcurl.xml.sofia.gateway;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.bytedesk.call.esl.xmlcurl.xml.sofia.Param;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "gateway", namespace = "gateway")
public class Gateway implements Serializable {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlElementWrapper(localName = "param", useWrapping = false)
    private List<Param> param;
}
