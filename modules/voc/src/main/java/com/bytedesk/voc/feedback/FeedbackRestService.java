/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:13:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FeedbackRestService extends BaseRestServiceWithExport<FeedbackEntity, FeedbackRequest, FeedbackResponse, FeedbackExcel> {

    private final FeedbackRepository feedbackRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<FeedbackEntity> createSpecification(FeedbackRequest request) {
        return FeedbackSpecification.search(request, authService);
    }

    @Override
    protected Page<FeedbackEntity> executePageQuery(Specification<FeedbackEntity> spec, Pageable pageable) {
        return feedbackRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "feedback", key = "#uid", unless="#result==null")
    @Override
    public Optional<FeedbackEntity> findByUid(String uid) {
        return feedbackRepository.findByUid(uid);
    }

    // @Cacheable(value = "feedback", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    // public Optional<FeedbackEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
    //     return feedbackRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return feedbackRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public FeedbackResponse create(FeedbackRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<FeedbackEntity> feedback = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (feedback.isPresent()) {
        //         return convertToResponse(feedback.get());
        //     }
        // }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        FeedbackEntity entity = modelMapper.map(request, FeedbackEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        FeedbackEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create feedback failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public FeedbackResponse update(FeedbackRequest request) {
        Optional<FeedbackEntity> optional = feedbackRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FeedbackEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FeedbackEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update feedback failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Feedback not found");
        }
    }

    @Override
    protected FeedbackEntity doSave(FeedbackEntity entity) {
        return feedbackRepository.save(entity);
    }

    @Override
    public FeedbackEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FeedbackEntity entity) {
        try {
            Optional<FeedbackEntity> latest = feedbackRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FeedbackEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return feedbackRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<FeedbackEntity> optional = feedbackRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // feedbackRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Feedback not found");
        }
    }

    @Override
    public void delete(FeedbackRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FeedbackResponse convertToResponse(FeedbackEntity entity) {
        return modelMapper.map(entity, FeedbackResponse.class);
    }

    @Override
    public FeedbackExcel convertToExcel(FeedbackEntity entity) {
        return modelMapper.map(entity, FeedbackExcel.class);
    }
    
    public void initFeedbacks(String orgUid) {
        // log.info("initThreadFeedback");
        // for (String feedback : FeedbackInitData.getAllFeedbacks()) {
        //     FeedbackRequest feedbackRequest = FeedbackRequest.builder()
        //             .uid(Utils.formatUid(orgUid, feedback))
        //             // .name(feedback)
        //             .type(FeedbackTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(feedbackRequest);
        // }
    }

    
    
}
