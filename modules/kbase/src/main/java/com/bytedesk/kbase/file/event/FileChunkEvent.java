/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:59:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 09:52:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file.event;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.context.ApplicationEvent;

import com.bytedesk.kbase.file.FileResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileChunkEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private List<Document> documents;

    private FileResponse fileResponse;

    public FileChunkEvent(List<Document> documents, FileResponse fileResponse) {
        super(documents);
        this.documents = documents;
        this.fileResponse = fileResponse;
    }

}
