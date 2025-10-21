package com.bytedesk.call.xmlcurl.xml.sofia.aliase;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
public class Alias implements Serializable {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;
}
