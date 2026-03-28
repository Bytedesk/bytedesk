package ai.z.openapi.service.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ai.z.openapi.service.assistant.message.AssistantMessageContent;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.deserialize.assistant.AssistantChoiceDeserializer;

import java.util.Iterator;
import java.util.Map;

/**
 * Represents an assistant's choice output in conversation responses. This class contains
 * the response index, message content, finish reason, and metadata.
 */
@JsonDeserialize(using = AssistantChoiceDeserializer.class)
public class AssistantChoice extends ObjectNode {

	/**
	 * Result index.
	 */
	@JsonProperty("index")
	private int index;

	/**
	 * Current conversation output message content.
	 */
	@JsonProperty("delta")
	private AssistantMessageContent delta;

	/**
	 * Reason for inference completion: - stop: inference naturally ended or triggered
	 * stop words - sensitive: model inference content was blocked by security review -
	 * network_error: model inference service exception
	 */
	@JsonProperty("finish_reason")
	private String finishReason;

	/**
	 * Metadata, extension field.
	 */
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	public AssistantChoice() {
		super(JsonNodeFactory.instance);
	}

	public AssistantChoice(ObjectNode objectNode) {
		super(JsonNodeFactory.instance);

		if (objectNode == null) {
			return;
		}

		ObjectMapper objectMapper = MessageDeserializeFactory.defaultObjectMapper();
		if (objectNode.has("index")) {
			this.setIndex(objectNode.get("index").asInt());
		}
		else {
			this.setIndex(0);
		}
		if (objectNode.has("delta")) {
			AssistantMessageContent delta = objectMapper.convertValue(objectNode.get("delta"),
					AssistantMessageContent.class);
			this.setDelta(delta);
		}
		else {
			this.setDelta(null);
		}
		if (objectNode.has("finish_reason")) {
			this.setFinishReason(objectNode.get("finish_reason").asText());

		}
		else {
			this.setFinishReason(null);
		}
		if (objectNode.has("metadata")) {
			Map<String, Object> metadata = objectMapper.convertValue(objectNode.get("metadata"),
					new TypeReference<Map<String, Object>>() {
					});

			this.setMetadata(metadata);
		}
		else {
			this.setMetadata(null);
		}

		Iterator<String> fieldNames = objectNode.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode field = objectNode.get(fieldName);
			this.set(fieldName, field);
		}
	}
	// Getters and Setters

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		this.put("index", index);
	}

	public AssistantMessageContent getDelta() {
		return delta;
	}

	public void setDelta(AssistantMessageContent delta) {
		this.delta = delta;
		this.putPOJO("delta", delta);
	}

	public String getFinishReason() {
		return finishReason;
	}

	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
		this.put("finish_reason", finishReason);
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
		this.putPOJO("metadata", metadata);
	}

}
