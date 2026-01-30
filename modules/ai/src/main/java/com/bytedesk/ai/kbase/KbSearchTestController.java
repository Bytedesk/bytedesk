/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-29
 * @Description: Knowledge base test search API for admin chat simulation.
 */
package com.bytedesk.ai.kbase;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotSearchTypeEnum;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.service.KnowledgeBaseSearchHelper;
import com.bytedesk.ai.service.SearchResultWithSources;
import com.bytedesk.core.utils.JsonResult;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai/kbase")
@RequiredArgsConstructor
public class KbSearchTestController {

    private final KnowledgeBaseSearchHelper knowledgeBaseSearchHelper;

    private final RobotRestService robotRestService;

    /**
     * 管理后台用于“对话测试”场景的知识库搜索调试接口。
     * - 复用 BaseSpringAIService 中问答搜索同一条链路：KnowledgeBaseSearchHelper#searchKnowledgeBaseWithSources
     * - 支持传 robotUid（优先使用机器人配置），或仅传 kbUid（使用默认 RobotLlm 配置）
     */
    @PostMapping("/testSearch")
    @PreAuthorize("hasAuthority('FAQ_READ')")
    public ResponseEntity<?> testSearch(@RequestBody KbTestSearchRequest request) {

        if (request == null || !StringUtils.hasText(request.getQuery())) {
            return ResponseEntity.ok(JsonResult.error("query is required"));
        }

        RobotProtobuf robot = resolveRobot(request);
        if (robot == null) {
            return ResponseEntity.ok(JsonResult.error("robotUid or kbUid is required"));
        }

        try {
                SearchResultWithSources results = knowledgeBaseSearchHelper.searchKnowledgeBaseWithSources(
                    request.getQuery(),
                    robot,
                    request.getSourceType());
            return ResponseEntity.ok(JsonResult.success(results));
        } catch (Exception ex) {
            log.error("kb testSearch failed, query={}, robotUid={}, kbUid={}, err={}",
                    request.getQuery(), request.getRobotUid(), request.getKbUid(), ex.getMessage(), ex);
            return ResponseEntity.ok(JsonResult.error(ex.getMessage()));
        }
    }

    private RobotProtobuf resolveRobot(KbTestSearchRequest request) {
        RobotProtobuf robot = null;

        // 1) 优先使用 robotUid
        if (StringUtils.hasText(request.getRobotUid())) {
            Optional<RobotEntity> robotOpt = robotRestService.findByUid(request.getRobotUid());
            if (robotOpt.isPresent()) {
                robot = RobotProtobuf.fromEntity(robotOpt.get());
            }
        }

        // 2) 否则使用 kbUid 构造一个默认 RobotProtobuf
        if (robot == null && StringUtils.hasText(request.getKbUid())) {
            robot = RobotProtobuf.builder()
                    .uid("kb_test")
                    .kbEnabled(true)
                    .kbSourceEnabled(true)
                    .kbUid(request.getKbUid())
                // 调试接口默认不过滤 scoreThreshold/topP，避免短 query（如“微语”）在向量相似度较低时直接被过滤为 0
                .llm(RobotLlm.builder()
                    .scoreThreshold(null)
                    .topP(null)
                    .build())
                    .build();
        }

        if (robot == null) {
            return null;
        }

        // 确保知识库启用，并可覆盖 kbUid（用于调试）
        robot.setKbEnabled(true);
        if (StringUtils.hasText(request.getKbUid())) {
            robot.setKbUid(request.getKbUid());
        }

        // 确保 llm 不为空（KnowledgeBaseSearchHelper 依赖 robot.getLlm()）
        if (robot.getLlm() == null) {
            robot.setLlm(RobotLlm.builder().build());
        }

        // 允许调试时覆盖部分检索参数
        RobotLlm llm = robot.getLlm();
        if (StringUtils.hasText(request.getSearchType())) {
            String raw = request.getSearchType().trim();
            String normalized = raw.toUpperCase();
            try {
                llm.setSearchType(RobotSearchTypeEnum.valueOf(normalized).name());
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid searchType={}, fallback to default FULLTEXT", raw);
                llm.setSearchType(RobotSearchTypeEnum.FULLTEXT.name());
            }
        }
        if (request.getTopK() != null) {
            llm.setTopK(request.getTopK());
        }
        if (request.getScoreThreshold() != null) {
            llm.setScoreThreshold(request.getScoreThreshold());
        }
        if (request.getTopP() != null) {
            llm.setTopP(request.getTopP());
        }
        robot.setLlm(llm);

        return robot;
    }

    @Data
    public static class KbTestSearchRequest {
        /** 搜索 query */
        private String query;

        /** 可选：优先使用机器人配置（searchType/topK/scoreThreshold等） */
        private String robotUid;

        /** 可选：指定知识库 uid（未传 robotUid 时必填；传 robotUid 时也可用于覆盖） */
        private String kbUid;

        /** 可选：覆盖 RobotLlm.searchType */
        private String searchType;

        /** 可选：过滤数据源类型（ALL/FAQ/TEXT/CHUNK/WEBPAGE） */
        private String sourceType;

        /** 可选：覆盖 RobotLlm.topK */
        private Integer topK;

        /** 可选：覆盖 RobotLlm.scoreThreshold */
        private Double scoreThreshold;

        /** 可选：覆盖 RobotLlm.topP */
        private Double topP;
    }
}
