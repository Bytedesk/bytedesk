package com.bytedesk.ai.pipeline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.ai.springai.service.BaseSpringAIService;
import com.bytedesk.ai.springai.service.SpringAIService;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.StreamContent;
import com.bytedesk.core.thread.ThreadRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final RobotService robotService;
    private final RobotRestService robotRestService;
    private final SpringAIServiceRegistry springAIServiceRegistry;
    private final ThreadRestService threadRestService;

    // ====== 对外编排入口 ======
    public void streamChat(PipelineChatRequest req, SseEmitter emitter) {
        Assert.notNull(emitter, "emitter is null");
        validateRequest(req);

        // 1) 构造查询和机器人
        RobotProtobuf robot = loadRobot(req);
        MessageProtobuf msgQ = req.toMessageQuery();
        MessageProtobuf msgR = req.toRobotReply(msgQ, robot);

        // 2) 重写查询
        String query = StringUtils.hasText(req.getQuery()) ? req.getQuery() : "";
        String rewritten = rewriteQueryIfEnabled(query, req, robot.getOrgUid());

        // 3) 预处理/分段（占位）
        // 预处理/分段（占位）
        preprocessAndSegment(rewritten, req);

        // 4) chunk search（复用 BaseSpringAIService 能力，通过具体 Provider 的 Base 实现）
        BaseSpringAIService.SearchResultWithSources sr = chunkSearchWithSources(rewritten, robot);

        // 5) rerank（占位：按 score 排序）
        List<StreamContent.SourceReference> reranked = chunkRerank(sr.getSourceReferences(), req);

        // 6) merge（占位：按来源聚合不变更内容）
        List<StreamContent.SourceReference> merged = chunkMerge(reranked, req);
        // 7) filter topK（占位结果暂不直接传递给 provider，后续可用于 prompt 拼装）
        filterTopK(merged, req.getTopK());

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

    public MessageProtobuf syncChat(PipelineChatRequest req) {
        validateRequest(req);

        RobotProtobuf robot = loadRobot(req);
        MessageProtobuf msgQ = req.toMessageQuery();
        MessageProtobuf msgR = req.toRobotReply(msgQ, robot);

        String query = StringUtils.hasText(req.getQuery()) ? req.getQuery() : "";
        String rewritten = rewriteQueryIfEnabled(query, req, robot.getOrgUid());

        // 预处理/分段（占位）
        // 预处理/分段（占位）
        preprocessAndSegment(rewritten, req);

        // 搜索
        BaseSpringAIService.SearchResultWithSources sr = chunkSearchWithSources(rewritten, robot);

        // rerank/merge/topK
        List<StreamContent.SourceReference> reranked = chunkRerank(sr.getSourceReferences(), req);
        List<StreamContent.SourceReference> merged = chunkMerge(reranked, req);
        filterTopK(merged, req.getTopK());

        // 同步聊天
        SpringAIService provider = getProvider(robot);
        String answer = provider.sendSyncMessage(rewritten, robot, msgQ, msgR);
        msgR.setContent(answer);
        msgR.setType(MessageTypeEnum.TEXT);
        return msgR;
    }

    // ====== 步骤实现（尽量复用，缺失先占位） ======

    private String rewriteQueryIfEnabled(String query, PipelineChatRequest req, String orgUid) {
        if (Boolean.TRUE.equals(req.getEnableRewrite())) {
            try {
                return robotService.queryRewrite(query, orgUid);
            } catch (Exception e) {
                log.warn("query rewrite failed, fallback to original: {}", e.getMessage());
                return query;
            }
        }
        return query;
    }

    private List<String> preprocessAndSegment(String content, PipelineChatRequest req) {
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
            PipelineChatRequest req) {
        if (refs == null)
            return List.of();
        // 占位：按 score 降序
        return refs.stream()
                .sorted(Comparator.comparing(StreamContent.SourceReference::getScore,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    private List<StreamContent.SourceReference> chunkMerge(List<StreamContent.SourceReference> refs,
            PipelineChatRequest req) {
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

    private RobotProtobuf loadRobot(PipelineChatRequest req) {
        // 允许通过 orgUid + robotName 查找，或直接携带 robotUid
        if (StringUtils.hasText(req.getRobotJson())) {
            return RobotProtobuf.fromJson(req.getRobotJson());
        }
        Assert.hasText(req.getThreadTopic(), "threadTopic is required");
        // 复用 RobotService 的线程查询逻辑：间接通过线程获取机器人
        // 这里走简化路径：让 MessageProtobuf 的 threadTopic 由 provider内部使用
        // 如需严格按主题取机器人，可引入 ThreadRestService；这里用 RobotRestService 兜底
        if (StringUtils.hasText(req.getRobotUid())) {
            return robotRestService.findByUid(req.getRobotUid())
                    .map(RobotProtobuf::convertFromRobotEntity)
                    .orElseThrow(() -> new IllegalArgumentException("robot not found: " + req.getRobotUid()));
        }
        // 优先从会话topic解析机器人
        Optional<com.bytedesk.core.thread.ThreadEntity> threadOptional = threadRestService
                .findFirstByTopic(req.getThreadTopic());
        if (threadOptional.isPresent() && StringUtils.hasText(threadOptional.get().getRobot())) {
            RobotProtobuf robot = RobotProtobuf.fromJson(threadOptional.get().getRobot());
            if (robot != null) {
                return robot;
            }
        }
        throw new IllegalArgumentException(
                "robot resolving failed; provide robotJson/robotUid or ensure thread has robot");
    }

    private void validateRequest(PipelineChatRequest req) {
        Assert.notNull(req, "request is null");
        Assert.hasText(req.getQuery(), "query is required");
        Assert.hasText(req.getThreadTopic(), "threadTopic is required");
    }
}
