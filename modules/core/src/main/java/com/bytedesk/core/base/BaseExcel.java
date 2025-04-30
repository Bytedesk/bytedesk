/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-10 09:38:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 21:35:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.io.Serializable;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class BaseExcel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ExcelProperty(index = 0, value = "唯一Uid")
    @ColumnWidth(20)
    private String uid;

}
