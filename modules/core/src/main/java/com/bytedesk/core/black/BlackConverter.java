/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-06 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:08:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * BlackResponse与BlackExcel转换工具类
 */
public class BlackConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 将BlackResponse对象转换为BlackExcel对象
     * @param response BlackResponse对象
     * @return BlackExcel对象
     */
    public static BlackExcel toExcel(BlackEntity response) {
        if (response == null) {
            return null;
        }

        BlackExcel excel = new BlackExcel();
        excel.setBlackUid(response.getBlackUid());
        excel.setBlackNickname(response.getBlackNickname());
        excel.setBlackAvatar(response.getBlackAvatar());
        excel.setReason(response.getReason());
        excel.setBlockIp(response.getBlockIp() ? "是" : "否");
        excel.setUserUid(response.getUserUid());
        excel.setUserNickname(response.getUserNickname());
        excel.setStartTime(response.getStartTime() != null ? response.getStartTime().format(DATE_FORMATTER) : "");
        excel.setEndTime(response.getEndTime() != null ? response.getEndTime().format(DATE_FORMATTER) : "");
        excel.setThreadTopic(response.getThreadTopic());

        return excel;
    }

    /**
     * 批量将BlackResponse对象转换为BlackExcel对象
     * @param responseList BlackResponse对象列表
     * @return BlackExcel对象列表
     */
    public static List<BlackExcel> toExcelList(List<BlackEntity> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return new ArrayList<>();
        }

        List<BlackExcel> excelList = new ArrayList<>(responseList.size());
        for (BlackEntity response : responseList) {
            BlackExcel excel = toExcel(response);
            if (excel != null) {
                excelList.add(excel);
            }
        }
        return excelList;
    }
}