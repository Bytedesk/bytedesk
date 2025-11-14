/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-28 10:40:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:17:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_template;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

@Service
public class EmailTemplateService extends BaseRestService<EmailTemplateEntity, EmailTemplateRequest, EmailTemplateResponse> {

    @Override
    public Page<EmailTemplateResponse> queryByOrg(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<EmailTemplateResponse> queryByUser(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<EmailTemplateEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public EmailTemplateResponse create(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public EmailTemplateResponse update(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public EmailTemplateEntity save(EmailTemplateEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected EmailTemplateEntity doSave(EmailTemplateEntity entity) {
        throw new UnsupportedOperationException("实现保存逻辑");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public EmailTemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            EmailTemplateEntity entity) {
        throw new UnsupportedOperationException("实现乐观锁处理逻辑");
    }

    @Override
    public EmailTemplateResponse convertToResponse(EmailTemplateEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    @Override
    public EmailTemplateResponse queryByUid(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    protected Specification<EmailTemplateEntity> createSpecification(EmailTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createSpecification'");
    }

    @Override
    protected Page<EmailTemplateEntity> executePageQuery(Specification<EmailTemplateEntity> specification, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executePageQuery'");
    }
    
}
