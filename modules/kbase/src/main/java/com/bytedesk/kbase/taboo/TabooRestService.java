/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:25:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TabooRestService extends BaseRestServiceWithExport<TabooEntity, TabooRequest, TabooResponse, TabooExcel> {

    private final TabooRepository tabooRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryRestService;

    private final MessageRestService messageRestService;

    private final ThreadRestService threadRestService;

    @Override
    protected Specification<TabooEntity> createSpecification(TabooRequest request) {
        return TabooSpecification.search(request, authService);
    }

    @Override
    protected Page<TabooEntity> executePageQuery(Specification<TabooEntity> spec, Pageable pageable) {
        return tabooRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "taboo", key="#uid", unless = "#result == null")
    @Override
    public Optional<TabooEntity> findByUid(String uid) {
        return tabooRepository.findByUid(uid);
    }

    // existsByContent
    public Boolean existsByContent(String content, String orgUid) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        // 去掉空格换行
        content = content.replaceAll("\\s+", "");

        // 获取指定组织的所有敏感词
        List<TabooEntity> tabooList = tabooRepository.findByOrgUidAndDeletedFalse(orgUid);
        
        // 检查内容是否包含任何敏感词
        for (TabooEntity taboo : tabooList) {
            if (content.contains(taboo.getContent())) {
                return true;
            }
        }
        
        // 是否被包含在敏感词列表中
        return tabooRepository.existsByContentContainsAndOrgUidAndDeletedFalse(content, orgUid);
    }

    /**
     * 供敏感词替换逻辑使用：返回指定组织启用的敏感词（包含同义词）。
     */
    public List<String> listEnabledWordsWithSynonyms(String orgUid) {
        if (orgUid == null || orgUid.isEmpty()) {
            return List.of();
        }

        List<TabooEntity> tabooList = tabooRepository.findByOrgUidAndDeletedFalse(orgUid);
        if (tabooList == null || tabooList.isEmpty()) {
            return List.of();
        }

        List<String> words = new ArrayList<>();
        for (TabooEntity taboo : tabooList) {
            if (taboo == null) {
                continue;
            }
            if (!Boolean.TRUE.equals(taboo.getEnabled())) {
                continue;
            }

            if (taboo.getContent() != null && !taboo.getContent().isBlank()) {
                words.add(taboo.getContent().trim());
            }
            if (taboo.getSynonymList() != null && !taboo.getSynonymList().isEmpty()) {
                for (String synonym : taboo.getSynonymList()) {
                    if (synonym != null && !synonym.isBlank()) {
                        words.add(synonym.trim());
                    }
                }
            }
        }

        return words;
    }

    /**
     * 根据原始消息内容匹配命中的敏感词，并返回对应自动回复。
     * 若命中词未配置 reply，则回退到系统默认文案。
     */
    public Optional<String> resolveReplyForContent(String orgUid, String content) {
        if (orgUid == null || orgUid.isBlank() || content == null || content.isBlank()) {
            return Optional.empty();
        }

        List<TabooEntity> tabooList = tabooRepository.findByOrgUidAndDeletedFalse(orgUid);
        if (tabooList == null || tabooList.isEmpty()) {
            return Optional.empty();
        }

        String normalizedContent = content.replaceAll("\\s+", "");
        ZonedDateTime now = ZonedDateTime.now();
        for (TabooEntity taboo : tabooList) {
            if (taboo == null || !Boolean.TRUE.equals(taboo.getEnabled())) {
                continue;
            }
            if (taboo.getStartDate() != null && now.isBefore(taboo.getStartDate())) {
                continue;
            }
            if (taboo.getEndDate() != null && now.isAfter(taboo.getEndDate())) {
                continue;
            }

            if (containsTabooWord(normalizedContent, taboo)) {
                String reply = taboo.getReply();
                if (reply == null || reply.isBlank()) {
                    reply = I18Consts.I18N_CANT_ANSWER;
                }
                return Optional.of(reply);
            }
        }

        return Optional.empty();
    }

    /**
     * 访客端机器人消息敏感词预检：命中时直接持久化访客问题(过滤后)与机器人回复，并返回给前端直接展示。
     */
    public VisitorTabooCheckResult checkVisitorTabooBeforeSse(String messageJson, String fallbackOrgUid) {
        // 1) 基础参数校验
        if (messageJson == null || messageJson.isBlank()) {
            throw new IllegalArgumentException("messageJson required");
        }

        // 2) 解析前端上传的消息 JSON
        MessageProtobuf messageProtobuf;
        try {
            messageProtobuf = MessageProtobuf.fromJson(messageJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("messageJson invalid");
        }

        // 3) 无内容时直接返回未命中
        if (messageProtobuf == null || messageProtobuf.getContent() == null || messageProtobuf.getContent().isBlank()) {
            return new VisitorTabooCheckResult(false, null, null, null, null);
        }

        // 4) 解析组织标识：优先消息 extra，其次接口参数
        MessageExtra extra = MessageExtra.fromJson(messageProtobuf.getExtra());
        String orgUid = extra.getOrgUid();
        if (orgUid == null || orgUid.isBlank()) {
            orgUid = fallbackOrgUid;
        }
        if (orgUid == null || orgUid.isBlank()) {
            return new VisitorTabooCheckResult(false, null, null, null, null);
        }

        // 5) 提取文本内容用于敏感词匹配
        String plainTextContent = TabooUtils.extractPlainTextContent(messageProtobuf.getContent());
        if (plainTextContent == null || plainTextContent.isBlank()) {
            return new VisitorTabooCheckResult(false, null, null, null, null);
        }

        // 6) 查找命中的敏感词回复；未命中直接返回
        Optional<String> replyOptional = resolveReplyForContent(orgUid, plainTextContent);
        if (!replyOptional.isPresent()) {
            return new VisitorTabooCheckResult(false, null, null, null, null);
        }

        // 7) 生成脱敏后的访客文本
        String filteredContent = TabooUtils.maskTabooContent(
                plainTextContent,
                listEnabledWordsWithSynonyms(orgUid));

        // 8) 尝试挂载到会话并持久化两条消息：访客问题 + 机器人回复
        MessageResponse questionMessage = null;
        MessageResponse answerMessage = null;
        String threadUid = messageProtobuf.getThread() != null ? messageProtobuf.getThread().getUid() : null;
        if (threadUid != null && !threadUid.isBlank()) {
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadUid);
            if (threadOptional.isPresent()) {
                ThreadEntity threadEntity = threadOptional.get();

                // 保存脱敏后的访客问题消息
                MessageEntity savedQuestion = messageRestService.save(TabooUtils.buildTabooVisitorMessage(
                        messageProtobuf,
                        threadEntity,
                        orgUid,
                        filteredContent));
                if (savedQuestion != null) {
                    questionMessage = ConvertUtils.convertToMessageResponse(savedQuestion);
                }

                // 保存机器人回复消息
                MessageEntity savedAnswer = messageRestService.save(TabooUtils.buildTabooReplyMessage(
                        threadEntity,
                        orgUid,
                        replyOptional.get()));
                if (savedAnswer != null) {
                    answerMessage = ConvertUtils.convertToMessageResponse(savedAnswer);
                }
            }
        }

        // 9) 返回最新数据格式，前端可直接渲染 questionMessage/answerMessage
        return new VisitorTabooCheckResult(
                true,
                filteredContent,
                replyOptional.get(),
                questionMessage,
                answerMessage);
    }

    public record VisitorTabooCheckResult(
            boolean hit,
            String filteredContent,
            String reply,
            MessageResponse questionMessage,
            MessageResponse answerMessage) {
    }

    private boolean containsTabooWord(String normalizedContent, TabooEntity taboo) {
        String contentWord = taboo.getContent();
        if (contentWord != null && !contentWord.isBlank()) {
            String normalizedWord = contentWord.replaceAll("\\s+", "");
            if (!normalizedWord.isBlank() && normalizedContent.contains(normalizedWord)) {
                return true;
            }
        }

        if (taboo.getSynonymList() == null || taboo.getSynonymList().isEmpty()) {
            return false;
        }

        for (String synonym : taboo.getSynonymList()) {
            if (synonym == null || synonym.isBlank()) {
                continue;
            }
            String normalizedSynonym = synonym.replaceAll("\\s+", "");
            if (!normalizedSynonym.isBlank() && normalizedContent.contains(normalizedSynonym)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public TabooResponse create(TabooRequest request) {
        // 
        TabooEntity taboo = modelMapper.map(request, TabooEntity.class);
        taboo.setUid(uidUtils.getUid());
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
        if (categoryOptional.isPresent()) {
            taboo.setCategoryUid(categoryOptional.get().getUid());
        }
        // 
        TabooEntity savedTaboo = save(taboo);
        if (savedTaboo == null) {
            throw new RuntimeException("create taboo failed");
        }
        return convertToResponse(savedTaboo);
    }

    @Override
    public TabooResponse update(TabooRequest request) {
        
        Optional<TabooEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TabooEntity taboo = optional.get();
            taboo.setContent(request.getContent());
            taboo.setReply(request.getReply());
            taboo.setSynonymList(request.getSynonymList());
            taboo.setTagList(request.getTagList());
            // categoryUid
            Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(request.getCategoryUid());
            if (categoryOptional.isPresent()) {
                taboo.setCategoryUid(categoryOptional.get().getUid());
            }
            // 
            TabooEntity savedTaboo = save(taboo);
            if (savedTaboo == null) {
                throw new RuntimeException("create taboo failed");
            }
            return convertToResponse(savedTaboo);
        } else {
            throw new RuntimeException("update taboo failed");
        }
    }

    @CachePut(value = "taboo", key = "#entity.uid")
    protected TabooEntity doSave(TabooEntity entity) {
        return tabooRepository.save(entity);
    }

    @Override
    public TabooEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TabooEntity entity) {
        try {
            Optional<TabooEntity> latest = tabooRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TabooEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return tabooRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void save(List<TabooEntity> entities) {
        tabooRepository.saveAll(entities);
    }

    // 启用/禁用敏感词
    public TabooResponse enable(TabooRequest request) {
        Optional<TabooEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TabooEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            TabooEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Failed to update Taboo");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("taboo not found");
        }
    }

    @CacheEvict(value = "taboo", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<TabooEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            TabooEntity taboo = optional.get();
            taboo.setDeleted(true);
            save(taboo);
        }
    }

    @Override
    public void delete(TabooRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public TabooResponse convertToResponse(TabooEntity entity) {
        return modelMapper.map(entity, TabooResponse.class);
    }

    @Override
    public TabooExcel convertToExcel(TabooEntity response) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(response.getCategoryUid());
        TabooExcel tabooExcel = modelMapper.map(response, TabooExcel.class);
        tabooExcel.setCategory(categoryOptional.get().getName());
        return tabooExcel;
    }

    public TabooEntity convertExcelToTaboo(TabooExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Taboo.class); // String categoryUid,
        TabooEntity taboo = TabooEntity.builder().build();
        taboo.setUid(uidUtils.getUid());
        taboo.setContent(excel.getContent());
        // 
        // taboo.setCategoryUid(categoryUid);
         Optional<CategoryEntity> categoryOptional = categoryRestService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            taboo.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.TABOO.name());
            categoryRequest.setOrgUid(orgUid);
            // 
            CategoryResponse categoryResponse = categoryRestService.create(categoryRequest);
            taboo.setCategoryUid(categoryResponse.getUid());
        }
        // 
        taboo.setKbUid(kbUid);
        taboo.setOrgUid(orgUid);

        return taboo;
    }
    
}
