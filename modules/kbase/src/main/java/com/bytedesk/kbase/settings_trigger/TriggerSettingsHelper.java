/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-23
 * @Description: TriggerSettings helper for resolving FAQ associations
 */
package com.bytedesk.kbase.settings_trigger;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.trigger.TriggerEntity;
import com.bytedesk.kbase.trigger.TriggerRequestSimple;
import com.bytedesk.kbase.trigger.TriggerRepository;
import com.bytedesk.kbase.trigger.TriggerKeyConsts;
import com.bytedesk.kbase.trigger.TriggerTypeEnum;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TriggerSettingsHelper {

    private final TriggerRepository triggerRepository;

    private final UidUtils uidUtils;

    /**
     * 仅当 triggers 不为 null 才更新；为 [] 表示清空；为 null 表示保持不变。
     */
    public void updateTriggerAssociationsIfPresent(TriggerSettingsEntity target, TriggerSettingsRequest req) {
        if (req == null || target == null) {
            return;
        }
        if (req.getTriggers() == null) {
            return;
        }
        if (target.getTriggers() == null) {
            target.setTriggers(new ArrayList<>());
        }
        target.getTriggers().clear();

        for (TriggerRequestSimple ref : req.getTriggers()) {
            if (ref == null) {
                continue;
            }

            TriggerEntity triggerEntity = null;
            if (StringUtils.hasText(ref.getUid())) {
                Optional<TriggerEntity> existing = triggerRepository.findByUid(ref.getUid());
                if (existing.isPresent()) {
                    triggerEntity = existing.get();
                }
            }

            if (triggerEntity == null) {
                triggerEntity = new TriggerEntity();
                triggerEntity.setUid(uidUtils.getUid());
                if (StringUtils.hasText(target.getOrgUid())) {
                    triggerEntity.setOrgUid(target.getOrgUid());
                }
                if (StringUtils.hasText(target.getLevel())) {
                    triggerEntity.setLevel(target.getLevel());
                }
            }

            if (ref.getEnabled() != null) {
                triggerEntity.setEnabled(ref.getEnabled());
            }
            if (StringUtils.hasText(ref.getTriggerKey())) {
                triggerEntity.setTriggerKey(ref.getTriggerKey());
            }

            // default type for known triggerKey when caller doesn't provide one
            if (!StringUtils.hasText(ref.getType())
                    && TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE
                            .equals(triggerEntity.getTriggerKey())) {
                triggerEntity.setType(TriggerTypeEnum.THREAD.name());
            }

            if (StringUtils.hasText(ref.getType())) {
                triggerEntity.setType(ref.getType());
            }

            // optional: default display name/description for known triggerKey
            if (!StringUtils.hasText(ref.getName())
                    && TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE
                            .equals(triggerEntity.getTriggerKey())
                    && !StringUtils.hasText(triggerEntity.getName())) {
                triggerEntity.setName("访客长时间未回复提醒");
            }
            if (StringUtils.hasText(ref.getName())) {
                triggerEntity.setName(ref.getName());
            }

            if (!StringUtils.hasText(ref.getDescription())
                    && TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE
                            .equals(triggerEntity.getTriggerKey())
                    && !StringUtils.hasText(triggerEntity.getDescription())) {
                triggerEntity.setDescription("访客长时间未发送消息时，主动发送提醒消息");
            }
            if (StringUtils.hasText(ref.getDescription())) {
                triggerEntity.setDescription(ref.getDescription());
            }
            if (ref.getConfig() != null) {
                triggerEntity.setConfig(ref.getConfig());
            }

            target.getTriggers().add(triggerEntity);
        }
    }

    // public List<FaqEntity> resolveFaqs(List<String> uids) {
    //     if (uids == null) {
    //         return null;
    //     }
    //     if (uids.isEmpty()) {
    //         return new ArrayList<>();
    //     }
    //     List<FaqEntity> result = new ArrayList<>(uids.size());
    //     for (String uid : uids) {
    //         Optional<FaqEntity> faqOpt = faqRepository.findByUid(uid);
    //         faqOpt.ifPresent(result::add);
    //     }
    //     return result;
    // }

    /**
     * 复制 TriggerSettings 属性,排除 id/uid/version 与时间字段,并正确处理懒加载的 proactiveFaqs 集合。
     */
    // public void copyTriggerSettingsProperties(TriggerSettingsEntity source, TriggerSettingsEntity target) {
    //     List<FaqEntity> proactiveFaqs = source.getProactiveFaqs();

    //     BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt", "proactiveFaqs");

    //     if (proactiveFaqs != null) {
    //         if (target.getProactiveFaqs() == null) {
    //             target.setProactiveFaqs(new ArrayList<>());
    //         }
    //         target.getProactiveFaqs().clear();
    //         target.getProactiveFaqs().addAll(proactiveFaqs);
    //     }
    // }
}
