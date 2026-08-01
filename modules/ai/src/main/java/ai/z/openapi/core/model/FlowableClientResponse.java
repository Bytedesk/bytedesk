package ai.z.openapi.core.model;

/**
 * Simplified client response with a Flowable stream where the body type and stream item
 * type are the same.
 *
 * @param <T> response data type
 */
public interface FlowableClientResponse<T> extends BiFlowableClientResponse<T, T> {

	// No additional methods needed

}
