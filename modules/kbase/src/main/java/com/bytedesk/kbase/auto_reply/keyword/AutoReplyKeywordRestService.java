/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:04:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 21:26:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.keyword;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AutoReplyKeywordRestService extends BaseRestServiceWithExcel<AutoReplyKeywordEntity, AutoReplyKeywordRequest, AutoReplyKeywordResponse, AutoReplyKeywordExcel> {

    private final AutoReplyKeywordRepository autoReplyKeywordRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryRestService;

    private final AuthService authService;

    @Override
    public Page<AutoReplyKeywordEntity> queryByOrgEntity(AutoReplyKeywordRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AutoReplyKeywordEntity> spec = AutoReplyKeywordSpecification.search(request);
        return autoReplyKeywordRepository.findAll(spec, pageable);
    }

    @Override
    public Page<AutoReplyKeywordResponse> queryByOrg(AutoReplyKeywordRequest request) {
        Page<AutoReplyKeywordEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<AutoReplyKeywordResponse> queryByUser(AutoReplyKeywordRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "keyword", key = "#uid", unless = "#result == null")
    @Override
    public Optional<AutoReplyKeywordEntity> findByUid(String uid) {
        return autoReplyKeywordRepository.findByUid(uid);
    }

    public String getKeywordReply(String keyword, String kbUid, String orgUid) {
        AutoReplyKeywordRequest request = AutoReplyKeywordRequest.builder().build();
        request.getKeywordList().add(keyword);
        request.setKbUid(kbUid);
        request.setOrgUid(orgUid);
        //
        Specification<AutoReplyKeywordEntity> spec = AutoReplyKeywordSpecification.search(request);
        List<AutoReplyKeywordEntity> keywordObjects = autoReplyKeywordRepository.findAll(spec);
        if (keywordObjects.isEmpty()) {
            return null;
        }
        // 使用第一条返回结果, TODO: 后续需要优化
        AutoReplyKeywordEntity keywordObject = keywordObjects.get(0);
        //
        // 随机选择一个回复
        Random random = new Random();
        String reply = null;
        if (!keywordObject.getReplyList().isEmpty()) {
            int randomIndex = random.nextInt(keywordObject.getReplyList().size());
            reply = keywordObject.getReplyList().get(randomIndex);
        }
        // 
        return reply;
    }

    @Override
    public AutoReplyKeywordResponse create(AutoReplyKeywordRequest request) {
        // 获取当前用户
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());
        // 
        AutoReplyKeywordEntity keyword = modelMapper.map(request, AutoReplyKeywordEntity.class);
        keyword.setUid(uidUtils.getUid());
        //
        AutoReplyKeywordEntity savedAutoReplyKeyword = save(keyword);
        if (savedAutoReplyKeyword == null) {
            throw new RuntimeException("Failed to create keyword");
        }

        return convertToResponse(savedAutoReplyKeyword);
    }

    @Override
    public AutoReplyKeywordResponse update(AutoReplyKeywordRequest request) {
        Optional<AutoReplyKeywordEntity> keywordOptional = findByUid(request.getUid());
        if (!keywordOptional.isPresent()) {
            throw new RuntimeException("AutoReplyKeyword not found");
        }
        AutoReplyKeywordEntity keyword = keywordOptional.get();
        keyword.setKeywordList(request.getKeywordList());
        keyword.setReplyList(request.getReplyList());
        keyword.setMatchType(request.getMatchType());
        keyword.setContentType(request.getContentType());
        keyword.setEnabled(request.getEnabled());
        //
        AutoReplyKeywordEntity savedAutoReplyKeyword = save(keyword);
        if (savedAutoReplyKeyword == null) {
            throw new RuntimeException("Failed to create keyword");
        }
        //
        return convertToResponse(savedAutoReplyKeyword);
    }

    @Override
    public AutoReplyKeywordEntity save(AutoReplyKeywordEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected AutoReplyKeywordEntity doSave(AutoReplyKeywordEntity entity) {
        return autoReplyKeywordRepository.save(entity);
    }

    @Override
    public AutoReplyKeywordEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AutoReplyKeywordEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<AutoReplyKeywordEntity> latest = autoReplyKeywordRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AutoReplyKeywordEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return autoReplyKeywordRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<AutoReplyKeywordEntity> keywords) {
        autoReplyKeywordRepository.saveAll(keywords);
    }

    // 启用/禁用关键词自动回复
    public AutoReplyKeywordResponse enable(AutoReplyKeywordRequest request) {
        Optional<AutoReplyKeywordEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            AutoReplyKeywordEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            AutoReplyKeywordEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update AutoReplyKeyword");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("auto_reply_keyword not found");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<AutoReplyKeywordEntity> keywordOptional = findByUid(uid);
        if (keywordOptional.isPresent()) {
            keywordOptional.get().setDeleted(true);
            save(keywordOptional.get());
        }
    }

    @Override
    public void delete(AutoReplyKeywordRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public AutoReplyKeywordResponse convertToResponse(AutoReplyKeywordEntity entity) {
        AutoReplyKeywordResponse keywordResponse = modelMapper.map(entity, AutoReplyKeywordResponse.class);
        // 没有自动转换？手动转换
        keywordResponse.setTransfer(entity.getTransfer());
        return keywordResponse;
    }

    @Override
    public AutoReplyKeywordExcel convertToExcel(AutoReplyKeywordEntity entity) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(entity.getCategoryUid());
        AutoReplyKeywordExcel keywordExcel = modelMapper.map(entity, AutoReplyKeywordExcel.class);
        keywordExcel.setKeywordList(String.join("|", entity.getKeywordList()));
        keywordExcel.setReplyList(String.join("|", entity.getReplyList()));
        if (categoryOptional.isPresent()) {
            keywordExcel.setCategory(categoryOptional.get().getName());
        }
        return keywordExcel;
    }

    public AutoReplyKeywordEntity convertExcelToAutoReplyKeyword(AutoReplyKeywordExcel excel, String kbUid, String orgUid) {
        List<String> keywordList = Arrays.asList(excel.getKeywordList().split("\\|")); // 使用正则表达式匹配 "|"
        List<String> replyList = Arrays.asList(excel.getReplyList().split("\\|")); // 使用正则表达式匹配 "|"
        log.info("keyword {} keywordList: {}", excel.getKeywordList(), keywordList);
        log.info("reply {} replyList: {}", excel.getReplyList(), replyList);
        // 
        AutoReplyKeywordEntity keyword = AutoReplyKeywordEntity.builder().build();
        keyword.setUid(uidUtils.getCacheSerialUid());
        keyword.setKeywordList(keywordList);
        keyword.setReplyList(replyList);
        // 
        keyword.setMatchType(AutoReplyKeywordMatchEnum.FUZZY.name()); // TODO: 默认匹配类型
        keyword.setTransfer(false);
        // 
        // keyword.setCategoryUid(categoryUid);
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            keyword.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.AUTOREPLY.name());
            categoryRequest.setOrgUid(orgUid);
            //
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
            keyword.setCategoryUid(categoryResponse.getUid());
        }
        // 
        keyword.setKbUid(kbUid);
        keyword.setOrgUid(orgUid);
        // 
        return keyword;
    }

    public void initData(String orgUid) {
        // 检查是否已经有数据，如果有则不初始化
        if (autoReplyKeywordRepository.count() > 0) {
            log.info("AutoReplyKeyword data already exists, skipping initialization.");
            return;
        }
        log.info("AutoReplyData initialization started...");

        String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_AUTOREPLY_UID);
        
        // 创建默认分类
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name(AutoReplyKeywordInitData.DEFAULT_CATEGORY_NAME)
                .type(CategoryTypeEnum.AUTOREPLY.name())
                .kbUid(kbUid)
                .orgUid(orgUid)
                .build();
        CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
        String categoryUid = categoryResponse.getUid();

        // 从AutoReplyKeywordInitData获取默认自动回复关键词数据
        List<AutoReplyKeywordInitData.AutoReplyKeywordData> defaultDataList = AutoReplyKeywordInitData.getDefaultAutoReplyKeywordData();
        
        // 创建关键词自动回复数据
        List<AutoReplyKeywordEntity> keywordList = new ArrayList<>();
        
        for (AutoReplyKeywordInitData.AutoReplyKeywordData data : defaultDataList) {
            AutoReplyKeywordEntity keyword = AutoReplyKeywordEntity.builder().build();
            keyword.setUid(uidUtils.getUid());
            keyword.setKeywordList(data.getKeywordList());
            keyword.setReplyList(data.getReplyList());
            keyword.setMatchType(data.getMatchType());
            keyword.setEnabled(data.isEnabled());
            keyword.setCategoryUid(categoryUid);
            keyword.setKbUid(kbUid);
            keyword.setOrgUid(orgUid);
            keywordList.add(keyword);
        }

        // 保存所有数据
        save(keywordList);
    }

}
