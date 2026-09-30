package com.bytedesk.webrtc.webrtc;

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

    /**
     * 场景筛选（VISITOR_SERVICE/MEMBER_CALL，规划 §5.7）：空不过滤；
     * VISITOR_SERVICE 需兼容存量 NULL 行（查询侧 IS NULL OR = 'VISITOR_SERVICE'）。
     */
    private String scene;
}
