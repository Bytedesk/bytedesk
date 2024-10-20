/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-14 09:35:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AssistantService {

    private final AssistantRepository assistantRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Page<AssistantResponse> query(AssistantRequest assistantRequest) {

        Pageable pageable = PageRequest.of(assistantRequest.getPageNumber(), assistantRequest.getPageSize(),
                Sort.Direction.ASC,
                "id");

        Page<Assistant> assistantPage = assistantRepository.findAll(pageable);

        return assistantPage.map(assistant -> convertToResponse(assistant));
    }

    public Assistant create(AssistantRequest assistantRequest) {

        Assistant assistant = modelMapper.map(assistantRequest, Assistant.class);
        if (!StringUtils.hasText(assistant.getUid())) {
            assistant.setUid(uidUtils.getUid());
        }

        return save(assistant);
    }

    private Assistant save(Assistant assistant) {
        return assistantRepository.save(assistant);
    }

    public AssistantResponse convertToResponse(Assistant assistant) {
        return modelMapper.map(assistant, AssistantResponse.class);
    }

    //
    public void initData() {

        if (assistantRepository.count() > 0) {
            return;
        }

        AssistantRequest assistantRequest = AssistantRequest.builder()
                .topic(TopicUtils.TOPIC_FILE_ASSISTANT)
                .nickname(I18Consts.I18N_FILE_ASSISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_FILE_ASSISTANT_AVATAR_URL)
                .description(I18Consts.I18N_FILE_ASSISTANT_DESCRIPTION)
                .build();
        assistantRequest.setUid(BdConstants.DEFAULT_FILE_ASSISTANT_UID);
        assistantRequest.setType(TypeConsts.TYPE_SYSTEM);
        // assistantRequest.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(assistantRequest);
    }

}
