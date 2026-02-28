package com.bytedesk.video.webrtc;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WebrtcRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String callUid;

    private String status;

    private String type;

    private String threadUid;

    private Long roomId;

    private Boolean record;

    private String recordFilename;
}
