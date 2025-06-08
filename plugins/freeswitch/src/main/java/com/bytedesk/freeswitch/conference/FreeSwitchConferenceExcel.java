/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.conference;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * FreeSwitch会议室Excel导出类
 * https://github.com/alibaba/easyexcel
 */
@Data
public class FreeSwitchConferenceExcel {

    @ExcelProperty(index = 0, value = "会议室名称")
    @ColumnWidth(20)
    private String conferenceName;

    @ExcelProperty(index = 1, value = "描述")
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(index = 2, value = "最大成员数")
    @ColumnWidth(15)
    private Integer maxMembers;

    @ExcelProperty(index = 3, value = "是否启用")
    @ColumnWidth(15)
    private Boolean enabled;

    @ExcelProperty(index = 4, value = "是否录音")
    @ColumnWidth(15)
    private Boolean recordEnabled;

    @ExcelProperty(index = 5, value = "是否有密码")
    @ColumnWidth(15)
    private Boolean passwordProtected;

    @ExcelProperty(index = 6, value = "主持人PIN")
    @ColumnWidth(20)
    private String moderatorPin;

    @ExcelProperty(index = 7, value = "参会者PIN")
    @ColumnWidth(20)
    private String participantPin;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(25)
    private LocalDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "修改时间")
    @ColumnWidth(25)
    private LocalDateTime updatedAt;

}
