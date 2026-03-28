package ai.z.openapi.utils;

import io.reactivex.rxjava3.core.Single;

/**
 * Functional interface for supplying single request operations using RxJava. This
 * interface defines a contract for asynchronous operations that take a parameter and
 * return a Single<Data> for reactive programming.
 *
 * @param <Param> the type of input parameter
 * @param <Data> the type of data wrapped in Single
 */
@FunctionalInterface
public interface RequestSupplier<Param, Data> {

	/**
	 * Executes an asynchronous operation with the given parameters. Returns a Single that
	 * emits exactly one item or an error.
	 * @param params the input parameters for the operation
	 * @return a Single that emits the result data or an error
	 */
	Single<Data> get(Param params);

}
