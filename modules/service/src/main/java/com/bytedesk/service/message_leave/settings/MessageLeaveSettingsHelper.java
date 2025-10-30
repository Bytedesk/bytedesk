/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-30 16:52:00
 * @Description: MessageLeaveSettings 辅助工具类，用于处理 Worktime 关联实体的解析
 */
package com.bytedesk.service.message_leave.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
