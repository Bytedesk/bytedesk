/*
 * @Author: Copilot
 * @Description: Workflow chat executor for visitor thread conversations
 */
package com.bytedesk.service.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.ChoiceContent;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadExtra;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.utils.ThreadMessageUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkflowChatService {

    private final ThreadRestService threadRestService;
    private final MessageRestService messageRestService;

    public MessageProtobuf createStartMessage(WorkflowEntity workflow, ThreadEntity thread) {
        List<MessageProtobuf> messages = createWorkflowMessages(workflow, thread, null, false);
        if (messages.isEmpty()) {
            return buildFallbackMessage(workflow, thread);
        }
        return messages.get(0);
    }

    public Optional<MessageProtobuf> continueAfterChoice(WorkflowEntity workflow, ThreadEntity thread, String selectedOptionKey) {
        List<MessageProtobuf> messages = continueAfterChoiceMessages(workflow, thread, selectedOptionKey);
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }

    public List<MessageProtobuf> continueAfterChoiceMessages(WorkflowEntity workflow, ThreadEntity thread, String selectedOptionKey) {
        ThreadExtra extra = getThreadExtra(thread);
        if (!StringUtils.hasText(extra.getWorkflowWaitingChoiceNodeId()) || !StringUtils.hasText(selectedOptionKey)) {
            return new ArrayList<>();
        }

        JSONObject workflowJson = parseWorkflowJson(workflow);
        ChoiceContent.ChoiceOption option = findChoiceOption(
                workflowJson,
                extra.getWorkflowWaitingChoiceNodeId(),
                selectedOptionKey);
        if (option == null) {
            log.debug("Ignore workflow choice reply without matching option, threadUid={}, selectedOptionKey={}",
                    thread.getUid(),
                    selectedOptionKey);
            return new ArrayList<>();
        }

        String nextNodeId = findNextNodeId(workflowJson, extra.getWorkflowWaitingChoiceNodeId());

        ThreadExtra preparedExtra = extra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowWaitingChoiceNodeId(null)
                .workflowWaitingQuestionNodeId(null)
                .workflowQuestionVariable(null)
            .workflowQuestionAnswer(null)
                .workflowSelectedOptionValue(resolveChoiceValue(option))
                .workflowCompleted(false)
                .build();
        thread.setExtra(preparedExtra.toJson());

        return createWorkflowMessages(workflow, thread, nextNodeId, true);
    }

    public List<MessageProtobuf> continueAfterQuestionMessages(WorkflowEntity workflow, ThreadEntity thread, String answerText) {
        ThreadExtra extra = getThreadExtra(thread);
        String normalizedAnswer = StringUtils.hasText(answerText) ? answerText.trim() : null;
        if (!StringUtils.hasText(extra.getWorkflowWaitingQuestionNodeId()) || !StringUtils.hasText(normalizedAnswer)) {
            return new ArrayList<>();
        }

        JSONObject workflowJson = parseWorkflowJson(workflow);
        String nextNodeId = findNextNodeId(workflowJson, extra.getWorkflowWaitingQuestionNodeId());

        ThreadExtra preparedExtra = extra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowWaitingChoiceNodeId(null)
                .workflowWaitingQuestionNodeId(null)
                .workflowQuestionAnswer(normalizedAnswer)
                .workflowCompleted(false)
                .build();
        thread.setExtra(preparedExtra.toJson());

        return createWorkflowMessages(workflow, thread, nextNodeId, true);
    }

    private List<MessageProtobuf> createWorkflowMessages(WorkflowEntity workflow, ThreadEntity thread, String startNodeId,
            boolean allowEmptyOutput) {
        JSONObject workflowJson = parseWorkflowJson(workflow);
        ThreadExtra currentExtra = getThreadExtra(thread);
        ExecutionResult result = executeConversation(workflowJson, startNodeId, currentExtra);

        ThreadExtra nextExtra = currentExtra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowCurrentNodeId(result.getCurrentNodeId())
                .workflowWaitingChoiceNodeId(result.getWaitingChoiceNodeId())
                .workflowWaitingQuestionNodeId(result.getWaitingQuestionNodeId())
                .workflowQuestionVariable(result.getQuestionVariable())
                .workflowCompleted(result.getCompleted())
                .build();
        thread.setExtra(nextExtra.toJson());

        if (result.getMessages().isEmpty()) {
            ThreadEntity savedThread = threadRestService.save(thread);
            if (allowEmptyOutput) {
                return new ArrayList<>();
            }
            List<MessageProtobuf> fallbackMessages = new ArrayList<>();
            fallbackMessages.add(buildFallbackMessage(workflow, savedThread));
            return fallbackMessages;
        }

        WorkflowMessageDraft lastMessage = result.getMessages().get(result.getMessages().size() - 1);
        thread.setContent(ThreadContent.of(lastMessage.getMessageType(), lastMessage.getPreviewText(), lastMessage.getMessagePayload()).toJson());
        ThreadEntity savedThread = threadRestService.save(thread);
        List<MessageProtobuf> messages = new ArrayList<>();
        for (WorkflowMessageDraft draft : result.getMessages()) {
            MessageEntity message = buildWorkflowMessageEntity(draft, savedThread);
            messageRestService.save(message);
            messages.add(ServiceConvertUtils.convertToMessageProtobuf(message, savedThread));
        }
        return messages;
    }

    private MessageEntity buildWorkflowMessageEntity(WorkflowMessageDraft draft, ThreadEntity thread) {
        if (MessageTypeEnum.CHOICE.equals(draft.getMessageType())) {
            return ThreadMessageUtil.getThreadWorkflowChoiceMessage(
                    ChoiceContent.fromJson(draft.getMessagePayload()),
                    thread);
        }
        return ThreadMessageUtil.getThreadWorkflowTextMessage(draft.getMessagePayload(), thread);
    }

    private MessageProtobuf buildFallbackMessage(WorkflowEntity workflow, ThreadEntity thread) {
        String fallback = StringUtils.hasText(workflow.getDescription()) ? workflow.getDescription() : "流程已结束";
        thread.setContent(ThreadContent.of(MessageTypeEnum.TEXT, fallback, fallback).toJson());
        ThreadEntity savedThread = threadRestService.save(thread);
        MessageEntity message = ThreadMessageUtil.getThreadWorkflowTextMessage(fallback, savedThread);
        messageRestService.save(message);
        return ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
    }

    private ExecutionResult executeConversation(JSONObject workflowJson, String startNodeId, ThreadExtra extra) {
        JSONArray nodes = workflowJson.getJSONArray("nodes");
        String nodeId = StringUtils.hasText(startNodeId) ? startNodeId : findStartNodeId(workflowJson);
        int guard = nodes == null ? 0 : Math.max(nodes.size() * 2, 10);
        String currentNodeId = nodeId;
        List<WorkflowMessageDraft> messages = new ArrayList<>();

        while (StringUtils.hasText(currentNodeId) && guard-- > 0) {
            JSONObject node = findNodeById(workflowJson, currentNodeId);
            if (node == null) {
                break;
            }
            String type = node.getString("type");
            if (!StringUtils.hasText(type)) {
                currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                continue;
            }

            switch (type) {
                case "start":
                    currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                    break;
                case "text": {
                    String textContent = resolveNodeContent(node);
                    if (StringUtils.hasText(textContent)) {
                        messages.add(WorkflowMessageDraft.builder()
                                .messageType(MessageTypeEnum.TEXT)
                                .messagePayload(textContent.trim())
                                .previewText(textContent.trim())
                                .build());
                    }
                    currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                    break;
                }
                case "message": {
                    String textContent = resolveNodeContent(node);
                    if (StringUtils.hasText(textContent)) {
                        messages.add(WorkflowMessageDraft.builder()
                                .messageType(MessageTypeEnum.TEXT)
                                .messagePayload(textContent.trim())
                                .previewText(textContent.trim())
                                .build());
                    }
                    currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                    break;
                }
                case "question": {
                    String textContent = resolveNodeContent(node);
                    if (StringUtils.hasText(textContent)) {
                        messages.add(WorkflowMessageDraft.builder()
                                .messageType(MessageTypeEnum.TEXT)
                                .messagePayload(textContent.trim())
                                .previewText(textContent.trim())
                                .build());
                    }
                    return ExecutionResult.builder()
                            .messages(messages)
                            .currentNodeId(node.getString("id"))
                            .waitingChoiceNodeId(null)
                            .waitingQuestionNodeId(node.getString("id"))
                            .questionVariable(resolveQuestionVariable(node))
                            .completed(false)
                            .build();
                }
                case "condition": {
                    String textContent = resolveNodeContent(node);
                    if (StringUtils.hasText(textContent)) {
                        messages.add(WorkflowMessageDraft.builder()
                                .messageType(MessageTypeEnum.TEXT)
                                .messagePayload(textContent.trim())
                                .previewText(textContent.trim())
                                .build());
                    }
                    currentNodeId = resolveConditionNextNodeId(workflowJson, node, extra);
                    break;
                }
                case "choice": {
                    ChoiceContent choiceContent = buildChoiceContent(node, resolveNodeContent(node));
                    if (choiceContent.getOptions() == null || choiceContent.getOptions().isEmpty()) {
                        currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                        break;
                    }
                    messages.add(WorkflowMessageDraft.builder()
                            .messageType(MessageTypeEnum.CHOICE)
                            .messagePayload(choiceContent.toJson())
                            .previewText(choiceContent.getContent())
                            .build());
                    return ExecutionResult.builder()
                            .messages(messages)
                            .currentNodeId(node.getString("id"))
                            .waitingChoiceNodeId(node.getString("id"))
                            .waitingQuestionNodeId(null)
                            .questionVariable(null)
                            .completed(false)
                            .build();
                }
                case "end":
                    return ExecutionResult.builder()
                            .messages(messages)
                            .currentNodeId(node.getString("id"))
                            .waitingChoiceNodeId(null)
                            .waitingQuestionNodeId(null)
                            .questionVariable(null)
                            .completed(true)
                            .build();
                default:
                    log.debug("Skip unsupported workflow chat node type={}, nodeId={}", type, currentNodeId);
                    currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                    break;
            }
        }

        return ExecutionResult.builder()
                .messages(messages)
                .currentNodeId(currentNodeId)
                .waitingChoiceNodeId(null)
                .waitingQuestionNodeId(null)
                .questionVariable(null)
                .completed(!StringUtils.hasText(currentNodeId))
                .build();
    }

    private JSONObject parseWorkflowJson(WorkflowEntity workflow) {
        if (workflow == null || !StringUtils.hasText(workflow.getSchema())) {
            throw new IllegalArgumentException("workflow schema is empty");
        }
        return JSON.parseObject(workflow.getSchema());
    }

    private String findStartNodeId(JSONObject workflowJson) {
        JSONArray nodes = workflowJson.getJSONArray("nodes");
        if (nodes == null) {
            return null;
        }
        for (int index = 0; index < nodes.size(); index++) {
            JSONObject node = nodes.getJSONObject(index);
            if ("start".equals(node.getString("type"))) {
                return node.getString("id");
            }
        }
        return null;
    }

    private JSONObject findNodeById(JSONObject workflowJson, String nodeId) {
        JSONArray nodes = workflowJson.getJSONArray("nodes");
        if (nodes == null || !StringUtils.hasText(nodeId)) {
            return null;
        }
        for (int index = 0; index < nodes.size(); index++) {
            JSONObject node = nodes.getJSONObject(index);
            if (nodeId.equals(node.getString("id"))) {
                return node;
            }
        }
        return null;
    }

    private String findNextNodeId(JSONObject workflowJson, String nodeId) {
        return findNextNodeId(workflowJson, nodeId, null);
    }

    private String findNextNodeId(JSONObject workflowJson, String nodeId, String sourcePortId) {
        JSONArray edges = workflowJson.getJSONArray("edges");
        if (edges == null || !StringUtils.hasText(nodeId)) {
            return null;
        }
        for (int index = 0; index < edges.size(); index++) {
            JSONObject edge = edges.getJSONObject(index);
            if (nodeId.equals(resolveEdgeNodeId(edge, "source"))
                    && (!StringUtils.hasText(sourcePortId) || sourcePortId.equals(resolveEdgePortId(edge, "source")))) {
                return resolveEdgeNodeId(edge, "target");
            }
        }
        return null;
    }

    private String findTargetNodeIdByEdgeId(JSONObject workflowJson, String edgeId) {
        JSONArray edges = workflowJson.getJSONArray("edges");
        if (edges == null || !StringUtils.hasText(edgeId)) {
            return null;
        }
        for (int index = 0; index < edges.size(); index++) {
            JSONObject edge = edges.getJSONObject(index);
            if (edgeId.equals(edge.getString("id"))) {
                return resolveEdgeNodeId(edge, "target");
            }
        }
        return null;
    }

    private String resolveEdgeNodeId(JSONObject edge, String prefix) {
        if (edge == null || !StringUtils.hasText(prefix)) {
            return null;
        }

        String upperIdKey = prefix + "NodeID";
        if (StringUtils.hasText(edge.getString(upperIdKey))) {
            return edge.getString(upperIdKey);
        }

        String lowerIdKey = prefix + "NodeId";
        if (StringUtils.hasText(edge.getString(lowerIdKey))) {
            return edge.getString(lowerIdKey);
        }

        return edge.getString(prefix);
    }

    private String resolveEdgePortId(JSONObject edge, String prefix) {
        if (edge == null || !StringUtils.hasText(prefix)) {
            return null;
        }

        String upperIdKey = prefix + "PortID";
        if (StringUtils.hasText(edge.getString(upperIdKey))) {
            return edge.getString(upperIdKey);
        }

        String lowerIdKey = prefix + "PortId";
        if (StringUtils.hasText(edge.getString(lowerIdKey))) {
            return edge.getString(lowerIdKey);
        }

        return null;
    }

    private String resolveNodeContent(JSONObject node) {
        if (node == null) {
            return null;
        }
        if (StringUtils.hasText(node.getString("text"))) {
            return node.getString("text");
        }

        JSONObject data = node.getJSONObject("data");
        if (data != null) {
            if (StringUtils.hasText(data.getString("content"))) {
                return data.getString("content");
            }
            if (StringUtils.hasText(data.getString("title"))) {
                return data.getString("title");
            }
            if (StringUtils.hasText(data.getString("description"))) {
                return data.getString("description");
            }
            JSONObject inputsValues = data.getJSONObject("inputsValues");
            if (inputsValues != null && StringUtils.hasText(inputsValues.getString("text"))) {
                return inputsValues.getString("text");
            }
            JSONObject inputs = data.getJSONObject("inputs");
            if (inputs != null && StringUtils.hasText(inputs.getString("text"))) {
                return inputs.getString("text");
            }
        }

        if (StringUtils.hasText(node.getString("description"))) {
            return node.getString("description");
        }
        return node.getString("name");
    }

    private ChoiceContent buildChoiceContent(JSONObject node, String promptText) {
        List<ChoiceContent.ChoiceOption> choiceOptions = new ArrayList<>();
        JSONArray options = getChoiceOptions(node);
        String nodeId = node.getString("id");

        for (int index = 0; index < options.size(); index++) {
            JSONObject option = options.getJSONObject(index);
            if (option == null) {
                continue;
            }
            String label = option.getString("label");
            String value = StringUtils.hasText(option.getString("value")) ? option.getString("value") : label;
            if (!StringUtils.hasText(label)) {
                continue;
            }
            choiceOptions.add(ChoiceContent.ChoiceOption.builder()
                    .optionUid(nodeId + "_" + index)
                    .title(label)
                    .value(value)
                    .build());
        }

        return ChoiceContent.builder()
                .choiceUid(nodeId)
                .content(promptText)
                .multiple(false)
                .options(choiceOptions)
                .build();
    }

    private JSONArray getChoiceOptions(JSONObject node) {
        JSONArray options = node.getJSONArray("options");
        if (options != null) {
            return options;
        }
        JSONObject data = node.getJSONObject("data");
        if (data != null) {
            JSONArray dataOptions = data.getJSONArray("options");
            if (dataOptions != null) {
                return dataOptions;
            }
        }
        return new JSONArray();
    }

    private String resolveQuestionVariable(JSONObject node) {
        if (node == null) {
            return null;
        }
        JSONObject data = node.getJSONObject("data");
        if (data == null) {
            return null;
        }
        String variable = data.getString("variable");
        return StringUtils.hasText(variable) ? variable.trim() : null;
    }

    private String resolveConditionNextNodeId(JSONObject workflowJson, JSONObject node, ThreadExtra extra) {
        JSONObject matchedCondition = findMatchedCondition(node, extra);
        if (matchedCondition != null) {
            String outgoingEdgeId = matchedCondition.getString("outgoingEdgeId");
            if (StringUtils.hasText(outgoingEdgeId)) {
                String nextNodeId = findTargetNodeIdByEdgeId(workflowJson, outgoingEdgeId);
                if (StringUtils.hasText(nextNodeId)) {
                    return nextNodeId;
                }
            }
            String conditionKey = matchedCondition.getString("key");
            if (StringUtils.hasText(conditionKey)) {
                String nextNodeId = findNextNodeId(workflowJson, node.getString("id"), conditionKey);
                if (StringUtils.hasText(nextNodeId)) {
                    return nextNodeId;
                }
            }
        }
        return findNextNodeId(workflowJson, node.getString("id"));
    }

    private JSONObject findMatchedCondition(JSONObject node, ThreadExtra extra) {
        JSONArray conditions = getConditionItems(node);
        if (conditions == null || conditions.isEmpty()) {
            return null;
        }
        String signal = resolveConditionSignal(extra);
        if (!StringUtils.hasText(signal)) {
            return null;
        }
        for (int index = 0; index < conditions.size(); index++) {
            JSONObject condition = conditions.getJSONObject(index);
            if (condition == null) {
                continue;
            }
            if (matchesConditionSignal(signal, condition.getString("key"))
                    || matchesConditionSignal(signal, condition.getString("label"))
                    || matchesConditionSignal(signal, resolveConditionExpression(condition))) {
                return condition;
            }
        }
        return null;
    }

    private JSONArray getConditionItems(JSONObject node) {
        if (node == null) {
            return null;
        }
        JSONObject data = node.getJSONObject("data");
        return data != null ? data.getJSONArray("conditions") : null;
    }

    private String resolveConditionSignal(ThreadExtra extra) {
        if (extra == null) {
            return null;
        }
        if (StringUtils.hasText(extra.getWorkflowQuestionAnswer())) {
            return extra.getWorkflowQuestionAnswer().trim();
        }
        if (StringUtils.hasText(extra.getWorkflowSelectedOptionValue())) {
            return extra.getWorkflowSelectedOptionValue().trim();
        }
        return null;
    }

    private boolean matchesConditionSignal(String signal, String candidate) {
        return StringUtils.hasText(signal) && StringUtils.hasText(candidate)
                && signal.trim().equalsIgnoreCase(candidate.trim());
    }

    private String resolveConditionExpression(JSONObject condition) {
        if (condition == null) {
            return null;
        }
        JSONObject value = condition.getJSONObject("value");
        if (value != null && StringUtils.hasText(value.getString("content"))) {
            return value.getString("content");
        }
        return null;
    }

    private ThreadExtra getThreadExtra(ThreadEntity thread) {
        ThreadExtra extra = ThreadExtra.fromJson(thread.getExtra());
        return extra != null ? extra : ThreadExtra.builder().build();
    }

    private ChoiceContent.ChoiceOption findChoiceOption(JSONObject workflowJson, String nodeId, String selectedOptionKey) {
        JSONObject node = findNodeById(workflowJson, nodeId);
        if (node == null) {
            return null;
        }
        ChoiceContent choiceContent = buildChoiceContent(node, resolveNodeContent(node));
        if (choiceContent.getOptions() == null) {
            return null;
        }
        for (ChoiceContent.ChoiceOption option : choiceContent.getOptions()) {
            if (option == null) {
                continue;
            }
            if (selectedOptionKey.equals(option.getOptionUid())
                    || selectedOptionKey.equals(option.getValue())
                    || selectedOptionKey.equals(option.getTitle())) {
                return option;
            }
        }
        return null;
    }

    private String resolveChoiceValue(ChoiceContent.ChoiceOption option) {
        if (option == null) {
            return null;
        }
        if (StringUtils.hasText(option.getValue())) {
            return option.getValue();
        }
        return option.getTitle();
    }

    @Data
    @Builder
    private static class ExecutionResult {

        @Builder.Default
        private List<WorkflowMessageDraft> messages = new ArrayList<>();

        private String currentNodeId;

        private String waitingChoiceNodeId;

        private String waitingQuestionNodeId;

        private String questionVariable;

        @Builder.Default
        private Boolean completed = false;
    }

    @Data
    @Builder
    private static class WorkflowMessageDraft {

        private MessageTypeEnum messageType;

        private String messagePayload;

        private String previewText;
    }
}