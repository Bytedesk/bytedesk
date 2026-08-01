/*
 * @Author: Copilot
 * @Description: Workflow chat executor for visitor thread conversations
 */
package com.bytedesk.service.workflow;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.ChoiceContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadExtra;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.workflow_variable.WorkflowVariableScopeEnum;
import com.bytedesk.core.workflow_variable.WorkflowVariableService;
import com.bytedesk.core.workflow_variable.WorkflowVariableTypeEnum;
import com.bytedesk.core.workflow_log.WorkflowLogEntity;
import com.bytedesk.core.workflow_log.WorkflowLogRepository;
import com.bytedesk.core.workflow_log.WorkflowLogTypeEnum;
import com.bytedesk.core.workflow.node.WorkflowNodeStatusEnum;
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

    private static final String CHOICE_PORT_PREFIX = "choice-option-";
    private static final Pattern CONTEXT_TOKEN_PATTERN = Pattern.compile("\\{\\{\\s*([^{}]+?)\\s*\\}\\}");

    private final ThreadRestService threadRestService;
    private final MessageRestService messageRestService;
    private final RestTemplate restTemplate;
    private final WorkflowVariableService workflowVariableService;
    private final WorkflowLogRepository workflowLogRepository;

    public MessageProtobuf createStartMessage(WorkflowEntity workflow, ThreadEntity thread) {
        List<MessageProtobuf> messages = createWorkflowMessages(workflow, thread, null, false);
        if (messages.isEmpty()) {
            return buildFallbackMessage(workflow, thread);
        }
        return messages.get(0);
    }

    public Optional<MessageProtobuf> continueAfterChoice(WorkflowEntity workflow, ThreadEntity thread,
            String selectedOptionKey) {
        List<MessageProtobuf> messages = continueAfterChoiceMessages(workflow, thread, selectedOptionKey);
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }

    public List<MessageProtobuf> continueAfterChoiceMessages(WorkflowEntity workflow, ThreadEntity thread,
            String selectedOptionKey) {
        ThreadExtra extra = getThreadExtra(thread);
        if (!StringUtils.hasText(extra.getWorkflowWaitingChoiceNodeId()) || !StringUtils.hasText(selectedOptionKey)) {
            return new ArrayList<>();
        }

        JSONObject workflowJson = parseWorkflowJson(workflow);
        JSONObject option = findChoiceOptionData(
                workflowJson,
                extra.getWorkflowWaitingChoiceNodeId(),
                selectedOptionKey);
        if (option == null) {
            log.debug("Ignore workflow choice reply without matching option, threadUid={}, selectedOptionKey={}",
                    thread.getUid(),
                    selectedOptionKey);
            return new ArrayList<>();
        }

        JSONObject choiceNode = findNodeById(workflowJson, extra.getWorkflowWaitingChoiceNodeId());
        String choiceValue = resolveChoiceValue(option, selectedOptionKey);
        persistChoiceContextVariable(workflow, choiceNode, option, selectedOptionKey, choiceValue);
        persistWorkflowInteractionLog(workflow, thread, choiceNode, "choice", selectedOptionKey, choiceValue, option);

        String nextNodeId = resolveChoiceNextNodeId(
                workflowJson,
                extra.getWorkflowWaitingChoiceNodeId(),
                option,
                selectedOptionKey);

        ThreadExtra preparedExtra = extra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowWaitingChoiceNodeId(null)
                .workflowWaitingQuestionNodeId(null)
                .workflowWaitingFormNodeId(null)
                .workflowQuestionVariable(null)
                .workflowQuestionAnswer(null)
                .workflowFormResponseData(null)
                .workflowSelectedOptionValue(choiceValue)
                .workflowCompleted(false)
                .build();
        thread.setExtra(preparedExtra.toJson());

        return createWorkflowMessages(workflow, thread, nextNodeId, true);
    }

    public List<MessageProtobuf> continueAfterQuestionMessages(WorkflowEntity workflow, ThreadEntity thread,
            String answerText) {
        ThreadExtra extra = getThreadExtra(thread);
        String normalizedAnswer = StringUtils.hasText(answerText) ? answerText.trim() : null;
        if (!StringUtils.hasText(extra.getWorkflowWaitingQuestionNodeId()) || !StringUtils.hasText(normalizedAnswer)) {
            return new ArrayList<>();
        }

        JSONObject workflowJson = parseWorkflowJson(workflow);
        JSONObject questionNode = findNodeById(workflowJson, extra.getWorkflowWaitingQuestionNodeId());
        String nextNodeId = findNextNodeId(workflowJson, extra.getWorkflowWaitingQuestionNodeId());

        persistWorkflowInteractionLog(workflow, thread, questionNode, "question", normalizedAnswer,
            normalizedAnswer, null);

        ThreadExtra preparedExtra = extra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowWaitingChoiceNodeId(null)
                .workflowWaitingQuestionNodeId(null)
                .workflowWaitingFormNodeId(null)
                .workflowQuestionAnswer(normalizedAnswer)
                .workflowFormResponseData(null)
                .workflowCompleted(false)
                .build();
        thread.setExtra(preparedExtra.toJson());

        return createWorkflowMessages(workflow, thread, nextNodeId, true);
    }

    public List<MessageProtobuf> continueAfterFormMessages(WorkflowEntity workflow, ThreadEntity thread,
            String formResponseData) {
        ThreadExtra extra = getThreadExtra(thread);
        if (!StringUtils.hasText(extra.getWorkflowWaitingFormNodeId())) {
            return new ArrayList<>();
        }

        JSONObject workflowJson = parseWorkflowJson(workflow);
        JSONObject formNode = findNodeById(workflowJson, extra.getWorkflowWaitingFormNodeId());
        String nextNodeId = findNextNodeId(workflowJson, extra.getWorkflowWaitingFormNodeId());
        String normalizedFormData = StringUtils.hasText(formResponseData) ? formResponseData.trim() : null;

        persistWorkflowInteractionLog(workflow, thread, formNode, "form", normalizedFormData,
            normalizedFormData, null);

        ThreadExtra preparedExtra = extra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowWaitingChoiceNodeId(null)
                .workflowWaitingQuestionNodeId(null)
                .workflowWaitingFormNodeId(null)
                .workflowQuestionVariable(null)
                .workflowQuestionAnswer(null)
                .workflowFormResponseData(normalizedFormData)
                .workflowCompleted(false)
                .build();
        thread.setExtra(preparedExtra.toJson());

        return createWorkflowMessages(workflow, thread, nextNodeId, true);
    }

    private List<MessageProtobuf> createWorkflowMessages(WorkflowEntity workflow, ThreadEntity thread,
            String startNodeId,
            boolean allowEmptyOutput) {
        JSONObject workflowJson = parseWorkflowJson(workflow);
        ThreadExtra currentExtra = getThreadExtra(thread);
        ExecutionResult result = executeConversation(workflowJson, startNodeId, workflow, thread, currentExtra);

        ThreadExtra nextExtra = currentExtra.toBuilder()
                .showQuickButtons(false)
                .quickButtons(new ArrayList<>())
                .workflowCurrentNodeId(result.getCurrentNodeId())
                .workflowWaitingChoiceNodeId(result.getWaitingChoiceNodeId())
                .workflowWaitingQuestionNodeId(result.getWaitingQuestionNodeId())
                .workflowWaitingFormNodeId(result.getWaitingFormNodeId())
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
        thread.setContent(ThreadContent
                .of(lastMessage.getMessageType(), lastMessage.getPreviewText(), lastMessage.getMessagePayload())
                .toJson());
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
        if (MessageTypeEnum.FORM.equals(draft.getMessageType())) {
            return ThreadMessageUtil.getThreadWorkflowFormMessage(draft.getMessagePayload(), thread);
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

    private ExecutionResult executeConversation(JSONObject workflowJson, String startNodeId, WorkflowEntity workflow,
            ThreadEntity thread, ThreadExtra extra) {
        JSONArray nodes = workflowJson.getJSONArray("nodes");
        String nodeId = StringUtils.hasText(startNodeId) ? startNodeId : findStartNodeId(workflowJson);
        int guard = nodes == null ? 0 : Math.max(nodes.size() * 2, 10);
        String currentNodeId = nodeId;
        List<WorkflowMessageDraft> messages = new ArrayList<>();
        Map<String, Object> contextVariables = buildContextVariables(workflow, thread, extra);

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
                    String textContent = renderTemplate(resolveNodeContent(node), contextVariables);
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
                    String textContent = renderTemplate(resolveNodeContent(node), contextVariables);
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
                    String textContent = renderTemplate(resolveNodeContent(node), contextVariables);
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
                            .waitingFormNodeId(null)
                            .questionVariable(resolveQuestionVariable(node))
                            .completed(false)
                            .build();
                }
                case "form": {
                    JSONObject formPayload = buildFormContent(node, contextVariables);
                    if (formPayload == null) {
                        currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                        break;
                    }
                    messages.add(WorkflowMessageDraft.builder()
                            .messageType(MessageTypeEnum.FORM)
                            .messagePayload(formPayload.toJSONString())
                            .previewText(resolveFormPreviewText(formPayload))
                            .build());
                    return ExecutionResult.builder()
                            .messages(messages)
                            .currentNodeId(node.getString("id"))
                            .waitingChoiceNodeId(null)
                            .waitingQuestionNodeId(null)
                            .waitingFormNodeId(node.getString("id"))
                            .questionVariable(null)
                            .completed(false)
                            .build();
                }
                case "condition": {
                    String textContent = renderTemplate(resolveNodeContent(node), contextVariables);
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
                    ChoiceContent choiceContent = buildChoiceContent(node,
                            renderTemplate(resolveNodeContent(node), contextVariables),
                            contextVariables);
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
                            .waitingFormNodeId(null)
                            .questionVariable(null)
                            .completed(false)
                            .build();
                }
                case "http": {
                    executeHttpNode(workflow, thread, node, contextVariables);
                    currentNodeId = findNextNodeId(workflowJson, currentNodeId);
                    break;
                }
                case "end":
                    return ExecutionResult.builder()
                            .messages(messages)
                            .currentNodeId(node.getString("id"))
                            .waitingChoiceNodeId(null)
                            .waitingQuestionNodeId(null)
                            .waitingFormNodeId(null)
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
                .waitingFormNodeId(null)
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

    private ChoiceContent buildChoiceContent(JSONObject node, String promptText, Map<String, Object> contextVariables) {
        List<ChoiceContent.ChoiceOption> choiceOptions = new ArrayList<>();
        JSONArray options = getChoiceOptions(node);
        String nodeId = node.getString("id");

        for (int index = 0; index < options.size(); index++) {
            JSONObject option = options.getJSONObject(index);
            if (option == null) {
                continue;
            }
            String label = renderTemplate(option.getString("label"), contextVariables);
            String value = StringUtils.hasText(option.getString("value"))
                    ? renderTemplate(option.getString("value"), contextVariables)
                    : label;
            if (!StringUtils.hasText(label)) {
                continue;
            }
            choiceOptions.add(ChoiceContent.ChoiceOption.builder()
                    .optionUid(resolveChoiceOptionUid(nodeId, option, index))
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

    private void persistChoiceContextVariable(WorkflowEntity workflow, JSONObject node, JSONObject option,
            String selectedOptionKey, String choiceValue) {
        if (workflow == null || !StringUtils.hasText(workflow.getUid()) || node == null) {
            return;
        }

        String variable = resolveQuestionVariable(node);
        if (!StringUtils.hasText(variable)) {
            return;
        }

        workflowVariableService.setVariable(
                workflow.getUid(),
                variable,
                choiceValue,
                inferWorkflowVariableType(choiceValue),
                WorkflowVariableScopeEnum.GLOBAL);
    }

    private void persistWorkflowInteractionLog(WorkflowEntity workflow, ThreadEntity thread, JSONObject node,
            String nodeType, String inputValue, Object outputValue, JSONObject option) {
        if (workflow == null || thread == null || node == null) {
            return;
        }

        try {
            JSONObject inputPayload = new JSONObject();
            inputPayload.put("threadUid", thread.getUid());
            inputPayload.put("workflowUid", workflow.getUid());
            inputPayload.put("nodeUid", node.getString("id"));
            inputPayload.put("nodeType", nodeType);
            inputPayload.put("input", inputValue);

            JSONObject outputPayload = new JSONObject();
            outputPayload.put("value", outputValue);
            if (option != null) {
                outputPayload.put("option", option);
            }

            WorkflowLogEntity logEntity = WorkflowLogEntity.builder()
                    .uid(buildWorkflowInteractionLogUid(thread, node, nodeType))
                    .name(resolveWorkflowLogName(workflow))
                    .description(resolveWorkflowLogDescription(node, nodeType, outputValue))
                    .type(WorkflowLogTypeEnum.WORKFLOW.name())
                    .workflowUid(workflow.getUid())
                    .executionUid(thread.getUid())
                    .sequence(resolveWorkflowLogSequence())
                    .nodeUid(node.getString("id"))
                    .nodeName(resolveWorkflowNodeName(node))
                    .nodeType(nodeType)
                    .nodeStatus(WorkflowNodeStatusEnum.SUCCESS.name())
                    .durationMs(0L)
                    .inputPayload(inputPayload.toJSONString())
                    .outputPayload(outputPayload.toJSONString())
                    .orgUid(thread.getOrgUid())
                    .userUid(resolveThreadUserUid(thread))
                    .build();
            workflowLogRepository.save(logEntity);
        } catch (Exception exception) {
            log.warn("Persist workflow interaction log failed, workflowUid={}, threadUid={}, nodeId={}, error={}",
                    workflow.getUid(),
                    thread.getUid(),
                    node.getString("id"),
                    exception.getMessage());
        }
    }

    private String buildWorkflowInteractionLogUid(ThreadEntity thread, JSONObject node, String nodeType) {
        return "wf-log-" + thread.getUid() + "-" + node.getString("id") + "-" + nodeType + "-"
                + System.currentTimeMillis();
    }

    private Integer resolveWorkflowLogSequence() {
        return (int) Math.min(Integer.MAX_VALUE, System.currentTimeMillis() / 1000);
    }

    private String resolveWorkflowLogName(WorkflowEntity workflow) {
        if (workflow != null && StringUtils.hasText(workflow.getNickname())) {
            return workflow.getNickname();
        }
        return "Workflow Chat";
    }

    private String resolveWorkflowLogDescription(JSONObject node, String nodeType, Object outputValue) {
        String nodeName = resolveWorkflowNodeName(node);
        String value = outputValue == null ? "" : String.valueOf(outputValue);
        return nodeName + " " + nodeType + " result" + (StringUtils.hasText(value) ? ": " + value : "");
    }

    private String resolveWorkflowNodeName(JSONObject node) {
        if (node == null) {
            return "Workflow Node";
        }
        if (StringUtils.hasText(node.getString("name"))) {
            return node.getString("name");
        }
        JSONObject data = node.getJSONObject("data");
        if (data != null && StringUtils.hasText(data.getString("title"))) {
            return data.getString("title");
        }
        if (StringUtils.hasText(node.getString("id"))) {
            return node.getString("id");
        }
        return "Workflow Node";
    }

    private String resolveThreadUserUid(ThreadEntity thread) {
        try {
            if (thread.getUserProtobuf() != null && StringUtils.hasText(thread.getUserProtobuf().getUid())) {
                return thread.getUserProtobuf().getUid();
            }
        } catch (Exception exception) {
            log.debug("Resolve workflow log user uid failed, threadUid={}, error={}",
                    thread.getUid(), exception.getMessage());
        }
        return thread.getUserUid();
    }

    private JSONObject buildFormContent(JSONObject node, Map<String, Object> contextVariables) {
        if (node == null) {
            return null;
        }
        JSONObject data = node.getJSONObject("data");
        if (data == null) {
            return null;
        }

        JSONArray formFields = data.getJSONArray("formFields");
        if (formFields == null || formFields.isEmpty()) {
            return null;
        }

        String nodeId = node.getString("id");
        String title = StringUtils.hasText(data.getString("title"))
                ? renderTemplate(data.getString("title"), contextVariables)
                : "表单节点";
        String description = renderTemplate(resolveFormDescription(node, data), contextVariables);

        JSONObject formContent = new JSONObject();
        formContent.put("uid", nodeId);
        formContent.put("formUid", nodeId);
        formContent.put("name", title);
        formContent.put("description", description);
        formContent.put("schema", JSON.toJSONString(formFields));
        formContent.put("formVersion", 1);
        formContent.put("workflowNodeId", nodeId);
        return formContent;
    }

    private String resolveFormDescription(JSONObject node, JSONObject data) {
        if (data != null && StringUtils.hasText(data.getString("content"))) {
            return data.getString("content").trim();
        }
        if (data != null && StringUtils.hasText(data.getString("description"))) {
            return data.getString("description").trim();
        }
        String content = resolveNodeContent(node);
        return StringUtils.hasText(content) ? content.trim() : null;
    }

    private String resolveFormPreviewText(JSONObject formPayload) {
        if (formPayload == null) {
            return "表单节点";
        }
        if (StringUtils.hasText(formPayload.getString("description"))) {
            return formPayload.getString("description").trim();
        }
        if (StringUtils.hasText(formPayload.getString("name"))) {
            return formPayload.getString("name").trim();
        }
        return "表单节点";
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

    private JSONObject findChoiceOptionData(JSONObject workflowJson, String nodeId,
            String selectedOptionKey) {
        JSONObject node = findNodeById(workflowJson, nodeId);
        if (node == null) {
            return null;
        }
        JSONArray options = getChoiceOptions(node);
        if (options == null || options.isEmpty()) {
            return null;
        }
        for (int index = 0; index < options.size(); index++) {
            JSONObject option = options.getJSONObject(index);
            if (option == null) {
                continue;
            }
            if (selectedOptionKey.equals(resolveChoiceOptionUid(nodeId, option, index))
                    || selectedOptionKey.equals(option.getString("id"))
                    || selectedOptionKey.equals(option.getString("value"))
                    || selectedOptionKey.equals(option.getString("label"))) {
                return option;
            }
        }
        return null;
    }

    private String resolveChoiceNextNodeId(JSONObject workflowJson, String nodeId, JSONObject option,
            String selectedOptionKey) {
        if (option != null) {
            String outgoingEdgeId = option.getString("outgoingEdgeId");
            if (StringUtils.hasText(outgoingEdgeId)) {
                String nextNodeId = findTargetNodeIdByEdgeId(workflowJson, outgoingEdgeId);
                if (StringUtils.hasText(nextNodeId)) {
                    return nextNodeId;
                }
            }

            String optionId = option.getString("id");
            if (StringUtils.hasText(optionId)) {
                String nextNodeId = findNextNodeId(workflowJson, nodeId, CHOICE_PORT_PREFIX + optionId);
                if (StringUtils.hasText(nextNodeId)) {
                    return nextNodeId;
                }
                nextNodeId = findNextNodeId(workflowJson, nodeId, optionId);
                if (StringUtils.hasText(nextNodeId)) {
                    return nextNodeId;
                }
            }
        }

        if (StringUtils.hasText(selectedOptionKey)) {
            String nextNodeId = findNextNodeId(workflowJson, nodeId, selectedOptionKey);
            if (StringUtils.hasText(nextNodeId)) {
                return nextNodeId;
            }
        }

        return findNextNodeId(workflowJson, nodeId);
    }

    private String resolveChoiceOptionUid(String nodeId, JSONObject option, int index) {
        if (option != null && StringUtils.hasText(option.getString("id"))) {
            return option.getString("id");
        }
        return nodeId + "_" + index;
    }

    private String resolveChoiceValue(JSONObject option, String fallback) {
        if (option == null) {
            return StringUtils.hasText(fallback) ? fallback.trim() : null;
        }
        if (StringUtils.hasText(option.getString("value"))) {
            return option.getString("value");
        }
        if (StringUtils.hasText(option.getString("label"))) {
            return option.getString("label");
        }
        if (StringUtils.hasText(option.getString("id"))) {
            return option.getString("id");
        }
        return StringUtils.hasText(fallback) ? fallback.trim() : null;
    }

    private void executeHttpNode(WorkflowEntity workflow, ThreadEntity thread, JSONObject node,
            Map<String, Object> contextVariables) {
        if (workflow == null || node == null) {
            return;
        }

        JSONObject data = node.getJSONObject("data");
        if (data == null) {
            return;
        }

        String renderedUrl = renderTemplate(data.getString("url"), contextVariables);
        if (!StringUtils.hasText(renderedUrl)) {
            log.debug("Skip workflow http node without url, workflowUid={}, nodeId={}", workflow.getUid(), node.getString("id"));
            return;
        }

        HttpMethod httpMethod = resolveHttpMethod(data.getString("method"));
        URI requestUri = buildHttpUri(renderedUrl, data.getJSONArray("queryParams"), contextVariables);
        HttpHeaders headers = buildHttpHeaders(data.getJSONArray("headers"), contextVariables);
        String body = renderTemplate(data.getString("body"), contextVariables);
        HttpEntity<String> requestEntity = buildHttpEntity(httpMethod, headers, body);

        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUri, httpMethod, requestEntity, String.class);
            Object parsedResponse = parseHttpResponseBody(response.getBody(), data.getString("responseType"));
            persistHttpContextVariables(workflow, node, response, parsedResponse, contextVariables);
            log.debug("Executed workflow http node, workflowUid={}, threadUid={}, nodeId={}, statusCode={}",
                    workflow.getUid(),
                    thread != null ? thread.getUid() : null,
                    node.getString("id"),
                    response.getStatusCode().value());
        } catch (Exception exception) {
            log.warn("Execute workflow http node failed, workflowUid={}, threadUid={}, nodeId={}, error={}",
                    workflow.getUid(),
                    thread != null ? thread.getUid() : null,
                    node.getString("id"),
                    exception.getMessage());
        }
    }

    private HttpMethod resolveHttpMethod(String method) {
        if (!StringUtils.hasText(method)) {
            return HttpMethod.GET;
        }
        try {
            return HttpMethod.valueOf(method.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            log.warn("Unknown workflow http method={}, fallback to GET", method);
            return HttpMethod.GET;
        }
    }

    private URI buildHttpUri(String url, JSONArray queryParams, Map<String, Object> contextVariables) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            for (int index = 0; index < queryParams.size(); index++) {
                JSONObject item = queryParams.getJSONObject(index);
                if (!isEnabledEntry(item)) {
                    continue;
                }
                String key = renderTemplate(item.getString("key"), contextVariables);
                if (!StringUtils.hasText(key)) {
                    continue;
                }
                String value = renderTemplate(item.getString("value"), contextVariables);
                builder.queryParam(key, value != null ? value : "");
            }
        }
        return builder.build(true).toUri();
    }

    private HttpHeaders buildHttpHeaders(JSONArray headersArray, Map<String, Object> contextVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
        if (headersArray == null) {
            return headers;
        }
        for (int index = 0; index < headersArray.size(); index++) {
            JSONObject item = headersArray.getJSONObject(index);
            if (!isEnabledEntry(item)) {
                continue;
            }
            String key = renderTemplate(item.getString("key"), contextVariables);
            if (!StringUtils.hasText(key)) {
                continue;
            }
            String value = renderTemplate(item.getString("value"), contextVariables);
            headers.add(key, value != null ? value : "");
        }
        return headers;
    }

    private HttpEntity<String> buildHttpEntity(HttpMethod httpMethod, HttpHeaders headers, String body) {
        if (!StringUtils.hasText(body) || HttpMethod.GET.equals(httpMethod)) {
            return new HttpEntity<>(headers);
        }
        if (!headers.containsHeader(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(looksLikeJson(body) ? MediaType.APPLICATION_JSON : MediaType.TEXT_PLAIN);
        }
        return new HttpEntity<>(body, headers);
    }

    private boolean looksLikeJson(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }

    private boolean isEnabledEntry(JSONObject item) {
        return item != null && !Boolean.FALSE.equals(item.getBoolean("enabled"));
    }

    private Object parseHttpResponseBody(String responseBody, String responseType) {
        if (!StringUtils.hasText(responseBody)) {
            return null;
        }
        if (!"text".equalsIgnoreCase(responseType)) {
            try {
                return JSON.parse(responseBody);
            } catch (Exception exception) {
                log.debug("Parse workflow http response as json failed, fallback to text, error={}",
                        exception.getMessage());
            }
        }
        return responseBody;
    }

    private void persistHttpContextVariables(WorkflowEntity workflow, JSONObject node, ResponseEntity<String> response,
            Object parsedResponse, Map<String, Object> contextVariables) {
        String workflowUid = workflow.getUid();
        String nodeId = node.getString("id");
        String nodeKey = StringUtils.hasText(nodeId) ? nodeId : "http";

        Map<String, Object> nodeContext = new LinkedHashMap<>();
        nodeContext.put("response", parsedResponse);
        nodeContext.put("statusCode", response.getStatusCode().value());
        contextVariables.put(nodeKey, nodeContext);

        workflowVariableService.setLocalVariable(workflowUid, nodeKey, "response",
                parsedResponse, inferWorkflowVariableType(parsedResponse));
        workflowVariableService.setLocalVariable(workflowUid, nodeKey, "statusCode",
                response.getStatusCode().value(), WorkflowVariableTypeEnum.NUMBER);

        JSONObject data = node.getJSONObject("data");
        JSONArray mappings = data != null ? data.getJSONArray("responseMappings") : null;
        if (mappings == null) {
            return;
        }

        for (int index = 0; index < mappings.size(); index++) {
            JSONObject mapping = mappings.getJSONObject(index);
            if (mapping == null) {
                continue;
            }
            String key = mapping.getString("key");
            if (!StringUtils.hasText(key)) {
                continue;
            }
            Object extractedValue = extractMappedValue(parsedResponse, mapping.getString("path"));
            contextVariables.put(key, extractedValue);
            workflowVariableService.setVariable(workflowUid,
                    key,
                    extractedValue,
                    inferWorkflowVariableType(extractedValue),
                    WorkflowVariableScopeEnum.GLOBAL);
        }
    }

    private Object extractMappedValue(Object source, String path) {
        if (!StringUtils.hasText(path)) {
            return source;
        }
        String normalizedPath = path.trim();
        if (normalizedPath.startsWith("$.")) {
            normalizedPath = normalizedPath.substring(2);
        } else if (normalizedPath.startsWith("$")) {
            normalizedPath = normalizedPath.substring(1);
        }

        Object current = source;
        for (String segment : normalizedPath.split("\\.")) {
            current = resolvePathSegment(current, segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private Object resolvePathSegment(Object current, String rawSegment) {
        if (!StringUtils.hasText(rawSegment)) {
            return current;
        }

        String segment = rawSegment;
        while (StringUtils.hasText(segment)) {
            int bracketIndex = segment.indexOf('[');
            String property = bracketIndex >= 0 ? segment.substring(0, bracketIndex) : segment;
            if (StringUtils.hasText(property)) {
                current = resolveMapValue(current, property);
                if (current == null) {
                    return null;
                }
            }
            if (bracketIndex < 0) {
                return current;
            }

            int closingIndex = segment.indexOf(']', bracketIndex);
            if (closingIndex < 0) {
                return null;
            }
            String indexValue = segment.substring(bracketIndex + 1, closingIndex);
            current = resolveIndexedValue(current, indexValue);
            if (current == null) {
                return null;
            }
            segment = closingIndex + 1 < segment.length() ? segment.substring(closingIndex + 1) : null;
        }
        return current;
    }

    private Object resolveMapValue(Object current, String property) {
        if (current instanceof JSONObject jsonObject) {
            return jsonObject.get(property);
        }
        if (current instanceof Map<?, ?> map) {
            return map.get(property);
        }
        return null;
    }

    private Object resolveIndexedValue(Object current, String indexValue) {
        try {
            int index = Integer.parseInt(indexValue);
            if (current instanceof JSONArray jsonArray) {
                return index >= 0 && index < jsonArray.size() ? jsonArray.get(index) : null;
            }
            if (current instanceof List<?> list) {
                return index >= 0 && index < list.size() ? list.get(index) : null;
            }
        } catch (NumberFormatException exception) {
            return null;
        }
        return null;
    }

    private WorkflowVariableTypeEnum inferWorkflowVariableType(Object value) {
        if (value instanceof Number) {
            return WorkflowVariableTypeEnum.NUMBER;
        }
        if (value instanceof Boolean) {
            return WorkflowVariableTypeEnum.BOOLEAN;
        }
        if (value instanceof JSONObject || value instanceof Map<?, ?>) {
            return WorkflowVariableTypeEnum.OBJECT;
        }
        if (value instanceof JSONArray || value instanceof List<?>) {
            return WorkflowVariableTypeEnum.ARRAY;
        }
        return WorkflowVariableTypeEnum.STRING;
    }

    private Map<String, Object> buildContextVariables(WorkflowEntity workflow, ThreadEntity thread, ThreadExtra extra) {
        Map<String, Object> contextVariables = new LinkedHashMap<>();
        if (workflow != null && StringUtils.hasText(workflow.getUid())) {
            contextVariables.putAll(workflowVariableService.getVariables(workflow.getUid()));
            contextVariables.put("workflowUid", workflow.getUid());
            if (StringUtils.hasText(workflow.getNickname())) {
                contextVariables.put("workflowName", workflow.getNickname());
            }
        }

        if (thread != null) {
            if (StringUtils.hasText(thread.getUid())) {
                contextVariables.put("threadUid", thread.getUid());
            }
            if (StringUtils.hasText(thread.getOrgUid())) {
                contextVariables.put("orgUid", thread.getOrgUid());
            }
            if (thread.getUserProtobuf() != null) {
                if (StringUtils.hasText(thread.getUserProtobuf().getUid())) {
                    contextVariables.put("visitorUid", thread.getUserProtobuf().getUid());
                    contextVariables.put("userUid", thread.getUserProtobuf().getUid());
                }
                if (StringUtils.hasText(thread.getUserProtobuf().getNickname())) {
                    contextVariables.put("visitorNickname", thread.getUserProtobuf().getNickname());
                    contextVariables.put("userNickname", thread.getUserProtobuf().getNickname());
                }
            }
        }

        if (extra == null) {
            return contextVariables;
        }

        if (StringUtils.hasText(extra.getWorkflowSelectedOptionValue())) {
            contextVariables.put("workflowSelectedOptionValue", extra.getWorkflowSelectedOptionValue());
        }
        if (StringUtils.hasText(extra.getWorkflowQuestionAnswer())) {
            contextVariables.put("workflowQuestionAnswer", extra.getWorkflowQuestionAnswer());
            if (StringUtils.hasText(extra.getWorkflowQuestionVariable())) {
                contextVariables.put(extra.getWorkflowQuestionVariable(), extra.getWorkflowQuestionAnswer());
            }
        }
        if (StringUtils.hasText(extra.getWorkflowFormResponseData())) {
            contextVariables.put("workflowFormResponseData", extra.getWorkflowFormResponseData());
            mergeFormContextVariables(contextVariables, extra.getWorkflowFormResponseData());
        }
        return contextVariables;
    }

    private void mergeFormContextVariables(Map<String, Object> contextVariables, String formResponseData) {
        try {
            Object parsed = JSON.parse(formResponseData);
            contextVariables.put("form", parsed);
            if (parsed instanceof JSONObject jsonObject) {
                for (String key : jsonObject.keySet()) {
                    contextVariables.put(key, jsonObject.get(key));
                }
            }
        } catch (Exception exception) {
            log.debug("Parse workflow form response for context failed, error={}", exception.getMessage());
        }
    }

    private String renderTemplate(String template, Map<String, Object> contextVariables) {
        if (!StringUtils.hasText(template) || contextVariables == null || contextVariables.isEmpty()) {
            return template;
        }
        Matcher matcher = CONTEXT_TOKEN_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object resolvedValue = resolveContextValue(contextVariables, key);
            matcher.appendReplacement(buffer,
                    Matcher.quoteReplacement(resolvedValue != null ? String.valueOf(resolvedValue) : ""));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private Object resolveContextValue(Map<String, Object> contextVariables, String rawKey) {
        if (!StringUtils.hasText(rawKey)) {
            return null;
        }
        String key = rawKey.trim();
        if (contextVariables.containsKey(key)) {
            return contextVariables.get(key);
        }

        Object current = contextVariables;
        for (String segment : key.split("\\.")) {
            current = resolvePathSegment(current, segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    @Data
    @Builder
    private static class ExecutionResult {

        @Builder.Default
        private List<WorkflowMessageDraft> messages = new ArrayList<>();

        private String currentNodeId;

        private String waitingChoiceNodeId;

        private String waitingQuestionNodeId;

        private String waitingFormNodeId;

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