package ai.z.openapi.service.deserialize.assistant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.assistant.AssistantChoice;
import ai.z.openapi.service.deserialize.BaseNodeDeserializer;
import ai.z.openapi.service.deserialize.ObjectDeserializer;

import java.io.IOException;

/**
 * Deserializer that can build instances of {@link AssistantChoice} from any JSON content,
 * using appropriate {@link AssistantChoice} type.
 */
public class AssistantChoiceDeserializer extends BaseNodeDeserializer<AssistantChoice> {

	private final static AssistantChoiceDeserializer instance = new AssistantChoiceDeserializer();

	public AssistantChoiceDeserializer() {
		super(AssistantChoice.class, null);
	}

	public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
		if (nodeClass == ObjectNode.class) {
			return ObjectDeserializer.getInstance();
		}
		return instance;
	}

	@Override
	public AssistantChoice getNullValue(DeserializationContext ctxt) {
		return null;
	}

	@Override
	public AssistantChoice deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		if (p.currentTokenId() == JsonTokenId.ID_START_OBJECT) {
			ObjectNode jsonNodes = deserializeObject(p, ctxt, ctxt.getNodeFactory());
			return new AssistantChoice(jsonNodes);
		}
		return null;
	}

}
