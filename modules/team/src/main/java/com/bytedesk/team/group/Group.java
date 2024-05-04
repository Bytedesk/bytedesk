/*
 * @Author: jack ning github@bytedesk.com
 * @Date: 2024-01-23 14:53:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:27:41
 * @FilePath: /server/plugins/im/src/main/java/com/bytedesk/im/group/Group.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.bytedesk.team.group;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.utils.AbstractEntity;
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
// @EntityListeners({ GroupListener.class })
// 注：group为mysql保留关键字, groups在mysql8启动报错，所有表名修改为groupes
@Table(name = "team_groupes")
public class Group extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    // @Column(unique = true, nullable = false)
    // private String gid;

    private String name;

    private String avatar;

    private String description;

    /**
     * 群成员
     */
    @JsonIgnore
    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    // @JoinTable(name = "team_group_members", 
    //     joinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)), 
    //     inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)))
    private List<User> members = new ArrayList<>();

    /**
     * 群创建者
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    /**
     * 所属管理员id，后台管理查看
     */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "admin_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    // private User admin;

}
