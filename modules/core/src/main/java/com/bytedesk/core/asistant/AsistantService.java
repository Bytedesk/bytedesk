/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-08 10:25:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.asistant;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TopicConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AsistantService {
    
    private final AsistantRepository asistantRepository;

    // private final UserService userService;

    // private final ThreadService threadService;

    private final ModelMapper modelMapper;
    
    private final UidUtils uidUtils;

    public Page<AsistantResponse> query(AsistantRequest asistantRequest) {
        
        Pageable pageable = PageRequest.of(asistantRequest.getPageNumber(), asistantRequest.getPageSize(), Sort.Direction.ASC,
                "id");

        Page<Asistant> asistantPage = asistantRepository.findAll(pageable);

        return asistantPage.map(asistant -> convertToAsistantResponse(asistant));
    }

    public Asistant create(AsistantRequest asistantRequest) {

        Asistant asistant = modelMapper.map(asistantRequest, Asistant.class);
        if (!StringUtils.hasText(asistant.getUid())) {
            asistant.setUid(uidUtils.getCacheSerialUid());
        }
        
        return save(asistant);
    }
    
    private Asistant save(Asistant asistant) {
        return asistantRepository.save(asistant);
    }

    public AsistantResponse convertToAsistantResponse(Asistant asistant) {
        return modelMapper.map(asistant, AsistantResponse.class);
    }


    // 
    public void initData() {
        
        if (asistantRepository.count() > 0) {
            return;
        }

        // Optional<User> adminOptional = userService.getAdmin();

        AsistantRequest asistantRequest = AsistantRequest.builder()
                .topic(TopicConsts.TOPIC_FILE_ASISTANT)
                .name(I18Consts.I18N_FILE_ASISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_FILE_ASISTANT_AVATAR_URL)
                .description(I18Consts.I18N_FILE_ASISTANT_DESCRIPTION)
                // .orgUid(adminOptional.get().getOrgUid())
                .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .build();
        asistantRequest.setUid(UserConsts.DEFAULT_FILE_ASISTANT_UID);
        asistantRequest.setType(TypeConsts.TYPE_SYSTEM);
        create(asistantRequest);

        // 方便测试，默认给每个初始用户生成一个跟 文件助手 的对话
        // UserRequest userRequest = new UserRequest();
        // userRequest.setPageNumber(0);
        // userRequest.setPageSize(10);
        // // 
        // Page<User> userPage = userService.query(userRequest);
        // userPage.forEach(user -> {
        //     // 
        //     UserResponseSimple userSimple = UserResponseSimple.builder()
        //             // .uid(asistantRequest.getAid())
        //             .nickname(asistantRequest.getName())
        //             .avatar(asistantRequest.getAvatar())
        //             .build();
        //     userSimple.setUid(asistantRequest.getUid());
        //     // 
        //     Thread thread = Thread.builder()
        //             // .tid(uidUtils.getCacheSerialUid())
        //             .type(ThreadTypeConsts.ASISTANT)
        //             .topic(TopicConsts.TOPIC_FILE_ASISTANT + "/" + user.getUid())
        //             .status(StatusConsts.THREAD_STATUS_INIT)
        //             .client(TypeConsts.TYPE_SYSTEM)
        //             .user(JSON.toJSONString(userSimple))
        //             .owner(user)
        //             .orgUid(asistantRequest.getOrgUid())
        //             .build();
        //     thread.setUid(uidUtils.getCacheSerialUid());
            
        //     threadService.save(thread);
        // });

    }



}
