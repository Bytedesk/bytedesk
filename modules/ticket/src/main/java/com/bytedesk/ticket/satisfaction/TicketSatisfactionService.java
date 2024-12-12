/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 13:38:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 15:34:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.satisfaction;

import java.time.LocalDateTime;
import java.util.Map;

public interface TicketSatisfactionService {
    // 提交评价
    void submitRating(Long ticketId, Integer rating, String comment);
    
    // 获取评价
    TicketSatisfactionEntity getTicketRating(Long ticketId);
    
    // 统计评价
    Map<Integer, Long> getRatingDistribution(LocalDateTime startTime, LocalDateTime endTime);
    Double getAverageRating(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 评价提醒
    void sendRatingReminder(Long ticketId);
} 