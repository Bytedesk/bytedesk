package com.bytedesk.core.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.constant.I18Consts;

/**
 * 验证 MVC 异常经真实分派链进入 GlobalExceptionHandler 的新增精确 handler：
 * 4xx 客户端输入错误返回对应状态码 + 业务码，转换能力缺失仍保持 500。
 */
class GlobalExceptionHandlerMvcTest {

    private MockMvc mockMvc;

    @RestController
    static class FixtureController {

        @PostMapping(value = "/json-only", consumes = MediaType.APPLICATION_JSON_VALUE)
        String jsonOnly(@RequestBody String body) {
            return body;
        }

        @PostMapping("/upload-part")
        String uploadPart(@RequestPart("file") MultipartFile file) {
            return file.getOriginalFilename();
        }

        @GetMapping("/required-param")
        String requiredParam(@RequestParam("msgUid") String msgUid) {
            return msgUid;
        }

        @GetMapping("/convert")
        String convert(@RequestParam("value") Long value) {
            return String.valueOf(value);
        }

        @GetMapping("/boom")
        String boom() {
            throw new ConversionNotSupportedException("value", String.class, null);
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FixtureController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void multipartPostedToXmlStringEndpointReturns415WithBusinessCode() throws Exception {
        mockMvc.perform(post("/json-only")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content("--b\r\nContent-Disposition: form-data; name=\"x\"\r\n\r\n1\r\n--b--\r\n"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value(415))
                .andExpect(jsonPath("$.message").value(I18Consts.I18N_HTTP_MEDIA_TYPE_NOT_SUPPORTED));
    }

    @Test
    void missingRequiredPartReturns400WithBusinessCode() throws Exception {
        mockMvc.perform(post("/upload-part")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content("--b\r\nContent-Disposition: form-data; name=\"other\"\r\n\r\n1\r\n--b--\r\n"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(I18Consts.I18N_MISSING_REQUEST_PART));
    }

    @Test
    void missingRequiredParameterReturns400WithBusinessCode() throws Exception {
        mockMvc.perform(get("/required-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(I18Consts.I18N_MISSING_REQUEST_PARAMETER));
    }

    @Test
    void parameterTypeMismatchReturns400WithBusinessCode() throws Exception {
        mockMvc.perform(get("/convert").param("value", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(I18Consts.I18N_TYPE_MISMATCH));
    }

    @Test
    void conversionNotSupportedKeepsServerError() throws Exception {
        mockMvc.perform(get("/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(I18Consts.I18N_INTERNAL_SERVER_ERROR));
    }

    @Test
    void handlerMapsExceptionsDirectlyForNonMvcCallers() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        var response = handler.handleHttpMediaTypeNotSupportedException(
                new org.springframework.web.HttpMediaTypeNotSupportedException(
                        MediaType.MULTIPART_FORM_DATA, java.util.List.of(MediaType.APPLICATION_JSON)));

        assertThat(response.getStatusCode().value()).isEqualTo(415);
    }
}
