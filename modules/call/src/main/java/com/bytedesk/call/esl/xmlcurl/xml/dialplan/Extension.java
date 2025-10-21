package com.bytedesk.call.esl.xmlcurl.xml.dialplan;

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
public class Extension implements Serializable {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlElementWrapper(localName = "condition", useWrapping = false)
    private List<Condition> condition;
}
