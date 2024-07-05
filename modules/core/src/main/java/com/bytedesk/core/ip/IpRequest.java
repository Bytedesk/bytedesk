package com.bytedesk.core.ip;

import com.bytedesk.core.base.BaseRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpRequest extends BaseRequest {

    private String ip;
    // private String ipLocation;

    private String ipRangeStart;

    private String ipRangeEnd;

    private String type;

    private String reason;

    // time duration
    private String untilDate;

    @NotBlank
    private String orgUid;
}
