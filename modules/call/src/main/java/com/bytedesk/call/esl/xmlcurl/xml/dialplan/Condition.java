package com.bytedesk.call.esl.xmlcurl.xml.dialplan;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Accessors(chain = true)
@Data
public class Condition implements Serializable {

    @JacksonXmlProperty(localName = "field", isAttribute = true)
    private String field;

    /**
     * 匹配被叫分机号正则表达式
     */
    @JacksonXmlProperty(localName = "expression", isAttribute = true)
    private String expression;

    @JacksonXmlProperty(localName = "require-nested", isAttribute = true)
    private String requireNested;

    @JacksonXmlElementWrapper(localName = "action", useWrapping = false)
    private List<Action> action;


}
