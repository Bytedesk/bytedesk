package com.bytedesk.core.workflow_settings;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowIframeTab implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String url;
}