package com.bytedesk.ai.provider.ollama;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.decision.DecisionPrompt;
import org.springframework.ai.decision.DecisionResponse;
import org.springframework.ai.decision.answer.ChoiceAnswer;
import org.springframework.ai.decision.answer.RefusalAnswer;
import org.springframework.ai.decision.answer.ScoreAnswer;
import org.springframework.ai.decision.answer.YesNoAnswer;
import org.springframework.ai.decision.input.ConversationInput;
import org.springframework.ai.decision.input.FieldsInput;
import org.springframework.ai.decision.input.TextInput;
import org.springframework.ai.decision.question.ChoiceQuestion;
import org.springframework.ai.decision.question.ScoreQuestion;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.ObjectProvider;

import com.bytedesk.ai.provider.ollama.decision.DecisionAnswerView;
import com.bytedesk.ai.provider.ollama.decision.DecisionDemoRequest;
import com.bytedesk.ai.provider.ollama.decision.DecisionDemoResponse;
import com.bytedesk.ai.service.TokenUsageHelper;

class SpringAIOllamaChatServiceDecisionTest {

    private static final String OLLAMA_URL = "http://127.0.0.1:11434";

    private SpringAIOllamaChatService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        service = new SpringAIOllamaChatService(mock(OllamaApi.class),
                (ObjectProvider<org.springframework.ai.ollama.OllamaChatModel>) mock(ObjectProvider.class),
                mock(TokenUsageHelper.class), OLLAMA_URL);
    }

    @Test
    void triagePromptUsesTextInputAndDocQuickStartQuestions() {
        DecisionPrompt prompt = service.createDecisionPrompt(DecisionDemoRequest.builder()
                .message("My new headphones arrived yesterday, but the left side doesn't work.")
                .build());

        assertTrue(prompt.getInstructions() instanceof TextInput text && text.getText().startsWith("My new"));
        assertEquals(List.of("defect", "topic", "satisfaction"),
                List.copyOf(prompt.getQuestions().keySet()));
        assertNull(prompt.getOptions(), "no runtime override means the server default model applies");
    }

    @Test
    void requestModelBecomesRuntimeOption() {
        DecisionPrompt prompt = service.createDecisionPrompt(DecisionDemoRequest.builder()
                .message("hello").model("d1-omni:2b").build());

        assertNotNull(prompt.getOptions());
        assertEquals("d1-omni:2b", prompt.getOptions().getModel());
    }

    @Test
    void fieldsAndConversationPromptsUseTypedInputs() {
        DecisionPrompt fields = service.createDecisionPrompt(DecisionDemoRequest.builder()
                .type("fields").message("Third time late").orders(12).lateDeliveries(3).build());
        assertTrue(fields.getInstructions() instanceof FieldsInput);
        assertEquals(Map.of("message", "Third time late", "orders", 12, "late_deliveries", 3),
                ((FieldsInput) fields.getInstructions()).getFields());
        assertTrue(fields.getQuestions().get("repeated") instanceof org.springframework.ai.decision.question.YesNoQuestion);

        DecisionPrompt conversation = service.createDecisionPrompt(DecisionDemoRequest.builder()
                .type("conversation").message("ignored")
                .userMessage("Please cancel my order.").assistantMessage("Your order will arrive on Friday.").build());
        assertTrue(conversation.getInstructions() instanceof ConversationInput);
        assertEquals(2, ((ConversationInput) conversation.getInstructions()).getMessages().size());
        assertTrue(conversation.getQuestions().get("answered") instanceof org.springframework.ai.decision.question.YesNoQuestion);
    }

    @Test
    void explainReadsAllThreeAnswerKindsLikeTheDocs() {
        DecisionDemoRequest request = DecisionDemoRequest.builder()
                .message("headphones broken").build();
        DecisionPrompt prompt = service.createDecisionPrompt(request);
        // Map<String, org.springframework.ai.decision.question.DecisionQuestion> questions = prompt.getQuestions();

        Map<String, Double> topicProbabilities = new LinkedHashMap<>();
        topicProbabilities.put("delivery", 0.05);
        topicProbabilities.put("product", 0.80);
        topicProbabilities.put("account", 0.05);
        topicProbabilities.put("other", 0.10);

        DecisionResponse response = DecisionResponse.builder(prompt)
                .answer("defect", YesNoAnswer.builder().probability(0.7).build())
                .answer("topic", ChoiceAnswer.builder().value("product")
                        .probabilities(topicProbabilities)
                        .confidence(0.75).build())
                .answer("satisfaction", ScoreAnswer.builder().value(1.0)
                        .probabilities(List.of(0.5, 0.0, 0.5)).confidence(0.4).build())
                .build();

        DecisionDemoResponse result = service.explain(request, prompt, response);

        DecisionAnswerView defect = result.getQuestions().get(0);
        assertEquals("yes_no", defect.getKind());
        assertTrue(defect.getIsYes(), "0.7 >= default threshold 0.5");
        assertFalse(defect.getIsYesStrict(), "0.7 < strict threshold 0.9");

        DecisionAnswerView topic = result.getQuestions().get(1);
        assertEquals("choice", topic.getKind());
        assertEquals("product", topic.getValue());
        // Ties (delivery/account both 0.05) keep insertion order, as the provider reports them.
        assertEquals(List.of("product", "other", "delivery", "account"), topic.getRankedOptions());
        assertEquals(0.75, topic.getConfidence());

        // Doc example: probabilities 0.5, 0, 0.5 over unhappy/neutral/happy with expected value 1.0.
        // Nearest level is 0 (unhappy and happy tie, unhappy is closer to 1.0); rounded level is 1 (neutral).
        DecisionAnswerView satisfaction = result.getQuestions().get(2);
        assertEquals("score", satisfaction.getKind());
        assertEquals(0, satisfaction.getNearestLevel());
        assertEquals("unhappy", satisfaction.getNearestLevelLabel());
        assertEquals(1, satisfaction.getRoundedLevel());
        assertEquals("neutral", satisfaction.getRoundedLevelLabel());
        assertEquals(1.0, satisfaction.getExpectedValue());
        assertEquals(List.of(0.5, 0.0, 0.5), satisfaction.getLevelProbabilities());
        assertEquals(List.of("unhappy", "neutral", "happy"), satisfaction.getExpectedLevelLabels());
    }

    @Test
    void refusalIsReportedInsteadOfThrowing() {
        DecisionDemoRequest request = DecisionDemoRequest.builder().message("x").build();
        DecisionPrompt prompt = service.createDecisionPrompt(request);
        DecisionResponse response = DecisionResponse.builder(prompt)
                .answer("defect", RefusalAnswer.builder().reason("not enough context").build())
                .answer("topic", ChoiceAnswer.builder().value("other").probabilities(Map.of("other", 1.0)).build())
                .answer("satisfaction", ScoreAnswer.builder().value(1.0).probabilities(List.of(0.0, 1.0, 0.0)).build())
                .build();

        DecisionDemoResponse result = service.explain(request, prompt, response);

        DecisionAnswerView defect = result.getQuestions().get(0);
        assertTrue(defect.isRefused());
        assertEquals("refusal", defect.getKind());
        assertEquals("choice", result.getQuestions().get(1).getKind());
    }

    @Test
    void decisionModelPointsAtConfiguredOllamaAndNeverPullsModels() {
        var model = service.createDecisionModel();
        assertNotNull(model);
        assertEquals(SpringAIOllamaChatService.DEFAULT_DECISION_MODEL, model.getOptions().getModel());
        assertEquals("d1-omni:600m", SpringAIOllamaChatService.DEFAULT_DECISION_MODEL);
    }

    @Test
    @EnabledIfSystemProperty(named = "bytedesk.test.decision.live", matches = "true")
    void callsInstalledLocalDecisionModelWhenExplicitlyEnabled() {
        var model = service.createDecisionModel();
        DecisionResponse response = model.call(service.createDecisionPrompt(DecisionDemoRequest.builder()
                .message("My new headphones arrived yesterday, but the left side doesn't work. "
                        + "Can I get my money back?")
                .build()));
        // Small decision models are sensitive to wording; assert the response shape and the
        // clear-cut answers only, not a specific score level.
        assertEquals("d1-omni:600m", response.getMetadata().getModel());
        assertTrue(response.getYesNo("defect").isYes());
        assertEquals("product", response.getChoice("topic").getValue());
        assertTrue(response.getScore("satisfaction").getProbabilities().size() == 3);
        assertTrue(response.getMetadata().getUsage().getPromptTokens() > 0);
    }

    @Test
    void helperQuestionsMatchDocumentedShapes() {
        ChoiceQuestion topic = (ChoiceQuestion) service.buildTriageQuestions().get("topic");
        assertEquals(List.of("delivery", "product", "account", "other"), topic.getOptionNames());

        ScoreQuestion satisfaction = (ScoreQuestion) service.buildTriageQuestions().get("satisfaction");
        assertEquals(List.of("unhappy", "neutral", "happy"), satisfaction.getLevelLabels());

        assertEquals(1, service.buildGuardrailQuestions().size());
        assertEquals(1, service.buildFieldsQuestions().size());
        assertEquals(1, service.buildConversationQuestions().size());
    }

    @Test
    void conversationDemoBuildsUserAndAssistantMessages() {
        DecisionPrompt prompt = service.createDecisionPrompt(DecisionDemoRequest.builder().type("conversation")
                .userMessage("q").assistantMessage("a").build());
        var messages = ((ConversationInput) prompt.getInstructions()).getMessages();
        assertTrue(messages.get(0) instanceof UserMessage);
        assertTrue(messages.get(1) instanceof AssistantMessage);
    }

}
