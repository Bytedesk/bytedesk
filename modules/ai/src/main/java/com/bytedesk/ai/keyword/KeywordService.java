/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:04:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 16:28:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.keyword;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.robot.Robot;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.Category;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KeywordService extends BaseService<Keyword, KeywordRequest, KeywordResponse> {

    private final KeywordRepository keywordRepository;

    private final CategoryService categoryService;

    private final RobotService robotService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public List<KeywordResponse> ask(String keyword, String robotUid, String orgUid) {
        KeywordRequest request = KeywordRequest.builder().build();
        request.setKeyword(keyword);
        request.setRobotUid(robotUid);
        request.setOrgUid(orgUid);
        // 
        Specification<Keyword> spec = KeywordSpecification.search(request);
        List<Keyword> keywords = keywordRepository.findAll(spec);
        return keywords.stream().map(this::convertToResponse).toList();
    }

    @Override
    public Page<KeywordResponse> queryByOrg(KeywordRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<Keyword> spec = KeywordSpecification.search(request);
        
        Page<Keyword> page = keywordRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<KeywordResponse> queryByUser(KeywordRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "keyword", key = "#uid")
    @Override
    public Optional<Keyword> findByUid(String uid) {
        return keywordRepository.findByUid(uid);
    }

    @Override
    public KeywordResponse create(KeywordRequest request) {
        
        Keyword keyword = modelMapper.map(request, Keyword.class);
        keyword.setUid(uidUtils.getCacheSerialUid());

        Optional<Category> category = categoryService.findByUid(request.getCategoryUid());
        if (category.isPresent()) {
            keyword.setCategory(category.get());
        }

        Optional<Robot> robot = robotService.findByUid(request.getRobotUid());
        if (robot.isPresent()) {
            keyword.setRobot(robot.get());
        }

        Keyword savedKeyword = save(keyword);
        if (savedKeyword == null) {
            throw new RuntimeException("Failed to create keyword");
        }

        return convertToResponse(savedKeyword);
    }

    @Override
    public KeywordResponse update(KeywordRequest request) {
        Optional<Keyword> keywordOptional = findByUid(request.getUid());
        if (!keywordOptional.isPresent()) {
            throw new RuntimeException("Keyword not found");
        }
        Keyword keyword = keywordOptional.get();
        keyword.setKeyword(request.getKeyword());
        keyword.setReply(request.getReply());
        keyword.setMatchType(request.getMatchType());
        keyword.setContentType(request.getContentType());
        keyword.setEnabled(request.getEnabled());
        // 
        Optional<Category> category = categoryService.findByUid(request.getCategoryUid());
        if (category.isPresent()) {
            keyword.setCategory(category.get());
        }
        Optional<Robot> robot = robotService.findByUid(request.getRobotUid());
        if (robot.isPresent()) {
            keyword.setRobot(robot.get());
        }
        Keyword savedKeyword = save(keyword);
        if (savedKeyword == null) {
            throw new RuntimeException("Failed to create keyword");
        }
        // 
        return convertToResponse(savedKeyword);
    }

    @Override
    public Keyword save(Keyword entity) {
        try {
            return keywordRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Keyword> keywordOptional = findByUid(uid);
        if (!keywordOptional.isPresent()) {
            throw new RuntimeException("Keyword not found");
        }
        keywordOptional.get().setDeleted(true);
        save(keywordOptional.get());
    }

    @Override
    public void delete(Keyword entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Keyword entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public KeywordResponse convertToResponse(Keyword entity) {
        return modelMapper.map(entity, KeywordResponse.class);
    }
    
}
