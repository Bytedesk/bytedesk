package ai.z.openapi.service.assistant.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ai.z.openapi.service.deserialize.JsonTypeMapping;
import ai.z.openapi.service.deserialize.assistant.message.AssistantMessageContentDeserializer;

@JsonTypeMapping({ AssistantToolsDeltaBlock.class, AssistantTextContentBlock.class })
@JsonDeserialize(using = AssistantMessageContentDeserializer.class)
public abstract class AssistantMessageContent {

}
