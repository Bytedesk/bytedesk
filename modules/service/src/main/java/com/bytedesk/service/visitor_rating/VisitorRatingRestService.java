/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:01:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:21:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_rating;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VisitorRatingRestService extends BaseRestService<VisitorRatingEntity, VisitorRatingRequest, VisitorRatingResponse> {

    private final VisitorRatingRepository VisitorRatingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    @Override
    public Page<VisitorRatingResponse> queryByOrg(VisitorRatingRequest request) {
        Pageable pageable = request.getPageable();
        Specification<VisitorRatingEntity> specifications = VisitorRatingSpecification.search(request);
        Page<VisitorRatingEntity> page = VisitorRatingRepository.findAll(specifications, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<VisitorRatingResponse> queryByUser(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<VisitorRatingEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public VisitorRatingResponse create(VisitorRatingRequest request) {
        // 如果同一条会话多次评价，则以最新评价为准
        Optional<VisitorRatingEntity> rateOptional = VisitorRatingRepository.findByThreadUid(request.getThreadUid());
        if (rateOptional.isPresent()) {
            VisitorRatingEntity rate = rateOptional.get();
            rate.setScore(request.getScore());
            rate.setComment(request.getComment());
            // 
            VisitorRatingEntity savedVisitorRating = save(rate);
            if (savedVisitorRating == null) {
                throw new RuntimeException("save rate failed");
            }
            return convertToResponse(savedVisitorRating);
        }
        // 
        VisitorRatingEntity rate = modelMapper.map(request, VisitorRatingEntity.class);
        rate.setUid(uidUtils.getUid());
        // 更新thread平台状态
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(request.getThreadUid());
        if (threadOptional.isPresent()) {
            ThreadEntity thread = threadOptional.get();
            // thread.setRated(true);
            threadRestService.save(thread);
            // 
            rate.setAgent(thread.getAgent());
            rate.setUser(thread.getUser());
        }
        // 
        VisitorRatingEntity savedVisitorRating = save(rate);
        if (savedVisitorRating == null) {
            throw new RuntimeException("save rate failed");
        }

        return convertToResponse(savedVisitorRating);
    }

    @Override
    public VisitorRatingResponse update(VisitorRatingRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public VisitorRatingEntity save(VisitorRatingEntity entity) {
        try {
            return VisitorRatingRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(VisitorRatingRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, VisitorRatingEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    public Page<VisitorRatingEntity> queryByOrgExcel(VisitorRatingRequest request) {
        Pageable pageable = request.getPageable();
        Specification<VisitorRatingEntity> spec = VisitorRatingSpecification.search(request);
        return VisitorRatingRepository.findAll(spec, pageable);
    }
    
    public VisitorRatingExcel convertToExcel(VisitorRatingEntity VisitorRating) {
        return modelMapper.map(VisitorRating, VisitorRatingExcel.class);
    }

    @Override
    public VisitorRatingResponse convertToResponse(VisitorRatingEntity entity) {
        VisitorRatingResponse VisitorRatingResponse = modelMapper.map(entity, VisitorRatingResponse.class);
        VisitorRatingResponse.setAgent(UserProtobuf.parseFromJson(entity.getAgent()));
        VisitorRatingResponse.setUser(UserProtobuf.parseFromJson(entity.getUser()));
        return VisitorRatingResponse;
    }

}
