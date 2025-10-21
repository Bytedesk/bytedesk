package com.bytedesk.call.xml_curl.xml.group;

import com.bytedesk.call.xml_curl.xml.user.Users;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Group implements Serializable {

    @JacksonXmlProperty(localName = "users", isAttribute = true)
    private Users users;
}
