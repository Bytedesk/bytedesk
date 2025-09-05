/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-15 16:49:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 12:47:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.jms;

public class JmsArtemisConsts {
    
    // queue 为队列消息，每个实例轮流收取
    public static final String QUEUE_PREFIX = "bytedesk.queue.";

    public static final String QUEUE_STRING_NAME = QUEUE_PREFIX + "string";

    public static final String QUEUE_MESSAGE_NAME = QUEUE_PREFIX + "message";

    public static final String QUEUE_TEST_NAME = QUEUE_PREFIX + "test";

    // topic 为pubsub广播消息，所有实例同时收取
    public static final String TOPIC_PREFIX = "bytedesk.topic.";

    public static final String TOPIC_STRING_NAME = TOPIC_PREFIX + "string";

    public static final String TOPIC_MESSAGE_NAME = TOPIC_PREFIX + "message";

    public static final String TOPIC_TEST_NAME = TOPIC_PREFIX + "test";

    // FAQ索引队列
    public static final String QUEUE_FAQ_INDEX = QUEUE_PREFIX + "faq.index";
    // Chunk索引队列
    public static final String QUEUE_CHUNK_INDEX = QUEUE_PREFIX + "chunk.index";
    // Chunk处理完成队列
    public static final String QUEUE_CHUNK_COMPLETE = QUEUE_PREFIX + "chunk.complete";
    // Article索引队列
    public static final String QUEUE_ARTICLE_INDEX = QUEUE_PREFIX + "article.index";
    // Webpage索引队列
    public static final String QUEUE_WEBPAGE_INDEX = QUEUE_PREFIX + "webpage.index";
    // QuickReply索引队列
    public static final String QUEUE_QUICK_REPLY_INDEX = QUEUE_PREFIX + "quick_reply.index";
    // queue member 队列
    public static final String QUEUE_MEMBER_UPDATE = QUEUE_PREFIX + "member.update";
    // Member批量导入队列
    public static final String QUEUE_MEMBER_BATCH_IMPORT = QUEUE_PREFIX + "member.batch.import";
    
    // 文件Chunk处理队列
    public static final String QUEUE_FILE_CHUNK_PROCESS = QUEUE_PREFIX + "file.chunk.process";
    // 文件Chunk重试队列
    public static final String QUEUE_FILE_CHUNK_RETRY = QUEUE_PREFIX + "file.chunk.retry";
    // 文件Chunk处理完成队列
    public static final String QUEUE_FILE_CHUNK_COMPLETE = QUEUE_PREFIX + "file.chunk.complete";
    // 文件Chunk索引队列
    public static final String QUEUE_FILE_CHUNK_INDEX = QUEUE_PREFIX + "file.chunk.index";
}
