package com.bytedesk.call.xmlcurl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bytedesk.call.xml_curl.XmlCurlController;
import com.bytedesk.call.xml_curl.XmlCurlService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = XmlCurlController.class, properties = {
        "bytedesk.call.freeswitch.xmlcurl.enabled=true"
})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import({XmlCurlService.class})
class XmlCurlControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void get_directory_ok() throws Exception {
        mockMvc.perform(get("/freeswitch/xmlcurl")
                        .param("type", "directory")
                        .param("domain", "default")
                        .param("user", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    void post_dialplan_ok() throws Exception {
        mockMvc.perform(post("/freeswitch/xmlcurl")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("type=dialplan&context=default&destination_number=1000&playback=ivr/ivr-welcome.wav&bridge=sofia/gateway/gw/${destination_number}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    void post_missing_type_bad_request() throws Exception {
        mockMvc.perform(post("/freeswitch/xmlcurl")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("context=default"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    void get_phrases_and_configuration() throws Exception {
        mockMvc.perform(get("/freeswitch/xmlcurl")
                        .param("type", "phrases")
                        .param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));

        mockMvc.perform(get("/freeswitch/xmlcurl")
                        .param("type", "configuration")
                        .param("conf_name", "sofia"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }
}
