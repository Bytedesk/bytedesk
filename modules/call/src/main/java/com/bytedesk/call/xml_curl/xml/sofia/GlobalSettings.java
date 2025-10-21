package com.bytedesk.call.xml_curl.xml.sofia;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "global_settings", namespace = "global_settings")
public class GlobalSettings implements Serializable {

    /**
     * log-level
     * auto-restart
     * debug-presence
     * capture-server
     */
    @JacksonXmlElementWrapper(localName = "param", useWrapping = false)
    private List<Param> param;
}
