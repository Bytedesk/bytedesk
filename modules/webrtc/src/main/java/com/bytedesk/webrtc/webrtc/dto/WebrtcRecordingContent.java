/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is prohibited – see the LICENSE for details.
 *   仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   技术/商务联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.webrtc.webrtc.dto;

import java.nio.file.Path;

import lombok.Builder;
import lombok.Value;

/**
 * 通话录制文件解析结果（供管理端流式播放/下载）：
 * filename 用于 Content-Disposition，contentType 按通话类型推导（AUDIO→audio/webm，其余 video/webm），
 * filePath 为存储根目录内的规范化绝对路径，由 Controller 包装为 Resource 输出。
 */
@Value
@Builder
public class WebrtcRecordingContent {

    private String callUid;

    private String filename;

    private String contentType;

    private Path filePath;
}
