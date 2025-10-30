/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-30 16:50:00
 * @Description: ServiceSettings 辅助工具类，用于处理 FAQ 和其他关联实体的解析
 */
package com.bytedesk.kbase.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRepository;

import lombok.AllArgsConstructor;

/**
 * ServiceSettings 辅助工具类
 * 提供统一的 FAQ 关联解析方法，避免在多个 Service 中重复代码
 */
@Component
@AllArgsConstructor
public class ServiceSettingsHelper {

    private final FaqRepository faqRepository;

    /**
     * 根据请求中的 FAQ UID 列表，解析并设置到目标 ServiceSettings 实体。
     * 仅当对应 UID 列表不为 null 才会更新；为 [] 时表示清空；为 null 时保持不变。
     * 
     * @param target 目标 ServiceSettings 实体（可以是草稿或发布版本）
     * @param req ServiceSettingsRequest 请求对象
     */
    public void updateFaqAssociationsIfPresent(ServiceSettingsEntity target, ServiceSettingsRequest req) {
        if (req.getWelcomeFaqUids() != null) {
            target.setWelcomeFaqs(resolveFaqs(req.getWelcomeFaqUids()));
        }
        if (req.getFaqUids() != null) {
            target.setFaqs(resolveFaqs(req.getFaqUids()));
        }
        if (req.getQuickFaqUids() != null) {
            target.setQuickFaqs(resolveFaqs(req.getQuickFaqUids()));
        }
        if (req.getGuessFaqUids() != null) {
            target.setGuessFaqs(resolveFaqs(req.getGuessFaqUids()));
        }
        if (req.getHotFaqUids() != null) {
            target.setHotFaqs(resolveFaqs(req.getHotFaqUids()));
        }
        if (req.getShortcutFaqUids() != null) {
            target.setShortcutFaqs(resolveFaqs(req.getShortcutFaqUids()));
        }
        if (req.getProactiveFaqUids() != null) {
            target.setProactiveFaqs(resolveFaqs(req.getProactiveFaqUids()));
        }
    }

    /**
     * 将 uid 列表解析为 FaqEntity 列表。忽略无效 uid。
     * 
     * @param uids FAQ UID 列表
     * @return 解析后的 FaqEntity 列表，null 输入返回 null，空列表返回空列表
     */
    public List<FaqEntity> resolveFaqs(List<String> uids) {
        if (uids == null) {
            return null;
        }
        if (uids.isEmpty()) {
            return new ArrayList<>();
        }
        List<FaqEntity> result = new ArrayList<>(uids.size());
        for (String uid : uids) {
            Optional<FaqEntity> faqOpt = faqRepository.findByUid(uid);
            if (faqOpt.isPresent()) {
                result.add(faqOpt.get());
            }
        }
        return result;
    }
}
