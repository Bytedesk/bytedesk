package ai.z.openapi.service.batches;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.batches.BatchesApi;
import ai.z.openapi.service.file.QueryBatchRequest;
import ai.z.openapi.utils.RequestSupplier;

/**
 * Batch service implementation
 */
public class BatchServiceImpl implements BatchService {

	private final AbstractAiClient zAiClient;

	private final BatchesApi batchesApi;

	public BatchServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.batchesApi = zAiClient.retrofit().create(BatchesApi.class);
	}

	@Override
	public BatchResponse createBatch(BatchCreateParams batchCreateParams) {
		validateCreateBatchParams(batchCreateParams);
		RequestSupplier<BatchCreateParams, Batch> supplier = batchesApi::batchesCreate;
		return this.zAiClient.executeRequest(batchCreateParams, supplier, BatchResponse.class);
	}

	@Override
	public BatchResponse retrieveBatch(BatchRequest request) {
		validateBatchRequest(request);
		RequestSupplier<BatchRequest, Batch> supplier = (params) -> batchesApi.batchesRetrieve(params.getBatchId());
		return this.zAiClient.executeRequest(request, supplier, BatchResponse.class);
	}

	@Override
	public BatchResponse retrieveBatch(String batchId) {
		validateBatchId(batchId);
		BatchRequest request = BatchRequest.builder().batchId(batchId).build();
		return retrieveBatch(request);
	}

	@Override
	public QueryBatchResponse listBatches(QueryBatchRequest queryBatchRequest) {
		validateQueryBatchRequest(queryBatchRequest);
		RequestSupplier<QueryBatchRequest, BatchPage> supplier = (params) -> batchesApi.batchesList(params.getAfter(),
				params.getLimit());
		return this.zAiClient.executeRequest(queryBatchRequest, supplier, QueryBatchResponse.class);
	}

	@Override
	public BatchResponse cancelBatch(BatchRequest request) {
		validateBatchRequest(request);
		RequestSupplier<BatchRequest, Batch> supplier = (params) -> batchesApi.batchesCancel(params.getBatchId());
		return this.zAiClient.executeRequest(request, supplier, BatchResponse.class);
	}

	@Override
	public BatchResponse cancelBatch(String batchId) {
		validateBatchId(batchId);
		BatchRequest request = BatchRequest.builder().batchId(batchId).build();
		return cancelBatch(request);
	}

	/**
	 * Validates BatchCreateParams parameters
	 * @param batchCreateParams the batch creation parameters to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateCreateBatchParams(BatchCreateParams batchCreateParams) {
		if (batchCreateParams == null) {
			throw new IllegalArgumentException("BatchCreateParams cannot be null");
		}
		if (batchCreateParams.getEndpoint() == null || batchCreateParams.getEndpoint().trim().isEmpty()) {
			throw new IllegalArgumentException("Endpoint cannot be null or empty");
		}
		if (batchCreateParams.getInputFileId() == null || batchCreateParams.getInputFileId().trim().isEmpty()) {
			throw new IllegalArgumentException("Input file ID cannot be null or empty");
		}
		if (batchCreateParams.getCompletionWindow() == null
				|| batchCreateParams.getCompletionWindow().trim().isEmpty()) {
			throw new IllegalArgumentException("Completion window cannot be null or empty");
		}
	}

	/**
	 * Validates BatchRequest parameters
	 * @param request the batch request to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateBatchRequest(BatchRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("BatchRequest cannot be null");
		}
		if (request.getBatchId() == null || request.getBatchId().trim().isEmpty()) {
			throw new IllegalArgumentException("Batch ID cannot be null or empty");
		}
	}

	/**
	 * Validates batch ID parameter
	 * @param batchId the batch ID to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateBatchId(String batchId) {
		if (batchId == null || batchId.trim().isEmpty()) {
			throw new IllegalArgumentException("Batch ID cannot be null or empty");
		}
	}

	/**
	 * Validates QueryBatchRequest parameters
	 * @param queryBatchRequest the query batch request to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateQueryBatchRequest(QueryBatchRequest queryBatchRequest) {
		if (queryBatchRequest == null) {
			throw new IllegalArgumentException("QueryBatchRequest cannot be null");
		}
	}

}