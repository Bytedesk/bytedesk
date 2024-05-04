/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 17:00:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:55:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.doc;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KbDocService {

    private final KbDocRepository kbDocRepository;

    private final ModelMapper modelMapper;

    public Page<KbDocResponse> query(KbDocRequest kbDocRequest) {

        Pageable pageable = PageRequest.of(kbDocRequest.getPageNumber(), kbDocRequest.getPageSize(),
                Sort.Direction.DESC,
                "id");

        Page<KbDoc> kbDocPage = kbDocRepository.findAll(pageable);

        return kbDocPage.map(this::convertToDocResponse);
    }
    
    public KbDocResponse convertToDocResponse(KbDoc kbDoc) {
        return modelMapper.map(kbDoc, KbDocResponse.class);
    }
    
}
