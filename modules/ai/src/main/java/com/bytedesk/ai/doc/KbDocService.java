/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 17:00:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-27 11:55:19
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

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KbDocService extends BaseService<KbDoc, KbDocRequest, KbDocResponse> {

    // private final KbDocRepository kbDocRepository;

    // private final ModelMapper modelMapper;

    @Override
    public Page<KbDocResponse> queryByOrg(KbDocRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<KbDocResponse> queryByUser(KbDocRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<KbDoc> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public KbDocResponse create(KbDocRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public KbDocResponse update(KbDocRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public KbDoc save(KbDoc entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(KbDoc entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, KbDoc entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public KbDocResponse convertToResponse(KbDoc entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    
    // public Page<KbDocResponse> query(KbDocRequest kbDocRequest) {

    //     Pageable pageable = PageRequest.of(kbDocRequest.getPageNumber(), kbDocRequest.getPageSize(),
    //             Sort.Direction.DESC,
    //             "id");

    //     Page<KbDoc> kbDocPage = kbDocRepository.findAll(pageable);

    //     return kbDocPage.map(this::convertToDocResponse);
    // }
    
    // public KbDocResponse convertToDocResponse(KbDoc kbDoc) {
    //     return modelMapper.map(kbDoc, KbDocResponse.class);
    // }
    
}
