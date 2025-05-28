/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-24 13:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 18:58:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChunkProtobuf implements Serializable {

	private static final long serialVersionUID = 1L;

    private String uid;

    private String name;

    private String content;

    @Builder.Default
    private List<ChunkProtobuf> relatedChunks = new ArrayList<>();

    public static ChunkProtobuf fromJson(String chunk) {
        return JSON.parseObject(chunk, ChunkProtobuf.class);
    }

    public static ChunkProtobuf fromElastic(ChunkElastic chunk) {
        return ChunkProtobuf.builder()
                .uid(chunk.getUid())
                .name(chunk.getName())
                .content(chunk.getContent())
                .build();
    }
    
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
