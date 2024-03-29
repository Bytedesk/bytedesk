/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 11:59:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
 * @author im.bytedesk.com
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = -8419736980541550530L;

    private String message;

    private int code;

    private T data;

    public static <T> JsonResult<T> success() {
        return new JsonResult<T>().setCode(200).setMessage("success");
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<T>().setCode(200).setMessage("success").setData(data);
    }

    public static <T> JsonResult<T> success(String message, int statusCode, T data) {
        return new JsonResult<T>().setCode(statusCode).setMessage(message).setData(data);
    }

    public static <T> JsonResult<T> error() {
        return new JsonResult<T>().setCode(500).setMessage("error");
    }

    public static <T> JsonResult<T> error(String message) {
        return new JsonResult<T>().setCode(500).setMessage(message);
    }

    public static <T> JsonResult<T> error(String message, int statusCode, T data) {
        return new JsonResult<T>().setCode(statusCode).setMessage(message).setData(data);
    }
}
