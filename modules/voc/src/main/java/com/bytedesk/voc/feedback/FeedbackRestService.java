/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 16:33:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import java.util.ArrayList;
import java.util.List;
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
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
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

    private final CategoryRestService categoryRestService;

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
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 处理分类名称，确保分类存在，如果不存在则创建
        if (request.getCategoryNames() != null && !request.getCategoryNames().isEmpty()) {
            List<String> categoryUids = processCategoryNames(request.getCategoryNames(), user != null ? user.getOrgUid() : null);
            request.setCategoryUids(categoryUids);
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
        log.info("initFeedbackCategories");
        
        // 初始化反馈分类
        String[] feedbackCategories = {
            "错别字、拼写错误",
            "链接跳转有问题", 
            "文档和实操过程不一致",
            "文档难以理解",
            "建议或其他"
        };
        
        String level = LevelEnum.ORGANIZATION.name();
        
        for (String categoryName : feedbackCategories) {
            // 检查分类是否已存在
            Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(
                    categoryName, CategoryTypeEnum.FEEDBACK.name(), orgUid, level, BytedeskConsts.PLATFORM_BYTEDESK);
            
            if (!categoryOptional.isPresent()) {
                // 创建新的反馈分类
                CategoryRequest categoryRequest = CategoryRequest.builder()
                        .name(categoryName)
                        .order(0)
                        .level(level)
                        .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                        .type(CategoryTypeEnum.FEEDBACK.name())
                        .orgUid(orgUid)
                        .build();
                
                CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
                if (categoryResponse != null) {
                    log.info("Created feedback category: {}", categoryName);
                } else {
                    log.error("Failed to create feedback category: {}", categoryName);
                }
            } else {
                log.debug("Feedback category already exists: {}", categoryName);
            }
        }
    }

    /**
     * 处理分类名称列表，确保分类存在，如果不存在则创建
     * @param categoryNames 分类名称列表
     * @param orgUid 组织UID
     * @return 分类UID列表
     */
    private List<String> processCategoryNames(List<String> categoryNames, String orgUid) {
        List<String> categoryUids = new ArrayList<>();
        
        if (categoryNames == null || categoryNames.isEmpty()) {
            return categoryUids;
        }
        
        String level = LevelEnum.ORGANIZATION.name();
        String platform = BytedeskConsts.PLATFORM_BYTEDESK;
        String type = CategoryTypeEnum.FEEDBACK.name();
        
        for (String categoryName : categoryNames) {
            if (!StringUtils.hasText(categoryName)) {
                continue;
            }
            
            // 检查分类是否已存在
            Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndTypeAndOrgUidAndLevelAndPlatformAndDeleted(
                    categoryName, type, orgUid, level, platform);
            
            CategoryEntity category;
            if (categoryOptional.isPresent()) {
                // 分类已存在，直接使用
                category = categoryOptional.get();
                log.debug("Found existing feedback category: {}", categoryName);
            } else {
                // 分类不存在，创建新分类
                CategoryRequest categoryRequest = CategoryRequest.builder()
                        .name(categoryName)
                        .order(0)
                        .level(level)
                        .platform(platform)
                        .type(type)
                        .orgUid(orgUid)
                        .build();
                
                try {
                    CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
                    if (categoryResponse != null) {
                        // 重新查询获取完整的CategoryEntity
                        Optional<CategoryEntity> newCategoryOptional = categoryRestService.findByUid(categoryResponse.getUid());
                        if (newCategoryOptional.isPresent()) {
                            category = newCategoryOptional.get();
                            log.info("Created new feedback category: {}", categoryName);
                        } else {
                            log.error("Failed to retrieve newly created feedback category: {}", categoryName);
                            continue;
                        }
                    } else {
                        log.error("Failed to create feedback category: {}", categoryName);
                        continue;
                    }
                } catch (Exception e) {
                    log.error("Exception while creating feedback category: {}", categoryName, e);
                    continue;
                }
            }
            
            categoryUids.add(category.getUid());
        }
        
        return categoryUids;
    }
    
    
}
