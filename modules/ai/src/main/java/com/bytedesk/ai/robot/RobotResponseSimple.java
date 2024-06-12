package com.bytedesk.ai.robot;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RobotResponseSimple extends BaseResponse {
    
    private static final long serialVersionUID = 1L;

    private String nickname;

    private String avatar;

    private RobotLlm llm;
}
