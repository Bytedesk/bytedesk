/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:04:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-26 10:33:02
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
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AsistantService {

    private final AsistantRepository asistantRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Page<AsistantResponse> query(AsistantRequest asistantRequest) {

        Pageable pageable = PageRequest.of(asistantRequest.getPageNumber(), asistantRequest.getPageSize(),
                Sort.Direction.ASC,
                "id");

        Page<Asistant> asistantPage = asistantRepository.findAll(pageable);

        return asistantPage.map(asistant -> convertToResponse(asistant));
    }

    public Asistant create(AsistantRequest asistantRequest) {

        Asistant asistant = modelMapper.map(asistantRequest, Asistant.class);
        if (!StringUtils.hasText(asistant.getUid())) {
            asistant.setUid(uidUtils.getUid());
        }

        return save(asistant);
    }

    private Asistant save(Asistant asistant) {
        return asistantRepository.save(asistant);
    }

    public AsistantResponse convertToResponse(Asistant asistant) {
        return modelMapper.map(asistant, AsistantResponse.class);
    }

    //
    public void initData() {

        if (asistantRepository.count() > 0) {
            return;
        }

        AsistantRequest asistantRequest = AsistantRequest.builder()
                .topic(TopicUtils.TOPIC_FILE_ASISTANT)
                .nickname(I18Consts.I18N_FILE_ASISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_FILE_ASISTANT_AVATAR_URL)
                .description(I18Consts.I18N_FILE_ASISTANT_DESCRIPTION)
                .build();
        asistantRequest.setUid(BdConstants.DEFAULT_FILE_ASISTANT_UID);
        asistantRequest.setType(TypeConsts.TYPE_SYSTEM);
        // asistantRequest.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(asistantRequest);
    }

}
