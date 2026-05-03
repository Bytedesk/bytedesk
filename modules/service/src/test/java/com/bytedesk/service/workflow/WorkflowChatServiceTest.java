/*
 * @Author: GitHub Copilot
 * @Description: Tests workflow chat execution against default schema shape
 */
package com.bytedesk.service.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.content.ChoiceContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadExtra;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.workflow_variable.WorkflowVariableScopeEnum;
import com.bytedesk.core.workflow_variable.WorkflowVariableService;
import com.bytedesk.core.workflow_variable.WorkflowVariableTypeEnum;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkflowChatServiceTest {

  @Mock
  private ThreadRestService threadRestService;

  @Mock
  private MessageRestService messageRestService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private WorkflowVariableService workflowVariableService;

  private WorkflowChatService workflowChatService;

  @BeforeEach
  void setUp() throws Exception {
    UidUtils uidUtils = mock(UidUtils.class);
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    when(uidUtils.getUid()).thenReturn(
        "msg-uid-1", "msg-uid-2", "msg-uid-3", "msg-uid-4",
        "msg-uid-5", "msg-uid-6", "msg-uid-7", "msg-uid-8");
    when(applicationContext.getBean(ModelMapper.class)).thenReturn(new ModelMapper());
    setUidUtilsInstance(uidUtils);
    setApplicationContext(applicationContext);

    workflowChatService = new WorkflowChatService(
      threadRestService,
      messageRestService,
      restTemplate,
      workflowVariableService);
    when(threadRestService.save(any(ThreadEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(workflowVariableService.getVariables(anyString())).thenReturn(new HashMap<>());
    lenient().when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(), eq(String.class)))
      .thenReturn(ResponseEntity.ok("{}"));
  }

  @Test
  void createStartMessageTraversesDefaultSchemaUntilChoice() {
    WorkflowEntity workflow = buildWorkflow(defaultWorkflowSchema());
    ThreadEntity thread = buildThread();

    MessageProtobuf message = workflowChatService.createStartMessage(workflow, thread);

    assertThat(message).isNotNull();
    assertThat(String.valueOf(message.getType())).isEqualTo(MessageTypeEnum.TEXT.name());
    assertThat(message.getContent()).isEqualTo("您好，我是流程助手");

    ArgumentCaptor<MessageEntity> messageCaptor = ArgumentCaptor.forClass(MessageEntity.class);
    verify(messageRestService, times(2)).save(messageCaptor.capture());
    List<MessageEntity> savedMessages = messageCaptor.getAllValues();
    assertThat(savedMessages).hasSize(2);
    assertThat(savedMessages.get(0).getType()).isEqualTo(MessageTypeEnum.TEXT.name());
    assertThat(savedMessages.get(0).getContent()).isEqualTo("您好，我是流程助手");
    assertThat(savedMessages.get(1).getType()).isEqualTo(MessageTypeEnum.CHOICE.name());
    ChoiceContent choiceContent = ChoiceContent.fromJson(savedMessages.get(1).getContent());
    assertThat(choiceContent).isNotNull();
    assertThat(choiceContent.getContent()).isEqualTo("请选择您需要的帮助方向");
    assertThat(choiceContent.getOptions()).hasSize(2);
    assertThat(choiceContent.getOptions().get(0).getOptionUid()).isEqualTo("choice-1_0");
    assertThat(choiceContent.getOptions().get(0).getTitle()).isEqualTo("咨询产品功能");

    ThreadExtra extra = ThreadExtra.fromJson(thread.getExtra());
    assertThat(extra.getWorkflowCurrentNodeId()).isEqualTo("choice-1");
    assertThat(extra.getWorkflowWaitingChoiceNodeId()).isEqualTo("choice-1");
    assertThat(extra.getWorkflowCompleted()).isFalse();
    assertThat(extra.getShowQuickButtons()).isFalse();
    assertThat(extra.getQuickButtons()).isEmpty();
  }

  @Test
  void continueAfterChoiceResumesToFollowupTextAndMarksCompletedAtEnd() {
    WorkflowEntity workflow = buildWorkflow(defaultWorkflowSchema());
    ThreadEntity thread = buildThread();

    workflowChatService.createStartMessage(workflow, thread);
    Optional<MessageProtobuf> response = workflowChatService.continueAfterChoice(
        workflow,
        thread,
        "product");

    assertThat(response).isPresent();
    assertThat(response.get().getContent()).isEqualTo("已为您准备好后续说明");

    ThreadExtra afterContinue = ThreadExtra.fromJson(thread.getExtra());
    assertThat(afterContinue.getWorkflowCurrentNodeId()).isEqualTo("end-1");
    assertThat(afterContinue.getWorkflowWaitingChoiceNodeId()).isNull();
    assertThat(afterContinue.getWorkflowCompleted()).isTrue();
    assertThat(afterContinue.getShowQuickButtons()).isFalse();
    assertThat(afterContinue.getQuickButtons()).isEmpty();
    assertThat(afterContinue.getWorkflowSelectedOptionValue()).isEqualTo("product");
  }

  @Test
  void continueAfterChoiceRoutesByDynamicChoicePort() {
    WorkflowEntity workflow = buildWorkflow(choiceBranchWorkflowSchema());
    ThreadEntity thread = buildThread();

    workflowChatService.createStartMessage(workflow, thread);
    Optional<MessageProtobuf> response = workflowChatService.continueAfterChoice(
        workflow,
        thread,
        "option-agent");

    assertThat(response).isPresent();
    assertThat(response.get().getContent()).isEqualTo("已切换到人工客服分支");

    ThreadExtra afterContinue = ThreadExtra.fromJson(thread.getExtra());
    assertThat(afterContinue.getWorkflowCurrentNodeId()).isEqualTo("end-1");
    assertThat(afterContinue.getWorkflowSelectedOptionValue()).isEqualTo("agent");
    assertThat(afterContinue.getWorkflowCompleted()).isTrue();
  }

  @Test
  void messageAndQuestionNodesPauseWorkflowAsSeparateMessages() {
    WorkflowEntity workflow = buildWorkflow(messageQuestionConditionSchema());
    ThreadEntity thread = buildThread();

    MessageProtobuf message = workflowChatService.createStartMessage(workflow, thread);

    assertThat(message).isNotNull();
    assertThat(message.getContent()).isEqualTo("第一条消息");

    ArgumentCaptor<MessageEntity> messageCaptor = ArgumentCaptor.forClass(MessageEntity.class);
    verify(messageRestService, times(2)).save(messageCaptor.capture());
    List<MessageEntity> savedMessages = messageCaptor.getAllValues();
    assertThat(savedMessages).hasSize(2);
    assertThat(savedMessages.get(0).getContent()).isEqualTo("第一条消息");
    assertThat(savedMessages.get(1).getContent()).isEqualTo("请输入您的问题");
    ThreadExtra extra = ThreadExtra.fromJson(thread.getExtra());
    assertThat(extra.getWorkflowWaitingQuestionNodeId()).isEqualTo("question-1");
    assertThat(extra.getWorkflowQuestionVariable()).isEqualTo("intent");
    assertThat(extra.getWorkflowCompleted()).isFalse();
  }

  @Test
  void continueAfterQuestionRoutesConditionBranchAndCompletesWorkflow() {
    WorkflowEntity workflow = buildWorkflow(messageQuestionConditionSchema());
    ThreadEntity thread = buildThread();

    workflowChatService.createStartMessage(workflow, thread);
    List<MessageProtobuf> responses = workflowChatService.continueAfterQuestionMessages(
        workflow,
        thread,
        "intent_handoff");

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).getContent()).isEqualTo("根据条件继续处理");
    assertThat(responses.get(1).getContent()).isEqualTo("转人工客服");

    ThreadExtra extra = ThreadExtra.fromJson(thread.getExtra());
    assertThat(extra.getWorkflowWaitingQuestionNodeId()).isNull();
    assertThat(extra.getWorkflowQuestionAnswer()).isEqualTo("intent_handoff");
    assertThat(extra.getWorkflowCurrentNodeId()).isEqualTo("end-1");
    assertThat(extra.getWorkflowCompleted()).isTrue();
  }

  @Test
  void formNodePausesUntilSubmitThenContinuesWorkflow() {
    WorkflowEntity workflow = buildWorkflow(formWorkflowSchema());
    ThreadEntity thread = buildThread();

    MessageProtobuf message = workflowChatService.createStartMessage(workflow, thread);

    assertThat(message).isNotNull();
    assertThat(String.valueOf(message.getType())).isEqualTo(MessageTypeEnum.FORM.name());
    JSONObject formPayload = JSON.parseObject(message.getContent());
    assertThat(formPayload.getString("formUid")).isEqualTo("form-1");
    JSONArray formFields = JSON.parseArray(formPayload.getString("schema"));
    assertThat(formFields).isNotNull();
    assertThat(formFields.toJSONString()).contains("\"type\":\"upload\"");

    ThreadExtra extra = ThreadExtra.fromJson(thread.getExtra());
    assertThat(extra.getWorkflowWaitingFormNodeId()).isEqualTo("form-1");
    assertThat(extra.getWorkflowCompleted()).isFalse();

    List<MessageProtobuf> responses = workflowChatService.continueAfterFormMessages(
        workflow,
        thread,
        "{\"name\":\"张三\"}");

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getContent()).isEqualTo("表单已提交，继续后续流程");

    ThreadExtra afterContinue = ThreadExtra.fromJson(thread.getExtra());
    assertThat(afterContinue.getWorkflowWaitingFormNodeId()).isNull();
    assertThat(afterContinue.getWorkflowFormResponseData()).isEqualTo("{\"name\":\"张三\"}");
    assertThat(afterContinue.getWorkflowCurrentNodeId()).isEqualTo("end-1");
    assertThat(afterContinue.getWorkflowCompleted()).isTrue();
  }

  @Test
  void httpNodeExecutesAndWritesResponseMappingsIntoContextVariables() {
    WorkflowEntity workflow = buildWorkflow(httpWorkflowSchema());
    ThreadEntity thread = buildThread();

    Map<String, Object> existingVariables = new HashMap<>();
    existingVariables.put("orderId", "order-1001");
    existingVariables.put("token", "token-abc");
    when(workflowVariableService.getVariables("wf-1")).thenReturn(existingVariables);
    when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(), eq(String.class)))
        .thenReturn(ResponseEntity.ok("{\"data\":{\"status\":\"SHIPPED\"}}"));

    MessageProtobuf message = workflowChatService.createStartMessage(workflow, thread);

    assertThat(message).isNotNull();
    assertThat(message.getContent()).isEqualTo("订单状态：SHIPPED");

    ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
    verify(restTemplate).exchange(uriCaptor.capture(), eq(HttpMethod.GET), any(), eq(String.class));
    assertThat(uriCaptor.getValue().toString())
        .isEqualTo("https://api.example.com/orders/order-1001?visitorUid=visitor-1");

    verify(workflowVariableService).setLocalVariable(
        eq("wf-1"),
        eq("http-1"),
        eq("response"),
        any(),
        eq(WorkflowVariableTypeEnum.OBJECT));
    verify(workflowVariableService).setVariable(
        eq("wf-1"),
        eq("orderStatus"),
        eq("SHIPPED"),
        eq(WorkflowVariableTypeEnum.STRING),
        eq(WorkflowVariableScopeEnum.GLOBAL));
  }

  private WorkflowEntity buildWorkflow(String schema) {
    return WorkflowEntity.builder()
        .uid("wf-1")
        .nickname("默认流程")
        .description("流程已结束")
        .schema(schema)
        .build();
  }

  private ThreadEntity buildThread() {
    ThreadEntity thread = new ThreadEntity();
    thread.setUid("thread-1");
    thread.setOrgUid("org-1");
    thread.setExtra(ThreadExtra.builder().build().toJson());
    thread.setUser(UserProtobuf.builder()
      .uid("visitor-1")
      .nickname("访客A")
      .type(UserTypeEnum.VISITOR.name())
      .build()
      .toJson());
    thread.setWorkflow(UserProtobuf.builder()
        .uid("wf-1")
        .nickname("默认流程")
        .type(UserTypeEnum.WORKFLOW.name())
        .build()
        .toJson());
    return thread;
  }

  private String defaultWorkflowSchema() {
    return """
        {
          "nodes": [
            {
              "id": "start-1",
              "type": "start",
              "name": "开始"
            },
            {
              "id": "text-1",
              "type": "text",
              "data": {
                "content": "您好，我是流程助手"
              }
            },
            {
              "id": "choice-1",
              "type": "choice",
              "data": {
                "content": "请选择您需要的帮助方向",
                "options": [
                  {
                    "label": "咨询产品功能",
                    "value": "product"
                  },
                  {
                    "label": "联系人工客服",
                    "value": "agent"
                  }
                ]
              }
            },
            {
              "id": "text-2",
              "type": "text",
              "data": {
                "content": "已为您准备好后续说明"
              }
            },
            {
              "id": "end-1",
              "type": "end",
              "name": "结束"
            }
          ],
          "edges": [
            {
              "sourceNodeID": "start-1",
              "targetNodeID": "text-1"
            },
            {
              "sourceNodeID": "text-1",
              "targetNodeID": "choice-1"
            },
            {
              "sourceNodeID": "choice-1",
              "targetNodeID": "text-2"
            },
            {
              "sourceNodeID": "text-2",
              "targetNodeID": "end-1"
            }
          ]
        }
        """;
  }

  private String messageQuestionConditionSchema() {
    return """
        {
          "nodes": [
            {
              "id": "start-1",
              "type": "start",
              "name": "开始"
            },
            {
              "id": "message-1",
              "type": "message",
              "data": {
                "content": "第一条消息"
              }
            },
            {
              "id": "question-1",
              "type": "question",
              "data": {
                "content": "请输入您的问题",
                "variable": "intent"
              }
            },
            {
              "id": "condition-1",
              "type": "condition",
              "data": {
                "description": "根据条件继续处理",
                "conditions": [
                  {
                    "key": "intent_order",
                    "label": "订单咨询",
                    "outgoingEdgeId": "edge-condition-order"
                  },
                  {
                    "key": "intent_handoff",
                    "label": "人工客服",
                    "outgoingEdgeId": "edge-condition-handoff"
                  }
                ]
              }
            },
            {
              "id": "text-order",
              "type": "text",
              "data": {
                "content": "订单处理说明"
              }
            },
            {
              "id": "text-handoff",
              "type": "text",
              "data": {
                "content": "转人工客服"
              }
            },
            {
              "id": "end-1",
              "type": "end",
              "name": "结束"
            }
          ],
          "edges": [
            {
              "sourceNodeID": "start-1",
              "targetNodeID": "message-1"
            },
            {
              "sourceNodeID": "message-1",
              "targetNodeID": "question-1"
            },
            {
              "sourceNodeID": "question-1",
              "targetNodeID": "condition-1"
            },
            {
              "id": "edge-condition-order",
              "sourceNodeID": "condition-1",
              "targetNodeID": "text-order",
              "sourcePortID": "intent_order"
            },
            {
              "id": "edge-condition-handoff",
              "sourceNodeID": "condition-1",
              "targetNodeID": "text-handoff",
              "sourcePortID": "intent_handoff"
            },
            {
              "sourceNodeID": "text-order",
              "targetNodeID": "end-1"
            },
            {
              "sourceNodeID": "text-handoff",
              "targetNodeID": "end-1"
            }
          ]
        }
        """;
  }

  private String choiceBranchWorkflowSchema() {
    return """
        {
          "nodes": [
            {
              "id": "start-1",
              "type": "start",
              "name": "开始"
            },
            {
              "id": "choice-1",
              "type": "choice",
              "data": {
                "content": "请选择服务类型",
                "options": [
                  {
                    "id": "option-product",
                    "label": "产品咨询",
                    "value": "product"
                  },
                  {
                    "id": "option-agent",
                    "label": "人工客服",
                    "value": "agent"
                  }
                ]
              }
            },
            {
              "id": "text-product",
              "type": "text",
              "data": {
                "content": "已切换到产品咨询分支"
              }
            },
            {
              "id": "text-agent",
              "type": "text",
              "data": {
                "content": "已切换到人工客服分支"
              }
            },
            {
              "id": "end-1",
              "type": "end",
              "name": "结束"
            }
          ],
          "edges": [
            {
              "sourceNodeID": "start-1",
              "targetNodeID": "choice-1"
            },
            {
              "sourceNodeID": "choice-1",
              "targetNodeID": "text-product",
              "sourcePortID": "choice-option-option-product"
            },
            {
              "sourceNodeID": "choice-1",
              "targetNodeID": "text-agent",
              "sourcePortID": "choice-option-option-agent"
            },
            {
              "sourceNodeID": "text-product",
              "targetNodeID": "end-1"
            },
            {
              "sourceNodeID": "text-agent",
              "targetNodeID": "end-1"
            }
          ]
        }
        """;
  }

  private String formWorkflowSchema() {
    return """
        {
          "nodes": [
            {
              "id": "start-1",
              "type": "start",
              "name": "开始"
            },
            {
              "id": "form-1",
              "type": "form",
              "data": {
                "title": "收集信息",
                "content": "请填写以下内容后继续",
                "formFields": [
                  {
                    "id": "name",
                    "type": "input",
                    "label": "姓名",
                    "required": true
                  },
                  {
                    "id": "need",
                    "type": "select",
                    "label": "咨询类型",
                    "required": true,
                    "options": ["产品咨询", "售后支持"]
                  },
                  {
                    "id": "attachment",
                    "type": "upload",
                    "label": "上传附件",
                    "required": false,
                    "props": {
                      "accept": ".png,.jpg,.pdf"
                    }
                  }
                ]
              }
            },
            {
              "id": "text-1",
              "type": "text",
              "data": {
                "content": "表单已提交，继续后续流程"
              }
            },
            {
              "id": "end-1",
              "type": "end",
              "name": "结束"
            }
          ],
          "edges": [
            {
              "sourceNodeID": "start-1",
              "targetNodeID": "form-1"
            },
            {
              "sourceNodeID": "form-1",
              "targetNodeID": "text-1"
            },
            {
              "sourceNodeID": "text-1",
              "targetNodeID": "end-1"
            }
          ]
        }
        """;
  }

  private String httpWorkflowSchema() {
    return """
        {
          "nodes": [
            {
              "id": "start-1",
              "type": "start",
              "name": "开始"
            },
            {
              "id": "http-1",
              "type": "http",
              "data": {
                "method": "GET",
                "url": "https://api.example.com/orders/{{orderId}}",
                "headers": [
                  {
                    "id": "header-1",
                    "key": "Authorization",
                    "value": "Bearer {{token}}",
                    "enabled": true
                  }
                ],
                "queryParams": [
                  {
                    "id": "query-1",
                    "key": "visitorUid",
                    "value": "{{visitorUid}}",
                    "enabled": true
                  }
                ],
                "responseType": "json",
                "responseMappings": [
                  {
                    "id": "mapping-1",
                    "key": "orderStatus",
                    "path": "data.status"
                  }
                ]
              }
            },
            {
              "id": "text-1",
              "type": "text",
              "data": {
                "content": "订单状态：{{orderStatus}}"
              }
            },
            {
              "id": "end-1",
              "type": "end",
              "name": "结束"
            }
          ],
          "edges": [
            {
              "sourceNodeID": "start-1",
              "targetNodeID": "http-1"
            },
            {
              "sourceNodeID": "http-1",
              "targetNodeID": "text-1"
            },
            {
              "sourceNodeID": "text-1",
              "targetNodeID": "end-1"
            }
          ]
        }
        """;
  }

  private void setUidUtilsInstance(UidUtils uidUtils) throws Exception {
    Field instanceField = UidUtils.class.getDeclaredField("instance");
    instanceField.setAccessible(true);
    instanceField.set(null, uidUtils);
  }

  private void setApplicationContext(ApplicationContext applicationContext) throws Exception {
    Field contextField = ApplicationContextHolder.class.getDeclaredField("context");
    contextField.setAccessible(true);
    contextField.set(null, applicationContext);
  }
}