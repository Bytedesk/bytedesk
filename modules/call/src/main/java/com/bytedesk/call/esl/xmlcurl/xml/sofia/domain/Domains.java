package com.bytedesk.call.esl.xmlcurl.xml.sofia.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
public class Domains implements Serializable {

    @JacksonXmlElementWrapper(localName = "domain", useWrapping = false)
    private List<Domain> domain;
}
