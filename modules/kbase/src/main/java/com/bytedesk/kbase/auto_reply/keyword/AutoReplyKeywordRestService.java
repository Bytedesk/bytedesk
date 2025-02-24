/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:04:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 16:56:15
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

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AutoReplyKeywordRestService extends BaseRestService<AutoReplyKeywordEntity, AutoReplyKeywordRequest, AutoReplyKeywordResponse> {

    private final AutoReplyKeywordRepository keywordRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryService;

    @Override
    public Page<AutoReplyKeywordResponse> queryByOrg(AutoReplyKeywordRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<AutoReplyKeywordEntity> spec = AutoReplyKeywordSpecification.search(request);
        Page<AutoReplyKeywordEntity> page = keywordRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<AutoReplyKeywordResponse> queryByUser(AutoReplyKeywordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "keyword", key = "#uid", unless = "#result == null")
    @Override
    public Optional<AutoReplyKeywordEntity> findByUid(String uid) {
        return keywordRepository.findByUid(uid);
    }

    public String getKeywordReply(String keyword, String kbUid, String orgUid) {
        AutoReplyKeywordRequest request = AutoReplyKeywordRequest.builder().build();
        request.getKeywordList().add(keyword);
        request.setKbUid(kbUid);
        request.setOrgUid(orgUid);
        //
        Specification<AutoReplyKeywordEntity> spec = AutoReplyKeywordSpecification.search(request);
        List<AutoReplyKeywordEntity> keywordObjects = keywordRepository.findAll(spec);
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

        AutoReplyKeywordEntity keyword = modelMapper.map(request, AutoReplyKeywordEntity.class);
        keyword.setUid(uidUtils.getCacheSerialUid());
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
        keyword.setMatchType(request.getMatchType().name());
        keyword.setContentType(request.getContentType().name());
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
            return keywordRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    public void save(List<AutoReplyKeywordEntity> keywords) {
        keywordRepository.saveAll(keywords);
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AutoReplyKeywordEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public AutoReplyKeywordResponse convertToResponse(AutoReplyKeywordEntity entity) {
        AutoReplyKeywordResponse keywordResponse = modelMapper.map(entity, AutoReplyKeywordResponse.class);
        // 没有自动转换？手动转换
        keywordResponse.setIsTransfer(entity.isTransfer());
        return keywordResponse;
    }

    public AutoReplyKeywordExcel convertToExcel(AutoReplyKeywordResponse entity) {
        AutoReplyKeywordExcel keywordExcel = modelMapper.map(entity, AutoReplyKeywordExcel.class);
        keywordExcel.setKeyword(String.join("|", entity.getKeywordList()));
        keywordExcel.setReply(String.join("|", entity.getReplyList()));
        return keywordExcel;
    }

    public AutoReplyKeywordEntity convertExcelToAutoReplyKeyword(AutoReplyKeywordExcel excel, String kbUid, String orgUid) {
        List<String> keywordList = Arrays.asList(excel.getKeyword().split("\\|")); // 使用正则表达式匹配 "|"
        List<String> replyList = Arrays.asList(excel.getReply().split("\\|")); // 使用正则表达式匹配 "|"
        log.info("keyword {} keywordList: {}", excel.getKeyword(), keywordList);
        log.info("reply {} replyList: {}", excel.getReply(), replyList);
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
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            keyword.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.KEYWORD.name());
            categoryRequest.setOrgUid(orgUid);
            //
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            keyword.setCategoryUid(categoryResponse.getUid());
        }
        // 
        keyword.setKbUid(kbUid);
        keyword.setOrgUid(orgUid);
        // 
        return keyword;
    }

}
