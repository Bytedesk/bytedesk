/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:06:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 14:19:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.channel;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Page<ChannelResponse> query(ChannelRequest channelRequest) {

        Pageable pageable = PageRequest.of(channelRequest.getPageNumber(), channelRequest.getPageSize(),
                Sort.Direction.ASC,
                "id");

        Page<ChannelEntity> channelPage = channelRepository.findAll(pageable);

        return channelPage.map(channel -> convertToResponse(channel));
    }

    public ChannelEntity create(ChannelRequest request) {
        ChannelEntity channel = modelMapper.map(request, ChannelEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            channel.setUid(uidUtils.getUid());
        }

        return save(channel);
    }

    private ChannelEntity save(ChannelEntity channel) {
        return channelRepository.save(channel);
    }

    public ChannelResponse convertToResponse(ChannelEntity channel) {
        return modelMapper.map(channel, ChannelResponse.class);
    }

    // public void initData() {

    //     if (channelRepository.count() > 0) {
    //         return;
    //     }

    //     ChannelRequest channelRequest = ChannelRequest.builder()
    //             .topic(TopicUtils.TOPIC_SYSTEM_NOTIFICATION)
    //             .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
    //             .avatar(AvatarConsts.DEFAULT_SYSTEM_NOTIFICATION_AVATAR_URL)
    //             .description(I18Consts.I18N_SYSTEM_NOTIFICATION_DESCRIPTION)
    //             .build();
    //     channelRequest.setType(TypeConsts.TYPE_SYSTEM);
    //     // channelRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
    //     create(channelRequest);
    // }

}
