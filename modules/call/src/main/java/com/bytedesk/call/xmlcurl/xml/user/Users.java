package com.bytedesk.call.xmlcurl.xml.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Users implements Serializable {
    @JacksonXmlElementWrapper(localName = "user", useWrapping = false)
    private List<User> user;
}
