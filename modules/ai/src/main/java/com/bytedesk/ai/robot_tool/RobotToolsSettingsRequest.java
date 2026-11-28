package com.bytedesk.ai.robot_tool;

import java.io.Serial;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RobotToolsSettingsRequest extends BaseRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;

    private Boolean intentRecognitionEnabled;

    private String intentModel;

    private String intentProviderUid;

    private Integer intentTimeoutMs;

    private List<RobotToolConfig> toolConfigs;
}
