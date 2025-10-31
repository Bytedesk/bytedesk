/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-30 16:50:00
 * @Description: ServiceSettings 辅助工具类，用于处理 FAQ 和其他关联实体的解析
 */
package com.bytedesk.kbase.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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

    /**
     * 复制 ServiceSettings 属性,排除 id/uid/version 与时间字段,并正确处理懒加载的 FAQ 集合。
     * 对于懒加载的 @ManyToMany 集合，BeanUtils.copyProperties 可能复制未初始化的代理对象导致数据丢失。
     * 因此需要在复制前保存集合引用，复制后恢复，确保数据完整性。
     * 
     * @param source 源 ServiceSettings 实体
     * @param target 目标 ServiceSettings 实体
     */
    public void copyServiceSettingsProperties(ServiceSettingsEntity source, ServiceSettingsEntity target) {
        // 在 BeanUtils.copyProperties 之前，保存 source 中的集合引用
        // 调用 getter 会触发懒加载，确保获取实际数据而非代理
        List<FaqEntity> welcomeFaqs = source.getWelcomeFaqs();
        List<FaqEntity> faqs = source.getFaqs();
        List<FaqEntity> quickFaqs = source.getQuickFaqs();
        List<FaqEntity> guessFaqs = source.getGuessFaqs();
        List<FaqEntity> hotFaqs = source.getHotFaqs();
        List<FaqEntity> shortcutFaqs = source.getShortcutFaqs();
        List<FaqEntity> proactiveFaqs = source.getProactiveFaqs();
        
        // 执行属性复制，排除 id/uid/version 等字段和所有集合字段
        BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt",
                "welcomeFaqs", "faqs", "quickFaqs", "guessFaqs", "hotFaqs", "shortcutFaqs", "proactiveFaqs");
        
        // 复制后，将保存的集合引用设置到 target
        // 对于 JPA 管理的 @ManyToMany 集合，使用 clear() + addAll() 来修改集合内容，保持 Hibernate 对集合的管理
        if (welcomeFaqs != null) {
            if (target.getWelcomeFaqs() == null) {
                target.setWelcomeFaqs(new ArrayList<>());
            }
            target.getWelcomeFaqs().clear();
            target.getWelcomeFaqs().addAll(welcomeFaqs);
        }
        if (faqs != null) {
            if (target.getFaqs() == null) {
                target.setFaqs(new ArrayList<>());
            }
            target.getFaqs().clear();
            target.getFaqs().addAll(faqs);
        }
        if (quickFaqs != null) {
            if (target.getQuickFaqs() == null) {
                target.setQuickFaqs(new ArrayList<>());
            }
            target.getQuickFaqs().clear();
            target.getQuickFaqs().addAll(quickFaqs);
        }
        if (guessFaqs != null) {
            if (target.getGuessFaqs() == null) {
                target.setGuessFaqs(new ArrayList<>());
            }
            target.getGuessFaqs().clear();
            target.getGuessFaqs().addAll(guessFaqs);
        }
        if (hotFaqs != null) {
            if (target.getHotFaqs() == null) {
                target.setHotFaqs(new ArrayList<>());
            }
            target.getHotFaqs().clear();
            target.getHotFaqs().addAll(hotFaqs);
        }
        if (shortcutFaqs != null) {
            if (target.getShortcutFaqs() == null) {
                target.setShortcutFaqs(new ArrayList<>());
            }
            target.getShortcutFaqs().clear();
            target.getShortcutFaqs().addAll(shortcutFaqs);
        }
        if (proactiveFaqs != null) {
            if (target.getProactiveFaqs() == null) {
                target.setProactiveFaqs(new ArrayList<>());
            }
            target.getProactiveFaqs().clear();
            target.getProactiveFaqs().addAll(proactiveFaqs);
        }
    }
}
