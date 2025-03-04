/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 09:58:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

// https://github.com/alibaba/easyexcel
// https://easyexcel.opensource.alibaba.com/docs/current/
@Data
public class QueueExcel {

    @ExcelProperty(index = 0, value = "队列名称")
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(index = 1, value = "队列类型")
    @ColumnWidth(15)
    private String type;

    @ExcelProperty(index = 2, value = "队列主题")
    @ColumnWidth(20)
    private String topic;

    @ExcelProperty(index = 3, value = "队列日期")
    @ColumnWidth(15)
    private String day;

    @ExcelProperty(index = 4, value = "队列状态")
    @ColumnWidth(15)
    private String status;

    @ExcelProperty(index = 5, value = "当前号码")
    @ColumnWidth(10)
    private Integer currentNumber;

    @ExcelProperty(index = 6, value = "等待人数")
    @ColumnWidth(10)
    private Integer waitingNumber;

    @ExcelProperty(index = 7, value = "服务中人数")
    @ColumnWidth(10)
    private Integer servingNumber;

    @ExcelProperty(index = 8, value = "已完成人数")
    @ColumnWidth(10)
    private Integer servedNumber;

    @ExcelProperty(index = 9, value = "平均等待时间(秒)")
    @ColumnWidth(15)
    private Integer avgWaitTime;

    @ExcelProperty(index = 10, value = "平均解决时间(秒)")
    @ColumnWidth(15)
    private Integer avgSolveTime;

    /**
     * 从QueueEntity创建QueueExcel
     */
    // public static QueueExcel from(QueueEntity queue) {
    //     QueueExcel excel = new QueueExcel();
    //     excel.setNickname(queue.getNickname());
    //     excel.setType(queue.getType());
    //     excel.setTopic(queue.getTopic());
    //     excel.setDay(queue.getDay());
    //     excel.setStatus(queue.getStatus());
    //     excel.setCurrentNumber(queue.getCurrentNumber());
    //     excel.setWaitingNumber(queue.getWaitingNumber());
    //     excel.setServingNumber(queue.getServingNumber());
    //     excel.setServedNumber(queue.getServedNumber());
    //     excel.setAvgWaitTime(queue.getAvgWaitTime());
    //     excel.setAvgSolveTime(queue.getAvgSolveTime());
    //     return excel;
    // }
}
