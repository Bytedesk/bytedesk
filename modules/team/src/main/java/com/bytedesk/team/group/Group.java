/*
 * @Author: jack ning github@bytedesk.com
 * @Date: 2024-01-23 14:53:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-19 08:37:19
 * @FilePath: /server/plugins/im/src/main/java/com/bytedesk/im/group/Group.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.bytedesk.team.group;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.team.member.Member;
import com.bytedesk.ai.robot.Robot;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ GroupEntityListener.class })
@Table(name = "team_groups")
public class Group extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String name = I18Consts.I18N_GROUP_NAME;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_GROUP_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_GROUP_DESCRIPTION;

    // 是否显示顶部通知提示
    @Builder.Default
    private boolean showTopTip = false;

    // 通知栏提示
    @Builder.Default
    private String topTip = BdConstants.EMPTY_STRING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false)
    private GroupTypeEnum type = GroupTypeEnum.MEMBER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private GroupStatusEnum status = GroupStatusEnum.NORMAL;

    @JsonIgnore
    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<User> admins = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<Robot> robots = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;

}
