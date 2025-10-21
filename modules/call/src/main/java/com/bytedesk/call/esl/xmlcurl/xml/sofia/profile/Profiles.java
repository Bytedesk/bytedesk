package com.bytedesk.call.esl.xmlcurl.xml.sofia.profile;

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
@JacksonXmlRootElement(localName = "profiles", namespace = "profiles")
public class Profiles implements Serializable {

    @JacksonXmlElementWrapper(localName = "profile", useWrapping = false)
    private List<Profile> profile;

}
