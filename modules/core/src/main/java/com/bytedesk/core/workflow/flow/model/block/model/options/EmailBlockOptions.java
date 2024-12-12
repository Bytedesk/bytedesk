package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Map;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmailBlockOptions extends BlockOptions {
    private String from;
    private String to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private String replyTo;
    private List<Attachment> attachments;
    private EmailProvider provider;
    private Map<String, String> credentials;
}
