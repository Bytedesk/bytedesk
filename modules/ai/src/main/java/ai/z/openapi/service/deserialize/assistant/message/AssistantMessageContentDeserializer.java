package ai.z.openapi.service.deserialize.assistant.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import ai.z.openapi.service.assistant.message.AssistantMessageContent;
import ai.z.openapi.service.assistant.message.AssistantTextContentBlock;
import ai.z.openapi.service.assistant.message.tools.AssistantToolsType;
import ai.z.openapi.service.deserialize.JsonTypeField;
import ai.z.openapi.service.deserialize.JsonTypeMapping;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class AssistantMessageContentDeserializer extends JsonDeserializer<AssistantMessageContent> {

	@Override
	public AssistantMessageContent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.readValueAsTree();

		// Get JsonTypeMapping annotation from MessageContent class
		JsonTypeMapping mapping = AssistantMessageContent.class.getAnnotation(JsonTypeMapping.class);
		if (mapping == null) {
			throw new IllegalStateException("Missing JsonTypeMapping annotation on MessageContent class");
		}

		// Iterate through classes defined in annotation to determine suitable class based
		// on annotation or static method values
		for (Class<?> clazz : mapping.value()) {
			JsonTypeField typeField = clazz.getAnnotation(JsonTypeField.class);

			if (typeField != null && node.has(typeField.value())) {
				try {
					// Create instance of the class
					Object obj = clazz.getDeclaredConstructor().newInstance();

					// Use reflection to manually set field values
					for (Field field : clazz.getDeclaredFields()) {
						field.setAccessible(true);
						// field.getName() gets the value from JsonProperty annotation
						// above
						JsonProperty annotation = field.getAnnotation(JsonProperty.class);
						String name = null;
						if (annotation == null) {
							name = field.getName();
						}
						else {
							name = annotation.value();
						}
						if (node.has(name)) {
							// Set values based on field type, assuming fields are
							// primitive types or strings
							if (field.getType().equals(String.class)) {
								field.set(obj, node.get(name).asText());
							}
							else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
								field.set(obj, node.get(name).asInt());
							}
							else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
								field.set(obj, node.get(name).asBoolean());
							}
							else if (List.class.isAssignableFrom(field.getType())) {
								// Check if generic type is ToolsType
								ParameterizedType listType = (ParameterizedType) field.getGenericType();
								Class<?> listGenericType = (Class<?>) listType.getActualTypeArguments()[0];

								if (listGenericType.equals(AssistantToolsType.class)) {
									// Deserialize to List<ToolsType>
									List<AssistantToolsType> list = new ObjectMapper()
										.readerForListOf(AssistantToolsType.class)
										.readValue(node.get(name));
									field.set(obj, list);
								}
								else {
									throw new IllegalArgumentException(
											"Unsupported generic list type: " + listGenericType);
								}
							}
							else {
								throw new IllegalArgumentException("Unsupported field type: " + field.getType());
							}

						}
					}

					return (AssistantMessageContent) obj;
				}
				catch (Exception e) {
					throw new RuntimeException("Error while creating instance of " + clazz.getName(), e);
				}
			}
		}

		// Create instance of the class
		try {
			AssistantMessageContent obj = AssistantTextContentBlock.class.getDeclaredConstructor().newInstance();

			// Use reflection to manually set field values
			for (Field field : AssistantTextContentBlock.class.getDeclaredFields()) {
				field.setAccessible(true);
				// field.getName() gets the value from JsonProperty annotation above
				JsonProperty annotation = field.getAnnotation(JsonProperty.class);
				String name = null;
				if (annotation == null) {
					name = field.getName();
				}
				else {
					name = annotation.value();
				}
				if (node.has(name)) {
					// Set values based on field type, assuming fields are primitive types
					// or strings
					if (field.getType().equals(String.class)) {
						field.set(obj, node.get(name).asText());
					}
					else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
						field.set(obj, node.get(name).asInt());
					}
					else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
						field.set(obj, node.get(name).asBoolean());

					}
					else {
						// For other types, use ObjectMapper for direct conversion
						Object o = new ObjectMapper().treeToValue(node.get(name), field.getType());
						field.set(obj, o);
					}
				}
			}

			return obj;
		}
		catch (Exception e) {
			throw new RuntimeException("Error while creating instance of " + AssistantTextContentBlock.class.getName(),
					e);
		}

	}

}
