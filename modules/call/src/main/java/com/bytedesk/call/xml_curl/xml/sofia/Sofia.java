package com.bytedesk.call.xml_curl.xml.sofia;

import com.bytedesk.call.xml_curl.xml.Configuration;
import com.bytedesk.call.xml_curl.xml.sofia.profile.Profiles;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Sofia extends Configuration implements Serializable {

    @JacksonXmlProperty(localName = "global_settings", isAttribute = true)
    private GlobalSettings globalSettings;


    @JacksonXmlProperty(localName = "profiles", isAttribute = true)
    private Profiles profiles;

}
