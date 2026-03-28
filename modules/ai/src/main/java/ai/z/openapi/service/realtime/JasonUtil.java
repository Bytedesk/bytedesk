package ai.z.openapi.service.realtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JSON serialization and deserialization of realtime events. This class
 * provides methods to convert between JSON strings and realtime event objects.
 */
public class JasonUtil {

	/**
	 * ObjectMapper configured for realtime event processing.
	 */
	private static final ObjectMapper objectMapper = new ObjectMapper() //
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //
		.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	/**
	 * Converts JSON string to RealtimeClientEvent object.
	 * @param json the JSON string to convert
	 * @return RealtimeClientEvent object or null if conversion fails
	 */
	public static RealtimeClientEvent fromJsonToClientEvent(String json) {
		try {
			return objectMapper.readValue(json, RealtimeClientEvent.class);
		}
		catch (JsonProcessingException e) {
			return null;
		}
	}

	/**
	 * Converts RealtimeClientEvent object to JSON string.
	 * @param event the RealtimeClientEvent object to convert
	 * @return JSON string or null if conversion fails
	 */
	public static String toJsonFromClientEvent(RealtimeClientEvent event) {
		try {
			return objectMapper.writeValueAsString(event);
		}
		catch (JsonProcessingException e) {
			return null;
		}
	}

	/**
	 * Converts JSON string to RealtimeServerEvent object.
	 * @param json the JSON string to convert
	 * @return RealtimeServerEvent object or null if conversion fails
	 */
	public static RealtimeServerEvent fromJsonToServerEvent(String json) {
		try {
			return objectMapper.readValue(json, RealtimeServerEvent.class);
		}
		catch (JsonProcessingException e) {
			return null;
		}
	}

	/**
	 * Converts RealtimeServerEvent object to JSON string.
	 * @param event the RealtimeServerEvent object to convert
	 * @return JSON string or null if conversion fails
	 */
	public static String toJsonFromServerEvent(RealtimeServerEvent event) {
		try {
			return objectMapper.writeValueAsString(event);
		}
		catch (JsonProcessingException e) {
			return null;
		}
	}

}