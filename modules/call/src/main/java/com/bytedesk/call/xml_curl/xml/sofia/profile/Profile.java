package com.bytedesk.call.xml_curl.xml.sofia.profile;

import com.bytedesk.call.xml_curl.xml.sofia.aliase.Aliases;
import com.bytedesk.call.xml_curl.xml.sofia.domain.Domains;
import com.bytedesk.call.xml_curl.xml.sofia.gateway.Gateways;
import com.bytedesk.call.xml_curl.xml.sofia.setting.Settings;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
public class Profile implements Serializable {
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlProperty(localName = "gateways", isAttribute = true)
    private Gateways gateways;

    @JacksonXmlProperty(localName = "aliases", isAttribute = true)
    private Aliases aliases;

    @JacksonXmlProperty(localName = "domains", isAttribute = true)
    private Domains domains;

    @JacksonXmlProperty(localName = "settings", isAttribute = true)
    private Settings settings;
}
