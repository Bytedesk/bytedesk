/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 18:13:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq_rating;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.utils.KbaseConvertUtils;
import com.bytedesk.kbase.faq.FaqRepository;
import com.bytedesk.kbase.faq.FaqEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FaqRatingRestService extends BaseRestService<FaqRatingEntity, FaqRatingRequest, FaqRatingResponse> {

    private final FaqRatingRepository faqRatingRepository;

    private final FaqRepository faqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<FaqRatingResponse> queryByOrg(FaqRatingRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FaqRatingEntity> spec = FaqRatingSpecification.search(request);
        Page<FaqRatingEntity> page = faqRatingRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FaqRatingResponse> queryByUser(FaqRatingRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "faq_rating", key = "#uid", unless="#result==null")
    @Override
    public Optional<FaqRatingEntity> findByUid(String uid) {
        return faqRatingRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return faqRatingRepository.existsByUid(uid);
    }

    @Override
    public FaqRatingResponse create(FaqRatingRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        FaqRatingEntity entity = modelMapper.map(request, FaqRatingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        Optional<FaqEntity> faqOptional = faqRepository.findByUid(request.getFaqUid());
        if (faqOptional.isPresent()) {
            entity.setFaq(faqOptional.get());
        }
        // 
        FaqRatingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create faq_rating failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FaqRatingResponse update(FaqRatingRequest request) {
        Optional<FaqRatingEntity> optional = faqRatingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FaqRatingEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            FaqRatingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update faq_rating failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("FaqRating not found");
        }
    }


    @Override
    protected FaqRatingEntity doSave(FaqRatingEntity entity) {
        // log.info("Attempting to save faq_rating: {}", entity.getName());
        return faqRatingRepository.save(entity);
    }

    @Override
    public FaqRatingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FaqRatingEntity entity) {
        try {
            Optional<FaqRatingEntity> latest = faqRatingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FaqRatingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return faqRatingRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking exception: {}", ex.getMessage());
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FaqRatingEntity> optional = faqRatingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // faq_ratingRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("FaqRating not found");
        }
    }

    @Override
    public void delete(FaqRatingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public FaqRatingResponse convertToResponse(FaqRatingEntity entity) {
        FaqRatingResponse response = modelMapper.map(entity, FaqRatingResponse.class);
        response.setFaq(KbaseConvertUtils.convertToFaqResponseSimple(entity.getFaq()));
        // 处理用户信息
        if (entity.getUser() != null) {
            response.setUser(UserProtobuf.fromJson(entity.getUser()));
        }
        return response;
    }

    @Override
    public FaqRatingResponse queryByUid(FaqRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
