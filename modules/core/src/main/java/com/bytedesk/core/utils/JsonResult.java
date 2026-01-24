/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-29 16:49:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 返回结果
 *
 * @author 270580156@qq.com
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private Integer code;

    private T data;

    public static JsonResult<Boolean> success() {
        return new JsonResult<Boolean>().setCode(200).setMessage("success").setData(true);
    }

    public static JsonResult<Boolean> success(String message) {
        return new JsonResult<Boolean>().setCode(200).setMessage(message).setData(true);
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<T>().setCode(200).setMessage("success").setData(data);
    }

    public static <T> JsonResult<T> success(String message, T data) {
        return new JsonResult<T>().setCode(200).setMessage(message).setData(data);
    }

    public static <T> JsonResult<T> success(String message, int code, T data) {
        return new JsonResult<T>().setCode(code).setMessage(message).setData(data);
    }

    public static JsonResult<Boolean> error() {
        return new JsonResult<Boolean>().setCode(500).setMessage("error").setData(false);
    }

    public static JsonResult<Boolean> error(String message) {
        return new JsonResult<Boolean>().setCode(500).setMessage(message).setData(false);
    }

    public static JsonResult<Boolean> error(String message, int code) {
        return new JsonResult<Boolean>().setCode(code).setMessage(message).setData(false);
    }

    public static <T> JsonResult<T> error(String message, int code, T data) {
        return new JsonResult<T>().setCode(code).setMessage(message).setData(data);
    }

}
