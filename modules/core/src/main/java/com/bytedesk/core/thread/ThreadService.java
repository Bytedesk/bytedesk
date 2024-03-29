/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-11 15:03:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class ThreadService {

    private AuthService authService;

    private ModelMapper modelMapper;

    private UserService userService;

    private ThreadRepository threadRepository;

    public Page<Thread> query(BaseRequest pageParam) {

        User user = authService.getCurrentUser();

        // 优先加载最近更新的会话记录，updatedAt越大越新
        Pageable pageable = PageRequest.of(pageParam.getPageNumber(), pageParam.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        return threadRepository.findByUser(user, pageable);
    }

    public Thread create(ThreadRequest threadRequest) {

        User user = authService.getCurrentUser();

        Optional<Thread> threadOptional = threadRepository.findByTopicAndUser(threadRequest.getTopic(), user);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }

        Thread thread = modelMapper.map(threadRequest, Thread.class);
        if (!StringUtils.hasText(thread.getTid())) {
            thread.setTid(Utils.getUid());
        }
        // 
        thread.setUser(user);
        return threadRepository.save(thread);
    }
    
    @SuppressWarnings("null")
    public Thread getReverse(Thread thread) {
        
        String reverseTid = new StringBuffer(thread.getTid()).reverse().toString();
        Optional<Thread> reverseThreadOptional = findByTid(reverseTid);
        if (reverseThreadOptional.isPresent()) {
            reverseThreadOptional.get().setContent(thread.getContent());
            return save(reverseThreadOptional.get());
        } else {
            Optional<User> userOptional = userService.findByUid(thread.getTopic());
            if (!userOptional.isPresent()) {
                return null;
            }
            Thread reverseThread = new Thread();
            reverseThread.setTid(reverseTid);
            reverseThread.setTopic(thread.getUser().getUid());
            reverseThread.setAvatar(thread.getUser().getAvatar());
            reverseThread.setNickname(thread.getUser().getNickname());
            reverseThread.setContent(thread.getContent());
            // reverseThread.setExtra(thread.getExtra());
            reverseThread.setType(thread.getType());
            reverseThread.setUser(userOptional.get());
            // 
            return save(reverseThread);
        }
    }

    public Optional<Thread> findByTid(String tid) {
        return threadRepository.findByTid(tid);
    }

    public Thread save(@NonNull Thread thread) {
        return threadRepository.save(thread);
    }

    public void delete(@NonNull Thread thread) {
        threadRepository.delete(thread);
    }


}
