package com.bytedesk.call.xml_curl.xml.sofia.aliase;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author howell
 */
@Data
@Accessors(chain = true)
public class Aliases implements Serializable {
    @JacksonXmlElementWrapper(localName = "alias", useWrapping = false)
    private List<Alias> alias;
}
