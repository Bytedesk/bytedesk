/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 14:38:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 09:04:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.utils;

import org.modelmapper.ModelMapper;

import com.bytedesk.ticket.statistic.TicketStatisticEntity;
import com.bytedesk.ticket.statistic.TicketStatisticResponse;

public class TicketStatisticConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper();


    public static TicketStatisticResponse convertToStatisticResponse(TicketStatisticEntity entity) {
        return modelMapper.map(entity, TicketStatisticResponse.class);
    }
    
}
