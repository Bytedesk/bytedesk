package com.bytedesk.team.department;

import java.util.List;

import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.utils.AuditModel;
import com.bytedesk.team.member.Member;
import com.bytedesk.team.organization.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 部门
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_department")
public class Department extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    /**
     * 
     */
    @Column(name = "did", unique = true, nullable = false)
    private String did;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    @Column(name = "by_type")
    private String type;

    /**
     * 
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Department parent;

    /**
     * 
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Member> members;

    /**
     * 
     */
    // @Column(name = "organization_oid")
    // private String organizationOid;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Organization organization;

    /**
     * created by
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    /**
     * 下级部门
     */
    @JsonIgnore
    @Transient
    private List<Department> children;

    public String toString() {
        return this.name;
    }

}
