package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 会话详情
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ThreadContent extends BaseContent {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String topic;
    private String type;
    private String status;
    private String channel;
    private String user;
    private String agent;
    private String robot;
    private String workgroup;
    private String createdAt;
    private String updatedAt;
    private String preview;
    
}