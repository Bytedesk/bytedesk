package com.bytedesk.ai.robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.SpringAIService;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.StreamContent;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotPipelineService {

    private final SpringAIServiceRegistry springAIServiceRegistry;
    private final ThreadRestService threadRestService;
    private final MessageService messageService;

    // ====== 对外编排入口 ======
    public void streamChat(String messageJson, SseEmitter emitter) {
        Assert.notNull(emitter, "emitter is null");
        // 解析并校验消息
        Parsed parsed = parseAndValidate(messageJson);

        // 1) 构造查询和机器人
        RobotProtobuf robot = loadRobotByThreadTopic(parsed.threadTopic);
        MessageProtobuf msgQ = parsed.message;
        MessageProtobuf msgR = RobotProtobufAware.createRobotReply(msgQ, robot);

        // 2) 查询（默认不启用重写）
        String query = StringUtils.hasText(parsed.query) ? parsed.query : "";
        String rewritten = query;

        // 3) 预处理/分段（占位）
        // 预处理/分段（占位）
        preprocessAndSegment(rewritten, null);

        // 4) chunk search（复用 BaseSpringAIService 能力，通过具体 Provider 的 Base 实现）
        BaseSpringAIService.SearchResultWithSources sr = chunkSearchWithSources(rewritten, robot);

        // 5) rerank（占位：按 score 排序）
        List<StreamContent.SourceReference> reranked = chunkRerank(sr.getSourceReferences(), null);

        // 6) merge（占位：按来源聚合不变更内容）
        List<StreamContent.SourceReference> merged = chunkMerge(reranked, null);
        // 7) filter topK（占位结果暂不直接传递给 provider，后续可用于 prompt 拼装）
        filterTopK(merged, null);

        // 9) 流式聊天（带来源）
        SpringAIService provider = getProvider(robot);
        if (provider instanceof BaseSpringAIService base) {
            // 直接复用 BaseSpringAIService 的入口：当 kb 启用时会自动走 WithSources 路径
            base.sendSseMessage(rewritten, robot, msgQ, msgR, emitter);
        } else {
            // 回退：直接让 provider 处理（无来源）
            provider.sendSseMessage(rewritten, robot, msgQ, msgR, emitter);
        }
    }

    public MessageProtobuf syncChat(String messageJson) {
        Parsed parsed = parseAndValidate(messageJson);

        RobotProtobuf robot = loadRobotByThreadTopic(parsed.threadTopic);
        MessageProtobuf msgQ = parsed.message;
        MessageProtobuf msgR = RobotProtobufAware.createRobotReply(msgQ, robot);

        String query = StringUtils.hasText(parsed.query) ? parsed.query : "";
        String rewritten = query;

        // 预处理/分段（占位）
        preprocessAndSegment(rewritten, null);

        // 搜索
        BaseSpringAIService.SearchResultWithSources sr = chunkSearchWithSources(rewritten, robot);

        // rerank/merge/topK
        List<StreamContent.SourceReference> reranked = chunkRerank(sr.getSourceReferences(), null);
        List<StreamContent.SourceReference> merged = chunkMerge(reranked, null);
        filterTopK(merged, null);

        // 同步聊天
        SpringAIService provider = getProvider(robot);
        String answer = provider.sendSyncMessage(rewritten, robot, msgQ, msgR);
        msgR.setContent(answer);
        msgR.setType(MessageTypeEnum.TEXT);
        return msgR;
    }

    // ====== 步骤实现（尽量复用，缺失先占位） ======

    // 保留占位：如需启用重写，可在消息中增加标志或从 robot 配置读取
    // private String rewriteQueryIfEnabled(String query, String orgUid) {
    // return query;
    // }

    private List<String> preprocessAndSegment(String content, Object unused) {
        // 占位：简单按标点/换行切分
        if (!StringUtils.hasText(content))
            return List.of();
        String[] parts = content.split("[\n；;。.!?]+");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty())
                list.add(t);
        }
        return list;
    }

    private BaseSpringAIService.SearchResultWithSources chunkSearchWithSources(String query, RobotProtobuf robot) {
        SpringAIService provider = getProvider(robot);
        if (provider instanceof BaseSpringAIService base) {
            // 直接调用受保护方法不可见，这里通过 sendSse/sendSync 的逻辑间接使用
            // 为了提前得到来源，复制一份基础搜索逻辑：
            try {
                var method = BaseSpringAIService.class.getDeclaredMethod(
                        "searchKnowledgeBaseWithSources", String.class, RobotProtobuf.class);
                method.setAccessible(true);
                return (BaseSpringAIService.SearchResultWithSources) method.invoke(base, query, robot);
            } catch (Exception e) {
                log.warn("access searchKnowledgeBaseWithSources failed: {}", e.getMessage());
                return new BaseSpringAIService.SearchResultWithSources(new ArrayList<>(), new ArrayList<>());
            }
        }
        return new BaseSpringAIService.SearchResultWithSources(new ArrayList<>(), new ArrayList<>());
    }

    private List<StreamContent.SourceReference> chunkRerank(List<StreamContent.SourceReference> refs,
            Object unused) {
        if (refs == null)
            return List.of();
        // 占位：按 score 降序
        return refs.stream()
                .sorted(Comparator.comparing(StreamContent.SourceReference::getScore,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    private List<StreamContent.SourceReference> chunkMerge(List<StreamContent.SourceReference> refs,
            Object unused) {
        // 占位：不合并，后续可按 fileUid/sourceType 做聚合
        return refs == null ? List.of() : refs;
    }

    private List<StreamContent.SourceReference> filterTopK(List<StreamContent.SourceReference> refs, Integer topK) {
        if (refs == null || refs.isEmpty())
            return List.of();
        int k = (topK == null || topK <= 0) ? Math.min(refs.size(), 5) : Math.min(refs.size(), topK);
        return refs.subList(0, k);
    }

    // 预留：未来可用于将 TopK 结果拼装进自定义 prompt。当前通过 BaseSpringAIService 统一处理。

    // ====== 工具方法 ======
    private SpringAIService getProvider(RobotProtobuf robot) {
        String provider = robot != null && robot.getLlm() != null && robot.getLlm().getTextProvider() != null
                ? robot.getLlm().getTextProvider().toLowerCase()
                : "zhipuai";
        return springAIServiceRegistry.getServiceByProviderName(provider);
    }

    private RobotProtobuf loadRobotByThreadTopic(String threadTopic) {
        Assert.hasText(threadTopic, "threadTopic is required");
        Optional<com.bytedesk.core.thread.ThreadEntity> threadOptional = threadRestService
                .findFirstByTopic(threadTopic);
        if (threadOptional.isPresent() && StringUtils.hasText(threadOptional.get().getRobot())) {
            RobotProtobuf robot = RobotProtobuf.fromJson(threadOptional.get().getRobot());
            if (robot != null) {
                return robot;
            }
        }
        throw new IllegalArgumentException("robot resolving failed by threadTopic: " + threadTopic);
    }

    private Parsed parseAndValidate(String messageJson) {
        Assert.notNull(messageJson, "message json is null");
        String processed = messageService.processMessageJson(messageJson);
        MessageProtobuf mp = MessageProtobuf.fromJson(processed);
        Assert.notNull(mp, "message parse failed");
        ThreadProtobuf thread = mp.getThread();
        Assert.notNull(thread, "thread is null");
        String topic = thread.getTopic();
        Assert.hasText(topic, "threadTopic is required");
        MessageTypeEnum type = mp.getType();
        if (type != null && !MessageTypeEnum.TEXT.equals(type) && !MessageTypeEnum.ROBOT_QUESTION.equals(type)) {
            throw new IllegalArgumentException("unsupported message type: " + type);
        }
        String query = mp.getContent();
        Assert.hasText(query, "query is required");
        return new Parsed(mp, query, topic);
    }

    private static class Parsed {
        final MessageProtobuf message;
        final String query;
        final String threadTopic;

        Parsed(MessageProtobuf message, String query, String threadTopic) {
            this.message = message;
            this.query = query;
            this.threadTopic = threadTopic;
        }
    }

    private static class RobotProtobufAware {
        static MessageProtobuf createRobotReply(MessageProtobuf queryMsg, RobotProtobuf robot) {
            // 复用工具类，避免直接依赖 PipelineChatRequest
            return com.bytedesk.ai.robot_message.RobotMessageUtils.createRobotMessage(
                    queryMsg.getThread(), robot, queryMsg);
        }
    }
}
