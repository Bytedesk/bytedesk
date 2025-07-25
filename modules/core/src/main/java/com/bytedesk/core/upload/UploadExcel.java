/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-12 09:31:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 10:42:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.bytedesk.core.base.BaseExcel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UploadExcel extends BaseExcel {
    
    @ExcelProperty(value = "文件名")
    @ColumnWidth(25)
    private String fileName;

    @ExcelProperty(value = "文件大小")
    @ColumnWidth(15)
    private String fileSize;

    @ExcelProperty(value = "文件URL")
    @ColumnWidth(50)
    private String fileUrl;

    @ExcelProperty(value = "文件类型")
    @ColumnWidth(15)
    private String fileType;

    @ExcelProperty(value = "客户端")
    @ColumnWidth(15)
    private String channel;

    @ExcelProperty(value = "上传类型")
    @ColumnWidth(15)
    private String type;

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(value = "分类UID")
    @ColumnWidth(25)
    private String categoryUid;

    @ExcelProperty(value = "知识库UID")
    @ColumnWidth(25)
    private String kbUid;

    @ExcelProperty(value = "额外信息")
    @ColumnWidth(50)
    private String extra;

    @ExcelProperty(value = "上传用户")
    @ColumnWidth(25)
    private String user;

    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private String createdAt;
}
