package ai.z.openapi.service.batches;

import ai.z.openapi.service.file.QueryBatchRequest;

/**
 * Batch service interface
 */
public interface BatchService {

	/**
	 * Creates a batch request.
	 * @param batchCreateParams the batch creation parameters
	 * @return BatchResponse containing the created batch details
	 */
	BatchResponse createBatch(BatchCreateParams batchCreateParams);

	/**
	 * Retrieves a specific batch.
	 * @param request the batch request containing the batch ID
	 * @return BatchResponse containing the batch details
	 */
	BatchResponse retrieveBatch(BatchRequest request);

	/**
	 * Retrieves a specific batch by ID.
	 * @param batchId the ID of the batch to retrieve
	 * @return BatchResponse containing the batch details
	 */
	BatchResponse retrieveBatch(String batchId);

	/**
	 * Lists all batches.
	 * @param queryBatchRequest the query parameters for listing batches
	 * @return QueryBatchResponse containing the list of batches
	 */
	QueryBatchResponse listBatches(QueryBatchRequest queryBatchRequest);

	/**
	 * Cancels a batch request.
	 * @param request the batch request containing the batch ID
	 * @return BatchResponse containing the cancellation result
	 */
	BatchResponse cancelBatch(BatchRequest request);

	/**
	 * Cancels a batch request by ID.
	 * @param batchId the ID of the batch to cancel
	 * @return BatchResponse containing the cancellation result
	 */
	BatchResponse cancelBatch(String batchId);

}