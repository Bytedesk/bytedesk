package com.bytedesk.call.call_settings;

import java.time.ZonedDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

@Data
public class CallSettingsExcel {

    @ExcelProperty(index = 0, value = "客服UID")
    @ColumnWidth(24)
    private String agentUid;

    @ExcelProperty(index = 1, value = "是否启用")
    @ColumnWidth(12)
    private Boolean enabled;

    @ExcelProperty(index = 2, value = "签入状态")
    @ColumnWidth(12)
    private Boolean signedIn;

    @ExcelProperty(index = 3, value = "手机外呼")
    @ColumnWidth(12)
    private Boolean mobileOutboundEnabled;

    @ExcelProperty(index = 4, value = "手机号码")
    @ColumnWidth(20)
    private String mobilePhoneNumber;

    @ExcelProperty(index = 5, value = "对外号码")
    @ColumnWidth(20)
    private String number;

    @ExcelProperty(index = 6, value = "显示名称")
    @ColumnWidth(20)
    private String displayName;

    @ExcelProperty(index = 7, value = "内线/SIP目标")
    @ColumnWidth(24)
    private String target;

    @ExcelProperty(index = 8, value = "注册状态")
    @ColumnWidth(18)
    private String registrationStatus;

    @ExcelProperty(index = 9, value = "内线咨询号码")
    @ColumnWidth(24)
    private String consultExtensionNumbers;

    @ExcelProperty(index = 10, value = "转接号码")
    @ColumnWidth(24)
    private String transferTargetNumbers;

    @ExcelProperty(index = 11, value = "会议号码")
    @ColumnWidth(24)
    private String conferenceTargetNumbers;

    @ExcelProperty(index = 12, value = "转IVR号码")
    @ColumnWidth(24)
    private String ivrTargetNumbers;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime createdAt;

    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "更新时间", converter = com.bytedesk.core.converter.ZonedDateTimeConverter.class)
    @ColumnWidth(25)
    private ZonedDateTime updatedAt;
}
