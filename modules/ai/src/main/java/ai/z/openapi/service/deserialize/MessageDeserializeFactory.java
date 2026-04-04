package ai.z.openapi.service.deserialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ai.z.openapi.service.assistant.AssistantChoice;
import ai.z.openapi.service.deserialize.assistant.AssistantChoiceDeserializer;

public class MessageDeserializeFactory {

	public static ObjectMapper defaultObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		SimpleModule module = new SimpleModule();

		module.addDeserializer(AssistantChoice.class, new AssistantChoiceDeserializer());
		mapper.registerModule(module);

		return mapper;
	}

}
