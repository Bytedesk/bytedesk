package ai.z.openapi.utils;

/**
 * Functional interface for supplying flowable request operations. This interface defines
 * a contract for operations that take a parameter and return data that can be processed
 * in a reactive stream.
 *
 * @param <Param> the type of input parameter
 * @param <Data> the type of data returned by the operation
 */
@FunctionalInterface
public interface FlowableRequestSupplier<Param, Data> {

	/**
	 * Executes the operation with the given parameters and returns the result. This
	 * method is designed to be used in reactive programming contexts where the returned
	 * data can be processed as a stream.
	 * @param params the input parameters for the operation
	 * @return the data result of the operation
	 * @throws RuntimeException if the operation fails
	 */
	Data get(Param params);

}
