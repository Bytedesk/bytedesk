/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 10:29:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.member.MemberEntity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ GroupEntityListener.class })
@Table(name = "bytedesk_team_groups")
public class GroupEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String name = I18Consts.I18N_GROUP_NAME;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultGroupAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_GROUP_DESCRIPTION;

    // 是否显示顶部通知提示
    @Builder.Default
    private Boolean showTopTip = false;

    // 通知栏提示
    @Builder.Default
    private String topTip = BytedeskConsts.EMPTY_STRING;

    // 是否外部群
    @Builder.Default
    @Column(name = "is_external")
    private Boolean external = false;

    @Builder.Default
    @Column(name = "group_type")
    private String type = GroupTypeEnum.NORMAL.name();

    @Builder.Default
    private String status = GroupStatusEnum.NORMAL.name();

    // 群成员关系
    
    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<MemberEntity> members = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<UserEntity> admins = new ArrayList<>();

    // @Builder.Default
    // @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    // private List<RobotEntity> robots = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity creator;

    // 群组设置
    @Builder.Default
    private Integer maxMembers = 500;  // 最大成员数
    @Builder.Default
    private Boolean needApproval = true; // 是否需要审批
    @Builder.Default
    private Boolean allowInvite = true;  // 是否允许邀请
    @Builder.Default
    private Boolean muteAll = false;     // 是否全员禁言

    // 邀请用户加入群组
    public void inviteMembers(List<UserEntity> users) {
        // if (!allowInvite) {
        //     throw new GroupOperationException("Group does not allow invitations");
        // }

        // if (members.size() + users.size() > maxMembers) {
        //     throw new GroupOperationException("Group member limit exceeded");
        // }

        // for (UserEntity user : users) {
        //     MemberEntity member = MemberEntity.builder()
        //         .user(user)
        //         .group(this)
        //         .joinTime(LocalDateTime.now())
        //         .build();
        //     members.add(member);
        // }
    }

    // 移除群成员
    public void removeMember(UserEntity user) {
        members.removeIf(member -> member.getUser().equals(user));
    }

    // 添加管理员
    public void addAdmin(UserEntity user) {
        if (!admins.contains(user)) {
            admins.add(user);
        }
    }

    // 移除管理员
    public void removeAdmin(UserEntity user) {
        admins.remove(user);
    }

    // 检查用户是否是群成员
    public Boolean isMember(UserEntity user) {
        return members.stream()
            .anyMatch(member -> member.getUser().equals(user));
    }

    // 检查用户是否是管理员
    public Boolean isAdmin(UserEntity user) {
        return admins.contains(user);
    }

    // 全员禁言
    public void muteAll() {
        this.muteAll = true;
    }

    // 解除全员禁言
    public void unMuteAll() {
        this.muteAll = false;
    }

    // 更新群组信息
    public void updateInfo(String name, String description, String avatar) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (avatar != null) this.avatar = avatar;
    }

    // 解散群组
    public void dismiss() {
        this.status = GroupStatusEnum.DISMISSED.name();
        this.members.clear();
        this.admins.clear();
    }

}
