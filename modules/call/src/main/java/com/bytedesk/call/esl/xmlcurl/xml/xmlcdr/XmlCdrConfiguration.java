package com.bytedesk.call.esl.xmlcurl.xml.xmlcdr;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.bytedesk.call.esl.xmlcurl.xmlcurl.Configuration;
import com.bytedesk.call.esl.xmlcurl.xmlcurl.sofia.setting.Settings;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 * @date 2023年09月14日 16:54
 */
@Data
@Accessors(chain = true)
public class XmlCdrConfiguration extends Configuration implements Serializable {

    @JacksonXmlElementWrapper(localName = "settings", useWrapping = false)
    private Settings settings;
}
