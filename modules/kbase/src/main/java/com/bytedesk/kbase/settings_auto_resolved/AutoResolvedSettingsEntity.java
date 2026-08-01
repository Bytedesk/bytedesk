/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Auto resolved timeout settings
 */
package com.bytedesk.kbase.settings_auto_resolved;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bytedesk_kbase_auto_resolved_settings")
public class AutoResolvedSettingsEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = false;

    @Builder.Default
    @Column(name = "timeout_hours")
    private Integer timeoutHours = 24;

    @Builder.Default
    @Column(name = "notify_visitor_on_auto_resolved")
    private Boolean notifyVisitorOnAutoResolved = false;

    public static AutoResolvedSettingsEntity fromRequest(AutoResolvedSettingsRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return AutoResolvedSettingsEntity.builder().build();
        }
        return modelMapper.map(request, AutoResolvedSettingsEntity.class);
    }
}