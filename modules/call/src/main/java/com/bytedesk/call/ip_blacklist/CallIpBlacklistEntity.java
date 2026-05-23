package com.bytedesk.call.ip_blacklist;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import org.modelmapper.ModelMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Table(
    name = "bytedesk_call_ip_blacklist",
    indexes = {
        @Index(name = "idx_call_ip_blacklist_uid", columnList = "uuid")
    }
)
public class CallIpBlacklistEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "source_esl_event_uid")
    private String sourceEslEventUid;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "caller_number")
    private String callerNumber;

    @Builder.Default
    private String reason = I18Consts.I18N_DESCRIPTION;

    public static CallIpBlacklistEntity fromRequest(CallIpBlacklistRequest request, ModelMapper modelMapper) {
        if (request == null || modelMapper == null) {
            return CallIpBlacklistEntity.builder().build();
        }
        return modelMapper.map(request, CallIpBlacklistEntity.class);
    }
}