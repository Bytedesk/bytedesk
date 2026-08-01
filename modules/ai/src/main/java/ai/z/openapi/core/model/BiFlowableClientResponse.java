package ai.z.openapi.core.model;

import io.reactivex.rxjava3.core.Flowable;

/**
 * A client response that supports a reactive Flowable stream with separate body type and
 * stream item type.
 *
 * @param <T> Response body type
 * @param <F> Flowable stream item type
 */
public interface BiFlowableClientResponse<T, F> extends ClientResponse<T> {

	void setFlowable(Flowable<F> stream);

	Flowable<F> getFlowable();

}
