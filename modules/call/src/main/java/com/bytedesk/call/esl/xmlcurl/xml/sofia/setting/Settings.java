package com.bytedesk.call.esl.xmlcurl.xml.sofia.setting;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
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
public class Settings implements Serializable {

    @JacksonXmlElementWrapper(localName = "param", useWrapping = false)
    private List<Param> param;
}
