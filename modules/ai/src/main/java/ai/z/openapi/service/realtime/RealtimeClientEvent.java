package ai.z.openapi.service.realtime;

import ai.z.openapi.service.realtime.client.ConversationItemCreate;
import ai.z.openapi.service.realtime.client.ConversationItemDelete;
import ai.z.openapi.service.realtime.client.ConversationItemTruncate;
import ai.z.openapi.service.realtime.client.InputAudioBufferAppend;
import ai.z.openapi.service.realtime.client.InputAudioBufferClear;
import ai.z.openapi.service.realtime.client.InputAudioBufferCommit;
import ai.z.openapi.service.realtime.client.InputAudioBufferPreCommit;
import ai.z.openapi.service.realtime.client.InputVideoBufferAppend;
import ai.z.openapi.service.realtime.client.ResponseCancel;
import ai.z.openapi.service.realtime.client.ResponseCreate;
import ai.z.openapi.service.realtime.client.SessionUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all realtime client events sent to the server. This class defines the
 * common structure and properties for client-side events in the realtime communication
 * protocol, including conversation management, audio buffer operations, and response
 * control.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
// @formatter:off
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = RealtimeServerEvent.class
)
@JsonSubTypes({
        // Event for handling client adding new items (messages, function calls and their responses) to the conversation context
        @JsonSubTypes.Type(value = ConversationItemCreate.class, name = "conversation.item.create"),
        // Event for handling deletion of an item from conversation history
        @JsonSubTypes.Type(value = ConversationItemDelete.class, name = "conversation.item.delete"),
        // Event for handling truncation of audio parts in previous assistant messages
        @JsonSubTypes.Type(value = ConversationItemTruncate.class, name = "conversation.item.truncate"),
        // Event for appending audio bytes to the input audio buffer
        @JsonSubTypes.Type(value = InputAudioBufferAppend.class, name = "input_audio_buffer.append"),
        // Event for clearing audio bytes in the buffer
        @JsonSubTypes.Type(value = InputAudioBufferClear.class, name = "input_audio_buffer.clear"),
        // Event for committing user's input audio buffer for processing
        @JsonSubTypes.Type(value = InputAudioBufferCommit.class, name = "input_audio_buffer.commit"),
        // Custom event
        @JsonSubTypes.Type(value = InputAudioBufferPreCommit.class, name = "input_audio_buffer.pre_commit"),
        // Custom event
        @JsonSubTypes.Type(value = InputVideoBufferAppend.class, name = "input_audio_buffer.append_video_frame"),
        // Event for canceling ongoing response processing
        @JsonSubTypes.Type(value = ResponseCancel.class, name = "response.cancel"),
        // Event indicating server to create response through model inference
        @JsonSubTypes.Type(value = ResponseCreate.class, name = "response.create"),
        // Event for updating session default configuration
        @JsonSubTypes.Type(value = SessionUpdate.class, name = "session.update")
})
// @formatter:on
public class RealtimeClientEvent {

	/**
	 * Unique identifier for this event.
	 */
	@JsonProperty("event_id")
	private String eventId;

	/**
	 * Type of the event, used for polymorphic deserialization.
	 */
	@JsonProperty("type")
	private String type;

	/**
	 * Timestamp when the event was created on the client side.
	 */
	@JsonProperty("client_timestamp")
	private Long clientTimestamp;

	public RealtimeClientEvent() {
		this.eventId = "";
		this.type = "";
		this.clientTimestamp = System.currentTimeMillis();
	}

}
