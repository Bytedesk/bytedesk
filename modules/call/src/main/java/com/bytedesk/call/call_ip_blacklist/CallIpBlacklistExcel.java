package com.bytedesk.call.call_ip_blacklist;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

@Data
public class CallIpBlacklistExcel {

    @ExcelProperty(index = 0, value = "IP地址")
    @ColumnWidth(20)
    private String ipAddress;

    @ExcelProperty(index = 1, value = "来源ESL事件UID")
    @ColumnWidth(24)
    private String sourceEslEventUid;

    @ExcelProperty(index = 2, value = "事件名称")
    @ColumnWidth(20)
    private String eventName;

    @ExcelProperty(index = 3, value = "主叫号码")
    @ColumnWidth(20)
    private String callerNumber;

    @ExcelProperty(index = 4, value = "拉黑原因")
    @ColumnWidth(40)
    private String reason;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "更新时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime updatedAt;
}