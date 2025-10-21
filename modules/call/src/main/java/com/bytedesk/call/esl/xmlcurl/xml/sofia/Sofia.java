package com.bytedesk.call.esl.xmlcurl.xml.sofia;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.bytedesk.call.esl.xmlcurl.xmlcurl.Configuration;
import com.bytedesk.call.esl.xmlcurl.xmlcurl.sofia.profile.Profiles;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
public class Sofia extends Configuration implements Serializable {

    @JacksonXmlProperty(localName = "global_settings", isAttribute = true)
    private GlobalSettings globalSettings;


    @JacksonXmlProperty(localName = "profiles", isAttribute = true)
    private Profiles profiles;

}
