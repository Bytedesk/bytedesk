package com.bytedesk.core.controller;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.StablePageResult;

@RestControllerAdvice
public class PageResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        if (body instanceof Page<?> page) {
            return StablePageResult.from(page);
        }

        if (body instanceof JsonResult<?> jsonResult) {
            return normalizeJsonResult(jsonResult);
        }

        return body;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JsonResult<?> normalizeJsonResult(JsonResult<?> jsonResult) {
        Object data = jsonResult.getData();
        if (data instanceof Page<?> page) {
            ((JsonResult) jsonResult).setData(StablePageResult.from(page));
        }
        return jsonResult;
    }
}