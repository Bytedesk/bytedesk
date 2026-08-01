/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:00:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.embedding_settings;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * https://github.com/alibaba/easyexcel
 */
@Data
public class EmbeddingSettingsExcel {

    @ExcelProperty(index = 0, value = "配置名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty(index = 1, value = "供应商")
    @ColumnWidth(20)
    private String provider;

    @ExcelProperty(index = 2, value = "模型")
    @ColumnWidth(20)
    private String model;

    @ExcelProperty(index = 3, value = "Base URL")
    @ColumnWidth(30)
    private String baseUrl;

    @ExcelProperty(index = 4, value = "向量维度")
    @ColumnWidth(15)
    private Integer dimensions;

    @ExcelProperty(index = 5, value = "索引名称")
    @ColumnWidth(25)
    private String vectorStoreIndexName;

    @ExcelProperty(index = 6, value = "默认")
    @ColumnWidth(10)
    private Boolean defaultSettings;

    @ExcelProperty(index = 7, value = "启用")
    @ColumnWidth(10)
    private Boolean enabled;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(index = 8, value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

}
