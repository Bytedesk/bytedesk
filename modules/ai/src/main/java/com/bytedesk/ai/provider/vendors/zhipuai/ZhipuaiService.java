/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 15:39:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-03 15:26:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider.vendors.zhipuai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotMessage;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotTypeEnum;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
// import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JsonResultCodeEnum;
import com.bytedesk.kbase.upload.UploadVectorStore;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageAccumulator;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.Choice;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ModelData;

import io.reactivex.Flowable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://open.bigmodel.cn/dev/api#sdk_install
 * https://github.com/MetaGLM/zhipuai-sdk-java-v4
 * 
 * https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html
 */
@Slf4j
@Service
@AllArgsConstructor
public class ZhipuaiService {

    private final ZhipuaiConfig zhipuaiConfig;

    private final ClientV4 client;

    private final UidUtils uidUtils;

    private final ThreadRestService threadService;

    private final ModelMapper modelMapper;

    private final MessageRestService messageService;

    private final UploadVectorStore uploadVectorStore;

    private final IMessageSendService messageSendService;

    private final String PROMPT_BLUEPRINT = """
            根据提供的文档信息回答问题，文档信息如下:
            {context}
            问题:
            {query}
            当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
            """;

    // RAG智能客服提示模板
    private final String PROMPT_TEMPLATE = """
              任务描述：根据用户的查询和文档信息回答问题，并结合历史聊天记录生成简要的回答。

              用户查询: {query}

              历史聊天记录: {history}

              搜索结果: {context}

              请根据以上信息生成一个简单明了的回答，确保信息准确且易于理解。
              当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
              另外，请提供更多相关的问答对。
              回答内容请以JSON格式输出，格式如下：
              {
                "answer": "回答内容",
                "additional_qa_pairs": [
                    {"question": "相关问题1", "answer": "相关答案1"},
                    {"question": "相关问题2", "answer": "相关答案2"}
                ]
              }
            """;

    private final String PROMPT_QA_TEMPLATE = """
            基于以下给定的文本，生成一组高质量的问答对。请遵循以下指南:

            1. 问题部分：
            - 为同一个主题创建多个不同表述的问题，确保问题的多样性
            - 每个问题应考虑用户可能的多种问法，例如：
              - 直接询问（如"什么是...？"）
              - 请求确认（如"是否可以说...？"）
              - 如何做（如"如何实现...？"）
              - 为什么（如"为什么需要...？"）
              - 比较类（如"...和...有什么区别？"）

            2. 答案部分：
            - 答案应该准确、完整且易于理解
            - 使用简洁清晰的语言
            - 适当添加示例说明
            - 可以包含关键步骤或要点
            - 必要时提供相关概念解释

            3. 格式要求：
            - 以JSON数组形式输出
            - 每个问答对包含question和answer字段
            - 可选添加type字段标识问题类型
            - 可选添加tags字段标识相关标签

            4. 质量控制：
            - 确保问答对之间不重复
            - 问题应该有实际意义和价值
            - 答案需要准确且有帮助
            - 覆盖文本中的重要信息点

            5. 示例格式：
            {
              "qaPairs": [
                {
                  "id": "1",
                  "question": "问题描述",
                  "answer": "答案内容",
                  "tags": ["标签1", "标签2"]
                }
              ]
            }

            给定文本：
            {chunk}

            请基于这个文本生成问答对
            """;

    // 知识库问答
    public void sendWsKbMessage(String query, String kbUid, RobotEntity robot, MessageProtobuf messageProtobuf) {
        //
        String prompt = robot.getLlm().getPrompt();
        if (robot.getType().equals(RobotTypeEnum.SERVICE.name())) {
            List<String> contentList = uploadVectorStore.searchText(query, kbUid);
            String context = String.join("\n", contentList);
            String history = ""; // TODO: 历史对话上下文，此处暂不使用
            prompt = PROMPT_TEMPLATE.replace("{context}", context).replace("{query}", query).replace("{history}",
                    history);
            log.info("sendWsRobotMessage prompt 1 {}", prompt);
        } else {
            prompt = prompt + "\n" + query;
            log.info("sendWsRobotMessage prompt 2 {}", prompt);
        }
        //
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(chatMessage);
        //
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                // 模型名称
                .model(zhipuaiConfig.zhiPuAiApiModel)
                // .model(Constants.ModelChatGLM3TURBO)
                // .model(robotSimple.getLlm().getModel())
                // .temperature(robotSimple.getLlm().getTemperature())
                // .topP(robotSimple.getLlm().getTopP())
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(messageProtobuf.getUid())
                .build();
        //
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            List<Choice> choices = new ArrayList<>();
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                log.info("answer start: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = JSON.toJSONString(accumulator.getDelta().getTool_calls());
                                log.info("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                String answerContent = accumulator.getDelta().getContent();
                                log.info("answerContent {}", answerContent);
                                if (StringUtils.hasText(answerContent)) {
                                    messageProtobuf.setType(MessageTypeEnum.STREAM);
                                    messageProtobuf.setContent(answerContent);
                                    //
                                    // MessageUtils.notifyUser(messageProtobuf);
                                    messageSendService.sendProtobufMessage(messageProtobuf);
                                }
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("answer end");
                    })
                    .lastElement()
                    .blockingGet();

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);// 打印前置空
            sseModelApiResp.setData(data);
            // 存储到数据库
            // robotMessage.setPromptTokens(chatMessageAccumulator.getUsage().getPromptTokens());
            // robotMessage.setCompletionTokens(chatMessageAccumulator.getUsage().getCompletionTokens());
            // robotMessage.setTotalTokens(chatMessageAccumulator.getUsage().getTotalTokens());
        }

        String result = JSON.toJSONString(sseModelApiResp);
        log.info("websocket output:" + result);
    }

    public void sendWsKbAutoReply(String query, String kbUid, MessageProtobuf messageProtobuf) {
        //
        List<String> contentList = uploadVectorStore.searchText(query, kbUid);
        String context = String.join("\n", contentList);
        String prompt = PROMPT_BLUEPRINT.replace("{context}", context).replace("{query}", query);
        log.info("sendWsAutoReply prompt {}", prompt);
        //
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(chatMessage);
        //
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                // 模型名称
                .model(zhipuaiConfig.zhiPuAiApiModel)
                // .model(Constants.ModelChatGLM3TURBO)
                // .model(robotSimple.getLlm().getModel())
                // .temperature(robotSimple.getLlm().getTemperature())
                // .topP(robotSimple.getLlm().getTopP())
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(messageProtobuf.getUid())
                .build();
        //
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            List<Choice> choices = new ArrayList<>();
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                log.info("answer start: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = JSON.toJSONString(accumulator.getDelta().getTool_calls());
                                log.info("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                String answerContent = accumulator.getDelta().getContent();
                                log.info("answerContent {}", answerContent);
                                if (StringUtils.hasText(answerContent)) {
                                    messageProtobuf.setType(MessageTypeEnum.STREAM);
                                    messageProtobuf.setContent(answerContent);
                                    //
                                    // MessageUtils.notifyUser(messageProtobuf);
                                    messageSendService.sendProtobufMessage(messageProtobuf);
                                }
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("answer end");
                    })
                    .lastElement()
                    .blockingGet();

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);// 打印前置空
            sseModelApiResp.setData(data);
            // 存储到数据库
            // robotMessage.setPromptTokens(chatMessageAccumulator.getUsage().getPromptTokens());
            // robotMessage.setCompletionTokens(chatMessageAccumulator.getUsage().getCompletionTokens());
            // robotMessage.setTotalTokens(chatMessageAccumulator.getUsage().getTotalTokens());
        }

        String result = JSON.toJSONString(sseModelApiResp);
        log.info("websocket output:" + result);

    }

    public String generateQaPairsAsync(String chunk) {
        String prompt = PROMPT_QA_TEMPLATE.replace("{chunk}", chunk);
        log.info("generateQaPairs prompt {}", prompt);
        //
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(chatMessage);
        //
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                // 模型名称
                .model(zhipuaiConfig.zhiPuAiApiModel)
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(uidUtils.getUid())
                .build();
        //
        StringBuilder answer = new StringBuilder();
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            List<Choice> choices = new ArrayList<>();
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                log.info("answer start: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = JSON.toJSONString(accumulator.getDelta().getTool_calls());
                                log.info("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                String answerContent = accumulator.getDelta().getContent();
                                // log.info("answerContent {}", answerContent);
                                if (StringUtils.hasText(answerContent)) {
                                    answer.append(answerContent);
                                }
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("answer end");
                    })
                    .lastElement()
                    .blockingGet();

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);// 打印前置空
            sseModelApiResp.setData(data);
            // 存储到数据库
            // robotMessage.setPromptTokens(chatMessageAccumulator.getUsage().getPromptTokens());
            // robotMessage.setCompletionTokens(chatMessageAccumulator.getUsage().getCompletionTokens());
            // robotMessage.setTotalTokens(chatMessageAccumulator.getUsage().getTotalTokens());
        }

        // {"code":200,"data":{"array":false,"bigDecimal":false,"bigInteger":false,"binary":false,"boolean":false,"choices":[],"containerNode":true,"created":1735878897,"double":false,"empty":false,"float":false,"floatingPointNumber":false,"id":"202501031234572bc205aee4ee42d2","int":false,"integralNumber":false,"long":false,"missingNode":false,"nodeType":"OBJECT","null":false,"number":false,"object":true,"pojo":false,"request_id":"1554299146469504","short":false,"textual":false,"usage":{"completion_tokens":401,"prompt_tokens":493,"total_tokens":894},"valueNode":false},"msg":"成功","success":true}
        log.info("generateQaPairsAsync result {}", JSON.toJSONString(sseModelApiResp));
        /** 
         以下是根据文本生成的问答对：
        ```json
        {
            "qaPairs": [
                {
                    "question": "什么是北京市人事考评办公室的监督举报渠道？",
                    "answer": "北京市人事考评办公室的监督举报渠道是纪检监察监督举报，可以通过访问网站https://beijing.12388.gov.cn/进行举报。",
                    "tags": ["监督举报", "人事考评办公室"]
                },
                {
                    "question": "北京市人事考评办公室是否与任何培训机构有合作关系？",
                    "answer": "不是，北京市人事考评办公室不指定任何培训，并且与任何培训机构无合作关系。",
                    "tags": ["人事考评办公室", "培训机构"]
                },
                {
                    "question": "北京市人事考评办公室的文件是由哪个部门印发的？",
                    "answer": "北京市人事考评办公室的文件是由其自身于2023年9月19日印发。",
                    "tags": ["文件印发", "人事考评办公室"]
                },
                {
                    "question": "北京市人事考评办公室的文件抄送给了哪些部门？",
                    "answer": "北京市人事考评办公室的文件抄送给了市人力资源和社会保障局办公室、事业单位人事管理处。",
                    "tags": ["文件抄送", "政府部门"]
                }
            ]
        }
        ```
        */
        // log.info("generateQaPairsAsync answer {}", answer.toString());
        return answer.toString();
    }

    // FIXME: Caused by: java.net.SocketTimeoutException: timeout
    public void generateQaPairsSync(String chunk) {
        String prompt = PROMPT_QA_TEMPLATE.replace("{chunk}", chunk);
        log.info("generateQaPairs prompt {}", prompt);
        
        // 构建消息
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(chatMessage);
        
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(zhipuaiConfig.zhiPuAiApiModel)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(uidUtils.getUid())
                .build();

        // 添加重试逻辑
        int maxRetries = 3;
        int retryCount = 0;
        int retryDelay = 1000; // 初始延迟1秒

        while (retryCount < maxRetries) {
            try {
                ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
                log.info("model output: {}", JSON.toJSONString(invokeModelApiResp));
                return; // 成功则返回
                
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    log.error("Failed to generate QA pairs after {} retries", maxRetries, e);
                    throw new RuntimeException("Failed to generate QA pairs: " + e.getMessage(), e);
                }
                
                // 指数退避重试
                int delay = retryDelay * (1 << (retryCount - 1));
                log.warn("Retry {}/{} after {}ms due to: {}", 
                        retryCount, maxRetries, delay, e.getMessage());
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying", ie);
                }
            }
        }
    }

    /**
     * sse调用
     */
    public void getSseAnswer(String uid, String sid, String question, SseEmitter emitter) {
        String topic = sid + "/" + uid;
        ThreadEntity thread = threadService.findFirstByTopic(topic)
                .orElseThrow(() -> new RuntimeException("thread with topic: " + topic + " not found"));

        RobotMessage robotMessage = RobotMessage.builder().question(question).build();

        RobotProtobuf robotSimple = JSON.parseObject(thread.getAgent(), RobotProtobuf.class);
        log.info("robotSimple {}", robotSimple);

        UserProtobuf user = modelMapper.map(thread.getAgent(), UserProtobuf.class);
        //
        String messageUid = uidUtils.getCacheSerialUid();
        MessageEntity message = MessageEntity.builder()
                .type(MessageTypeEnum.ROBOT.name())
                .status(MessageStatusEnum.SUCCESS.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(user))
                .build();
        message.setUid(messageUid);
        message.setOrgUid(thread.getOrgUid());
        //
        message.setThreadTopic(thread.getTopic());

        if (!robotSimple.getLlm().isEnabled()) {
            // 机器人未开启
            message.setContent(JsonResultCodeEnum.ROBOT_DISABLED.getName());
            try {
                emitter.send(SseEmitter.event()
                        .data(JsonResult.success(
                                JsonResultCodeEnum.ROBOT_DISABLED.getName(),
                                JsonResultCodeEnum.ROBOT_DISABLED.getValue(), message)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 完成后完成SSE流
            emitter.complete();

            return;
        }

        //
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
        messages.add(chatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                // 模型名称
                // .model(Constants.ModelChatGLM3TURBO)
                .model(robotSimple.getLlm().getModel())
                .temperature(robotSimple.getLlm().getTemperature())
                .topP(robotSimple.getLlm().getTopP())
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(messageUid)
                .build();
        //
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            List<Choice> choices = new ArrayList<>();
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                log.info("answer start: ");
                                robotMessage.clearAnswer();
                                message.setContent(JsonResultCodeEnum.ROBOT_ANSWER_START.getName());
                                emitter.send(SseEmitter.event().data(JsonResult.success(
                                        JsonResultCodeEnum.ROBOT_ANSWER_START.getName(),
                                        JsonResultCodeEnum.ROBOT_ANSWER_START.getValue(), message)));
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = JSON.toJSONString(accumulator.getDelta().getTool_calls());
                                log.info("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                String answerContent = accumulator.getDelta().getContent();
                                // delta {"role":"assistant","content":"告诉我","tool_calls":[]} answerContent 告诉我
                                robotMessage.appendAnswer(answerContent);
                                message.setContent(JSON.toJSONString(robotMessage));
                                log.info("delta {} answerContent {}", accumulator.getDelta().toString(), answerContent);
                                emitter.send(SseEmitter.event().data(JsonResult.success(
                                        JsonResultCodeEnum.ROBOT_ANSWER_CONTINUE.getName(),
                                        JsonResultCodeEnum.ROBOT_ANSWER_CONTINUE.getValue(), message)));
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("answer end");
                        message.setContent(JsonResultCodeEnum.ROBOT_ANSWER_END.getName());
                        emitter.send(SseEmitter.event().data(JsonResult.success(
                                JsonResultCodeEnum.ROBOT_ANSWER_END.getName(),
                                JsonResultCodeEnum.ROBOT_ANSWER_END.getValue(), message)));
                        // 完成后完成SSE流
                        emitter.complete();
                    })
                    .lastElement()
                    .blockingGet();

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);// 打印前置空
            sseModelApiResp.setData(data);
            // 存储到数据库
            robotMessage.setPromptTokens(chatMessageAccumulator.getUsage().getPromptTokens());
            robotMessage.setCompletionTokens(chatMessageAccumulator.getUsage().getCompletionTokens());
            robotMessage.setTotalTokens(chatMessageAccumulator.getUsage().getTotalTokens());
            //
            message.setContent(JSON.toJSONString(robotMessage));
            messageService.save(message);
        }

        String result = JSON.toJSONString(sseModelApiResp);
        // sse output:
        // {"code":200,"data":{"array":false,"bigDecimal":false,"bigInteger":false,"binary":false,"boolean":false,"choices":[],
        // "containerNode":true,"created":1717580268,"double":false,"empty":false,"float":false,"floatingPointNumber":false,
        // "id":"8718088513501356715","int":false,"integralNumber":false,"long":false,"missingNode":false,"nodeType":"OBJECT",
        // "null":false,"number":false,"object":true,"pojo":false,"request_id":"bytedesk-1717580267785","short":false,
        // "textual":false,"usage":{"completion_tokens":28,"prompt_tokens":6,"total_tokens":34},"valueNode":false},"success":true}
        log.info("sse output:" + result);
    }

    /**
     * websocket 发送消息
     * LLM 聊天
     */
    public void sendWsMessage(String query, RobotLlm robotLlm, MessageProtobuf messageProtobuf) {
        //
        String prompt = robotLlm.getPrompt() + "\n" + query;
        //
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(chatMessage);
        //
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(robotLlm.getModel())
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(messageProtobuf.getUid())
                .build();
        //
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            List<Choice> choices = new ArrayList<>();
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                log.info("answer start: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = JSON.toJSONString(accumulator.getDelta().getTool_calls());
                                log.info("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                String answerContent = accumulator.getDelta().getContent();
                                log.info("answerContent {}", answerContent);
                                if (StringUtils.hasText(answerContent)) {
                                    messageProtobuf.setType(MessageTypeEnum.STREAM);
                                    messageProtobuf.setContent(answerContent);
                                    //
                                    // MessageUtils.notifyUser(messageProtobuf);
                                    messageSendService.sendProtobufMessage(messageProtobuf);
                                }
                            }
                        }
                    })
                    .doOnComplete(() -> {
                        log.info("answer end");
                    })
                    .lastElement()
                    .blockingGet();

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);// 打印前置空
            sseModelApiResp.setData(data);
            // 存储到数据库
            // robotMessage.setPromptTokens(chatMessageAccumulator.getUsage().getPromptTokens());
            // robotMessage.setCompletionTokens(chatMessageAccumulator.getUsage().getCompletionTokens());
            // robotMessage.setTotalTokens(chatMessageAccumulator.getUsage().getTotalTokens());
        }
        String result = JSON.toJSONString(sseModelApiResp);
        log.info("websocket output:" + result);
    }

    public static Flowable<ChatMessageAccumulator> mapStreamToAccumulator(Flowable<ModelData> flowable) {
        return flowable.map(chunk -> {
            return new ChatMessageAccumulator(chunk.getChoices().get(0).getDelta(), null, chunk.getChoices().get(0),
                    chunk.getUsage(), chunk.getCreated(), chunk.getId());
        });
    }

    // private List<String> generateAdditionalQAPairs(String question, String
    // answer) {
    // // 这里可以实现生成更多问答对的逻辑
    // // 例如，可以基于现有的问答对生成相关的问答对
    // List<String> additionalQAPairs = new ArrayList<>();
    // additionalQAPairs.add("Q: " + question + " A: " + answer);
    // additionalQAPairs.add("Q: 相关问题1 A: 相关答案1");
    // additionalQAPairs.add("Q: 相关问题2 A: 相关答案2");
    // return additionalQAPairs;
    // }

}
