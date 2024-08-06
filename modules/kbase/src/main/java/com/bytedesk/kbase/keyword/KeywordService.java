/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:04:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-01 19:36:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.keyword;

import java.util.Arrays;
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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class KeywordService extends BaseService<Keyword, KeywordRequest, KeywordResponse> {

    private final KeywordRepository keywordRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

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
        //
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
        keyword.setKeywordList(request.getKeywordList());
        keyword.setReplyList(request.getReplyList());
        keyword.setMatchType(request.getMatchType());
        keyword.setContentType(request.getContentType());
        keyword.setEnabled(request.getEnabled());
        //
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

    public void save(List<Keyword> keywords) {
        keywordRepository.saveAll(keywords);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Keyword> keywordOptional = findByUid(uid);
        if (keywordOptional.isPresent()) {
            keywordOptional.get().setDeleted(true);
            save(keywordOptional.get());
        }
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
        KeywordResponse keywordResponse = modelMapper.map(entity, KeywordResponse.class);
        // 没有自动转换？手动转换
        keywordResponse.setIsTransfer(entity.isTransfer());
        return keywordResponse;
    }

    public KeywordExcel convertToExcel(KeywordResponse entity) {
        KeywordExcel keywordExcel = modelMapper.map(entity, KeywordExcel.class);
        keywordExcel.setKeyword(String.join("|", entity.getKeywordList()));
        keywordExcel.setReply(String.join("|", entity.getReplyList()));
        return keywordExcel;
    }

    public Keyword convertExcelToKeyword(KeywordExcel entity, String categoryUid, String kbUid, String orgUid) {
        List<String> keywordList = Arrays.asList(entity.getKeyword().split("\\|")); // 使用正则表达式匹配 "|"
        List<String> replyList = Arrays.asList(entity.getReply().split("\\|")); // 使用正则表达式匹配 "|"
        log.info("keyword {} keywordList: {}", entity.getKeyword(), keywordList);
        log.info("reply {} replyList: {}", entity.getReply(), replyList);
        // 
        Keyword keyword = Keyword.builder().build();
        keyword.setUid(uidUtils.getCacheSerialUid());
        keyword.setKeywordList(keywordList);
        keyword.setReplyList(replyList);
        // 
        keyword.setMatchType(KeywordMatchEnum.FUZZY); // TODO: 默认匹配类型
        keyword.setTransfer(false);
        // 
        keyword.setCategoryUid(categoryUid);
        keyword.setKbUid(kbUid);
        keyword.setOrgUid(orgUid);
        // 
        return keyword;
    }

}
