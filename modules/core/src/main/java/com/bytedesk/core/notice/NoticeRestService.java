/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-01 09:28:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:17:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NoticeRestService extends BaseRestService<NoticeEntity, NoticeRequest, NoticeResponse> {

    private NoticeRepository noticeRepository;

    private ModelMapper modelMapper;

    private UidUtils uidUtils;

    @Override
    public Page<NoticeResponse> queryByOrg(NoticeRequest request) {
        Pageable pageable = request.getPageable();
        Specification<NoticeEntity> specification = NoticeSpecification.search(request);
        Page<NoticeEntity> page = noticeRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<NoticeResponse> queryByUser(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "notice", key = "#uid", unless = "#result == null")
    @Override
    public Optional<NoticeEntity> findByUid(String uid) {
        return noticeRepository.findByUid(uid);
    }

    @Cacheable(value = "notice", key = "#messageUid", unless = "#result == null")
    public Optional<NoticeEntity> findByExtraContains(String messageUid) {
        return noticeRepository.findByExtraContains(messageUid);
    }

    @Cacheable(value = "notice", key = "#status + #messageUid", unless = "#result == null")
    public Optional<NoticeEntity> findByStatusAndExtraContains(String status, String messageUid) {
        return noticeRepository.findByStatusAndExtraContains(status, messageUid);
    }

    @Override
    public NoticeResponse initVisitor(NoticeRequest request) {
        NoticeEntity entity = modelMapper.map(request, NoticeEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        NoticeEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create notice failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public NoticeResponse update(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public NoticeResponse acceptTransfer(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.TRANSFER_ACCEPTED.name());
    }

    public NoticeResponse rejectTransfer(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.TRANSFER_REJECTED.name());
    }

    public NoticeResponse cancelTransfer(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.TRANSFER_CANCELED.name());
    }

    public NoticeResponse timeOutTransfer(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.TRANSFER_TIMEOUT.name());
    }

    public NoticeResponse acceptInvite(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.INVITE_ACCEPTED.name());
    }

    public NoticeResponse rejectInvite(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.INVITE_REJECTED.name());
    }

    public NoticeResponse cancelInvite(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.INVITE_CANCELED.name());
    }

    public NoticeResponse timeOutInvite(String messageUid) {
        return updateByMessageUid(messageUid, MessageStatusEnum.INVITE_TIMEOUT.name());
    }

    public NoticeResponse updateByMessageUid(String messageUid, String status) {
        Optional<NoticeEntity> entity = noticeRepository.findByStatusAndExtraContains(MessageStatusEnum.TRANSFER_PENDING.name(), messageUid);
        if (entity.isPresent()) {
            NoticeEntity noticeEntity = entity.get();
            noticeEntity.setStatus(status);
            // 
            NoticeEntity savedEntity = save(noticeEntity);
            if (savedEntity == null) {
                throw new RuntimeException("Update notice failed");
            }
            return convertToResponse(savedEntity);
        }
        return null;
    }

    @Override
    public NoticeEntity save(NoticeEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected NoticeEntity doSave(NoticeEntity entity) {
        return noticeRepository.save(entity);
    }

    @Override
    public NoticeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            NoticeEntity entity) {
        try {
            Optional<NoticeEntity> latest = noticeRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                NoticeEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return noticeRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<NoticeEntity> entity = noticeRepository.findByUid(uid);
        if (entity.isPresent()) {
            // noticeRepository.delete(entity.get());
            entity.get().setDeleted(true);
            save(entity.get());
        }
    }

    @Override
    public void delete(NoticeRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public NoticeResponse convertToResponse(NoticeEntity entity) {
        return modelMapper.map(entity, NoticeResponse.class);
    }

    @Override
    public NoticeResponse queryByUid(NoticeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
