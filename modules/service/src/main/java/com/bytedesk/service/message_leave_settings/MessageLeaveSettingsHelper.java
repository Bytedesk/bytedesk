/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-30 16:52:00
 * @Description: MessageLeaveSettings 辅助工具类，用于处理 Worktime 关联实体的解析
 */
package com.bytedesk.service.message_leave_settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.bytedesk.service.worktime.WorktimeEntity;
import com.bytedesk.service.worktime.WorktimeRepository;

import lombok.AllArgsConstructor;

/**
 * MessageLeaveSettings 辅助工具类
 * 提供统一的 Worktime 关联解析方法，避免在多个 Service 中重复代码
 */
@Component
@AllArgsConstructor
public class MessageLeaveSettingsHelper {

    private final WorktimeRepository worktimeRepository;

    /**
     * 根据请求中的 worktime UID 列表，解析并设置到目标 MessageLeaveSettings 实体。
     * 仅当 UID 列表不为 null 才会更新；为空列表表示清空；为 null 时保持不变。
     * 
     * @param target 目标 MessageLeaveSettings 实体（可以是草稿或发布版本）
     * @param req MessageLeaveSettingsRequest 请求对象
     */
    public void updateWorktimesIfPresent(MessageLeaveSettingsEntity target, MessageLeaveSettingsRequest req) {
        if (req.getWorktimeUids() == null) {
            return;
        }
        target.setWorktimes(resolveWorktimes(req.getWorktimeUids()));
    }

    /**
     * 将 uid 列表解析为 WorktimeEntity 列表。忽略无效 uid。
     * 
     * @param uids Worktime UID 列表
     * @return 解析后的 WorktimeEntity 列表，null 输入返回 null，空列表返回空列表
     */
    public List<WorktimeEntity> resolveWorktimes(List<String> uids) {
        if (uids == null) {
            return null;
        }
        if (uids.isEmpty()) {
            return new ArrayList<>();
        }
        List<WorktimeEntity> result = new ArrayList<>(uids.size());
        for (String uid : uids) {
            Optional<WorktimeEntity> worktimeOpt = worktimeRepository.findByUid(uid);
            if (worktimeOpt.isPresent()) {
                result.add(worktimeOpt.get());
            }
        }
        return result;
    }

    /**
     * 复制 MessageLeaveSettings 属性,排除 id/uid/version 与时间字段,并正确处理懒加载的 Worktime 集合。
     * 对于懒加载的 @ManyToMany 集合，BeanUtils.copyProperties 可能复制未初始化的代理对象导致数据丢失。
     * 因此需要在复制前保存集合引用，复制后恢复，确保数据完整性。
     * 
     * @param source 源 MessageLeaveSettings 实体
     * @param target 目标 MessageLeaveSettings 实体
     */
    public void copyMessageLeaveSettingsProperties(MessageLeaveSettingsEntity source, MessageLeaveSettingsEntity target) {
        // 保存集合引用
        List<WorktimeEntity> worktimes = source.getWorktimes();
        
        // 执行属性复制，排除 id/uid/version 等字段和集合字段
        BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt", "worktimes");
        
        // 恢复集合引用
        if (worktimes != null) {
            if (target.getWorktimes() == null) {
                target.setWorktimes(new ArrayList<>());
            }
            target.getWorktimes().clear();
            target.getWorktimes().addAll(worktimes);
        }
    }

    /**
     * 通用属性复制方法,仅复制业务字段,忽略 id/uid/version 与时间字段。
     * 适用于不包含懒加载集合的简单实体。
     * 
     * @param source 源对象
     * @param target 目标对象
     */
    public void copyPropertiesExcludingIds(Object source, Object target) {
        BeanUtils.copyProperties(source, target, "id", "uid", "version", "createdAt", "updatedAt");
    }
}
