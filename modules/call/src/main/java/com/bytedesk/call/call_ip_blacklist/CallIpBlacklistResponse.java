package com.bytedesk.call.call_ip_blacklist;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CallIpBlacklistResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String ipAddress;

    private String sourceEslEventUid;

    private String eventName;

    private String callerNumber;

    private String reason;
}