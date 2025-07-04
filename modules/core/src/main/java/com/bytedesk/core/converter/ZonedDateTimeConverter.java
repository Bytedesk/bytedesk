/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-04 18:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 18:10:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

/**
 * ZonedDateTime类型的Excel转换器
 * 
 * 用于处理ZonedDateTime类型的字段导出到Excel时的转换
 * 
 * @author jackning
 * @version 1.0
 * @since 2025-07-04
 */
public class ZonedDateTimeConverter implements Converter<ZonedDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Class<?> supportJavaTypeKey() {
        return ZonedDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<ZonedDateTime> context) throws Exception {
        ZonedDateTime value = context.getValue();
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(value.format(FORMATTER));
    }
}
