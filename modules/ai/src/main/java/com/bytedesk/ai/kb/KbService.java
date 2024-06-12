/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:46:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-27 14:48:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.kb;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KbService {

    private final KbRepository kbRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Page<KbResponse> query(KbRequest kbRequest) {

        Pageable pageable = PageRequest.of(kbRequest.getPageNumber(), kbRequest.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Kb> kbList = kbRepository.findAll(pageable);
        // 
        return kbList.map(this::convertToKbResponse);
    }

    public JsonResult<?> create(KbRequest kbRequest) {

        Kb kb = modelMapper.map(kbRequest, Kb.class);
        kb.setUid(uidUtils.getCacheSerialUid());

        // kb.setUser(authService.getCurrentUser());

        kbRepository.save(kb);

        return JsonResult.success();
    }

    public KbResponse convertToKbResponse(Kb kb) {
        return modelMapper.map(kb, KbResponse.class);
    }

    public Kb getKb(String name, String orgUid) {

        Kb kb = Kb.builder()
                // .kid(uidUtils.getCacheSerialUid())
                .name(name)
                .vectorStore("redis")
                .embeddings("m3e-base")
                .orgUid(orgUid)
                .build();
        kb.setUid(uidUtils.getCacheSerialUid());

        return kbRepository.save(kb);
    }


}
