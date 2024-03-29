/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-11 13:54:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bytedesk.core.utils.BdConvertUtils;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<MessageResponse> query(MessageRequest request) {

        // 优先加载最新聊天记录，也即：id越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Message> messagePage = messageRepository.findByThreadsTidIn(request.getThreads(), pageable);

        return messagePage.map(BdConvertUtils::convertToMessageResponse);
    }

    public Optional<Message> findByMid(String mid) {
        return messageRepository.findByMid(mid);
    }

    public Message save(@NonNull Message message) {
        return messageRepository.save(message);
    }

    public void delete(@NonNull Message message) {
        messageRepository.delete(message);
    }

    public void deleteByMid(String mid) {
        messageRepository.deleteByMid(mid);
    }
}
