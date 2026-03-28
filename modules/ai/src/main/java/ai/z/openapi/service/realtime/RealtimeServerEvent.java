package ai.z.openapi.service.realtime;

import ai.z.openapi.service.realtime.server.ConversationCreated;
import ai.z.openapi.service.realtime.server.ConversationItemCreated;
import ai.z.openapi.service.realtime.server.ConversationItemDeleted;
import ai.z.openapi.service.realtime.server.ConversationItemInputAudioTranscriptionCompleted;
import ai.z.openapi.service.realtime.server.ConversationItemInputAudioTranscriptionFailed;
import ai.z.openapi.service.realtime.server.ConversationItemTruncated;
import ai.z.openapi.service.realtime.server.InputAudioBufferCleared;
import ai.z.openapi.service.realtime.server.InputAudioBufferCommitted;
import ai.z.openapi.service.realtime.server.InputAudioBufferSpeechStarted;
import ai.z.openapi.service.realtime.server.InputAudioBufferSpeechStopped;
import ai.z.openapi.service.realtime.server.RateLimitsUpdated;
import ai.z.openapi.service.realtime.server.RealtimeError;
import ai.z.openapi.service.realtime.server.RealtimeHeartBeat;
import ai.z.openapi.service.realtime.server.ResponseAudioDelta;
import ai.z.openapi.service.realtime.server.ResponseAudioDone;
import ai.z.openapi.service.realtime.server.ResponseAudioTranscriptDelta;
import ai.z.openapi.service.realtime.server.ResponseAudioTranscriptDone;
import ai.z.openapi.service.realtime.server.ResponseContentPartAdded;
import ai.z.openapi.service.realtime.server.ResponseContentPartDone;
import ai.z.openapi.service.realtime.server.ResponseCreated;
import ai.z.openapi.service.realtime.server.ResponseDone;
import ai.z.openapi.service.realtime.server.ResponseFunctionCallArgumentsDelta;
import ai.z.openapi.service.realtime.server.ResponseFunctionCallArgumentsDone;
import ai.z.openapi.service.realtime.server.ResponseOutputItemAdded;
import ai.z.openapi.service.realtime.server.ResponseOutputItemDone;
import ai.z.openapi.service.realtime.server.ResponseTextDelta;
import ai.z.openapi.service.realtime.server.ResponseTextDone;
import ai.z.openapi.service.realtime.server.SessionCreated;
import ai.z.openapi.service.realtime.server.SessionUpdated;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all realtime server events received from the server. This class defines
 * the common structure and properties for server-side events in the realtime
 * communication protocol, including conversation updates, audio processing events,
 * response generation, and session management.
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
        // Server conversation.created event is returned immediately after session creation. A conversation is created for each session.
        @JsonSubTypes.Type(value = ConversationCreated.class, name = "conversation.created"),
        // Server conversation.item.created event is returned when a conversation item is created.
        @JsonSubTypes.Type(value = ConversationItemCreated.class, name = "conversation.item.created"),
        // Server conversation.item.deleted event is returned when client deletes an item in conversation via conversation.item.delete event.
        @JsonSubTypes.Type(value = ConversationItemDeleted.class, name = "conversation.item.deleted"),
        // Server conversation.item.input_audio_transcription.completed event is the result of speech-to-text from audio buffer.
        @JsonSubTypes.Type(value = ConversationItemInputAudioTranscriptionCompleted.class, name = "conversation.item.input_audio_transcription.completed"),
        // Server conversation.item.input_audio_transcription.failed event is returned when input audio transcription is enabled but transcription request for user message fails.
        @JsonSubTypes.Type(value = ConversationItemInputAudioTranscriptionFailed.class, name = "conversation.item.input_audio_transcription.failed"),
        // Server conversation.item.truncated event is returned when client truncates previous assistant audio message item via conversation.item.truncate event.
        @JsonSubTypes.Type(value = ConversationItemTruncated.class, name = "conversation.item.truncated"),
        // Server error event is returned when an error occurs, which could be client or server issue.
        @JsonSubTypes.Type(value = RealtimeError.class, name = "error"),
        // Server heartbeat custom event.
        @JsonSubTypes.Type(value = RealtimeHeartBeat.class, name = "heartbeat"),
        // Server input_audio_buffer.cleared event is returned when client clears input audio buffer via input_audio_buffer.clear event.
        @JsonSubTypes.Type(value = InputAudioBufferCleared.class, name = "input_audio_buffer.cleared"),
        // Server input_audio_buffer.committed event is returned when input audio buffer is committed, which can be initiated by client or automatically completed in server VAD mode.
        @JsonSubTypes.Type(value = InputAudioBufferCommitted.class, name = "input_audio_buffer.committed"),
        // Server input_audio_buffer.speech_started event is returned when speech is detected in audio buffer in server VAD mode.
        @JsonSubTypes.Type(value = InputAudioBufferSpeechStarted.class, name = "input_audio_buffer.speech_started"),
        // Server input_audio_buffer.speech_stopped event is returned when speech end is detected in audio buffer in server VAD mode.
        @JsonSubTypes.Type(value = InputAudioBufferSpeechStopped.class, name = "input_audio_buffer.speech_stopped"),
        // Server rate_limits.updated event is sent at response start to indicate updated rate limits.
        @JsonSubTypes.Type(value = RateLimitsUpdated.class, name = "rate_limits.updated"),
        // Server response.audio.delta event is returned when model-generated audio updates.
        @JsonSubTypes.Type(value = ResponseAudioDelta.class, name = "response.audio.delta"),
        // Server response.audio.done event is returned when model-generated audio is complete.
        @JsonSubTypes.Type(value = ResponseAudioDone.class, name = "response.audio.done"),
        // Server response.audio_transcript.delta event is returned when model-generated audio output transcription content updates.
        @JsonSubTypes.Type(value = ResponseAudioTranscriptDelta.class, name = "response.audio_transcript.delta"),
        // Server response.audio_transcript.done event is returned when model-generated audio output transcription stream ends.
        @JsonSubTypes.Type(value = ResponseAudioTranscriptDone.class, name = "response.audio_transcript.done"),
        // Server response.content_part.added event is returned when new content part is added to assistant message item.
        @JsonSubTypes.Type(value = ResponseContentPartAdded.class, name = "response.content_part.added"),
        // Server response.content_part.done event is returned when content part stream ends.
        @JsonSubTypes.Type(value = ResponseContentPartDone.class, name = "response.content_part.done"),
        // Server response.created event is returned when new response is created. This is the first event of response creation, with response in initial in_progress state.
        @JsonSubTypes.Type(value = ResponseCreated.class, name = "response.created"),
        // Server response.done event is returned when response stream ends.
        @JsonSubTypes.Type(value = ResponseDone.class, name = "response.done"),
        // Server response.function_call_arguments.delta event is returned when model-generated function call arguments update.
        @JsonSubTypes.Type(value = ResponseFunctionCallArgumentsDelta.class, name = "response.function_call_arguments.delta"),
        // Server response.function_call_arguments.done event is returned when model-generated function call arguments stream ends.
        @JsonSubTypes.Type(value = ResponseFunctionCallArgumentsDone.class, name = "response.function_call_arguments.done"),
        // Server response.output_item.added event is returned when new item is created during response generation.
        @JsonSubTypes.Type(value = ResponseOutputItemAdded.class, name = "response.output_item.added"),
        // Server response.output_item.done event is returned when item stream ends.
        @JsonSubTypes.Type(value = ResponseOutputItemDone.class, name = "response.output_item.done"),
        // Server response.text.delta event is returned when model-generated text updates.
        @JsonSubTypes.Type(value = ResponseTextDelta.class, name = "response.text.delta"),
        // Server response.text.done event is returned when model-generated text stream ends.
        @JsonSubTypes.Type(value = ResponseTextDone.class, name = "response.text.done"),
        // Server session.created event is the first server event when establishing new connection with realtime API. This event creates and returns a new session with default session configuration.
        @JsonSubTypes.Type(value = SessionCreated.class, name = "session.created"),
        // Server session.updated event is returned when client updates session. If error occurs, server will return error event.
        @JsonSubTypes.Type(value = SessionUpdated.class, name = "session.updated")
})
// @formatter:on
public class RealtimeServerEvent {

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

	public RealtimeServerEvent() {
		this.eventId = "";
		this.type = "";
		// Private fields
		this.clientTimestamp = 0L;
	}

}