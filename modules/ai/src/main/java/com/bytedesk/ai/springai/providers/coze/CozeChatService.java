/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-23 06:10:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 15:12:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.coze;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.CreateChatResp;
import com.coze.openapi.client.chat.RetrieveChatReq;
import com.coze.openapi.client.chat.RetrieveChatResp;
import com.coze.openapi.client.chat.model.Chat;
import com.coze.openapi.client.chat.model.ChatEvent;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.chat.model.ChatPoll;
import com.coze.openapi.client.chat.model.ChatStatus;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.client.connversations.message.model.MessageObjectString;
import com.coze.openapi.client.connversations.message.model.MessageType;
import com.coze.openapi.client.files.UploadFileReq;
import com.coze.openapi.client.files.model.FileInfo;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.service.CozeAPI;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

/**
 * https://www.coze.cn/open/docs/developer_guides
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "bytedesk.coze.enabled", havingValue = "true", matchIfMissing = false)
public class CozeChatService {

    @Value("${bytedesk.coze.token:}")
    private String token;

    @Value("${bytedesk.coze.botID:}")
    private String botID;

    @Value("${bytedesk.coze.base-url:https://api.coze.cn}")
    private String baseURL;

    public ChatPoll Chat(String question, String userID) {
        // Get an access_token through personal access token or oauth.

        TokenAuth authCli = new TokenAuth(token);

        // Init the Coze client through the access_token.
        CozeAPI coze = new CozeAPI.Builder()
                .baseURL(baseURL)
                .auth(authCli)
                .readTimeout(10000)
                .build();

        /*
         * Step one, create chat
         * Call the coze.chat().create() method to create a chat. The create method is a
         * non-streaming
         * chat and will return a Chat class. Developers should periodically check the
         * status of the
         * chat and handle them separately according to different states.
         */
        CreateChatReq req = CreateChatReq.builder()
                .botID(botID)
                .userID(userID)
                .messages(Collections.singletonList(Message.buildUserQuestionText(question)))
                .build();

        CreateChatResp chatResp = coze.chat().create(req);
        log.info("chatResp: {}", chatResp);
        //
        Chat chat = chatResp.getChat();
        // get chat id and conversationID
        String chatID = chat.getID();
        String conversationID = chat.getConversationID();

        /*
         * Step two, poll the result of chat
         * Assume the development allows at most one chat to run for 10 seconds. If it
         * exceeds 10 seconds,
         * the chat will be cancelled.
         * And when the chat status is not completed, poll the status of the chat once
         * every second.
         * After the chat is completed, retrieve all messages in the chat.
         */
        long timeout = 10L;
        long start = System.currentTimeMillis() / 1000;
        while (ChatStatus.IN_PROGRESS.equals(chat.getStatus())) {
            try {
                // The API has a rate limit with 1 QPS.
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            if ((System.currentTimeMillis() / 1000) - start > timeout) {
                // The chat can be cancelled before its completed.
                log.info("cancel chat, conversationID: {}, chatID: {}", conversationID, chatID);
                break;
            }
            //
            RetrieveChatResp resp = coze.chat().retrieve(RetrieveChatReq.of(conversationID, chatID));
            log.info("resp: {}", resp);
            // update chat
            chat = resp.getChat();
            if (ChatStatus.COMPLETED.equals(chat.getStatus())) {
                break;
            }
        }

        // The sdk provide an automatic polling method.
        ChatPoll chat2;
        try {
            chat2 = coze.chat().createAndPoll(req);
            log.info("chat2: {}", chat2);

            return chat2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public void ChatWithImage(String imagePath, String userID) {

        TokenAuth authCli = new TokenAuth(token);

        // Init the Coze client through the access_token.
        CozeAPI coze = new CozeAPI.Builder()
                .baseURL(baseURL)
                .auth(authCli)
                .readTimeout(10000)
                .build();
        ;

        // Call the upload file interface to get the image id.
        FileInfo imageInfo = coze.files().upload(UploadFileReq.of(imagePath)).getFileInfo();

        /*
         * Step one, create chat
         * Call the coze.chat().stream() method to create a chat. The create method is a
         * streaming
         * chat and will return a Flowable ChatEvent. Developers should iterate the
         * iterator to get
         * chat event and handle them.
         */
        CreateChatReq req = CreateChatReq.builder()
                .botID(botID)
                .userID(userID)
                .messages(
                        Collections.singletonList(
                                Message.buildUserQuestionObjects(
                                        Arrays.asList(
                                                MessageObjectString.buildText("Describe this picture"),
                                                MessageObjectString.buildImageByID(imageInfo.getID())))))
                .build();

        Flowable<ChatEvent> resp = coze.chat().stream(req);
        resp.blockingForEach(
                event -> {
                    if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())) {
                        System.out.print(event.getMessage().getContent());
                    }
                });
    }

    public void ChatStream(String question, String userID, SseEmitter emitter) {

        TokenAuth authCli = new TokenAuth(token);

        // Init the Coze client through the access_token.
        CozeAPI coze = new CozeAPI.Builder()
                .baseURL(baseURL)
                .auth(authCli)
                .readTimeout(10000)
                .build();

        /*
         * Step one, create chat
         * Call the coze.chat().stream() method to create a chat. The create method is a
         * streaming
         * chat and will return a Flowable ChatEvent. Developers should iterate the
         * iterator to get
         * chat event and handle them.
         */
        CreateChatReq req = CreateChatReq.builder()
                .botID(botID)
                .userID(userID)
                .messages(Collections.singletonList(Message.buildUserQuestionText(question)))
                .build();

        // 使用原子布尔值来跟踪是否已经完成
        final boolean[] isCompleted = {false};

        try {
            Flowable<ChatEvent> resp = coze.chat().stream(req);
            resp.subscribeOn(Schedulers.io())
                    .subscribe(
                            event -> {
                                if (isCompleted[0]) {
                                    return; // 如果已经完成，跳过处理
                                }
                                
                                try {
                                    if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())) {
                                        // 检查消息是否为空
                                        if (event.getMessage() != null && event.getMessage().getContent() != null) {
                                            // 发送流式数据
                                            emitter.send(SseEmitter.event()
                                                    .name("message")
                                                    .data(event.getMessage().getContent()));
                                        }
                                    }
                                    if (ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent())) {
                                        if (event.getMessage() != null && event.getMessage().getType() != null) {
                                            if (MessageType.FOLLOW_UP.equals(event.getMessage().getType())) {
                                                emitter.send(SseEmitter.event()
                                                        .name("followup")
                                                        .data(event.getMessage().getContent()));
                                            }
                                        }
                                        // 发送使用量信息（如果可用）
                                        if (event.getChat() != null && event.getChat().getUsage() != null) {
                                            emitter.send(SseEmitter.event()
                                                    .name("usage")
                                                    .data("Token usage:" + event.getChat().getUsage().getTokenCount()));
                                        }
                                    }
                                    if (ChatEventType.DONE.equals(event.getEvent())) {
                                        if (!isCompleted[0]) {
                                            isCompleted[0] = true;
                                            emitter.send(SseEmitter.event()
                                                    .name("done")
                                                    .data("conversation completed"));
                                            emitter.complete();
                                            coze.shutdownExecutor();
                                        }
                                    }
                                } catch (IllegalStateException e) {
                                    // SseEmitter 已经完成，记录日志但不重新抛出异常
                                    log.warn("SseEmitter already completed, ignoring event: {}", event.getEvent());
                                    isCompleted[0] = true;
                                } catch (Exception e) {
                                    log.error("Error sending SSE event", e);
                                    if (!isCompleted[0]) {
                                        isCompleted[0] = true;
                                        try {
                                            emitter.completeWithError(e);
                                        } catch (IllegalStateException ise) {
                                            log.warn("SseEmitter already completed when trying to complete with error");
                                        }
                                    }
                                }
                            },
                            throwable -> {
                                if (!isCompleted[0]) {
                                    isCompleted[0] = true;
                                    log.error("Error occurred in chat stream", throwable);
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .name("error")
                                                .data("Error occurred: " + throwable.getMessage()));
                                        emitter.completeWithError(throwable);
                                    } catch (IllegalStateException e) {
                                        log.warn("SseEmitter already completed when trying to send error");
                                    } catch (Exception e) {
                                        log.error("Error sending error event", e);
                                    }
                                }
                                coze.shutdownExecutor();
                            },
                            () -> {
                                if (!isCompleted[0]) {
                                    isCompleted[0] = true;
                                    log.info("Chat stream completed");
                                    try {
                                        emitter.complete();
                                    } catch (IllegalStateException e) {
                                        log.warn("SseEmitter already completed in onComplete");
                                    } catch (Exception e) {
                                        log.error("Error completing emitter", e);
                                    }
                                }
                                coze.shutdownExecutor();
                            });
        } catch (Exception e) {
            log.error("Error starting chat stream", e);
            if (!isCompleted[0]) {
                isCompleted[0] = true;
                try {
                    emitter.completeWithError(e);
                } catch (IllegalStateException ise) {
                    log.warn("SseEmitter already completed when trying to complete with error in catch block");
                } catch (Exception ex) {
                    log.error("Error completing emitter with error", ex);
                }
            }
        }
    }

}
