/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:59:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:57:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.file;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KbFileService {

    private final KbFileRepository kbFileRepository;

    private final ModelMapper modelMapper;

    public Page<KbFileResponse> query(KbFileRequest kbFileRequest) {

        Pageable pageable = PageRequest.of(kbFileRequest.getPageNumber(), kbFileRequest.getPageSize(),
                Sort.Direction.DESC,
                "id");

        Page<KbFile> kbFiles = kbFileRepository.findAll(pageable);

        return kbFiles.map(this::convertToKbFileResponse);
    }
    
    public KbFileResponse convertToKbFileResponse(KbFile kbFile) {
        return modelMapper.map(kbFile, KbFileResponse.class);
    }

    
    
}
