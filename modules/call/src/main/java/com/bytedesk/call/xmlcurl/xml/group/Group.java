package com.bytedesk.call.xmlcurl.xml.group;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.bytedesk.call.xmlcurl.xml.user.Users;
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
