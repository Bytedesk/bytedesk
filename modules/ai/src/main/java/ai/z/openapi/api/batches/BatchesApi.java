package ai.z.openapi.api.batches;

import ai.z.openapi.service.batches.Batch;
import ai.z.openapi.service.batches.BatchCreateParams;
import ai.z.openapi.service.batches.BatchPage;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Batch Processing API for large-scale data operations Enables efficient processing of
 * millions of requests with cost-effective batch execution Provides up to 50% discount
 * compared to individual API calls with 24-hour completion guarantee Supports up to
 * 50,000 requests per batch with maximum 200MB input file size Ideal for evaluation,
 * classification, and embedding tasks that don't require immediate responses
 */
public interface BatchesApi {

	/**
	 * Create a new batch processing job Submits a large number of requests for efficient
	 * batch processing with JSONL format Each request must include a unique custom_id for
	 * result mapping Supports /v1/chat/completions and /v1/embeddings endpoints
	 * @param batchCreateParams Batch configuration including input file, endpoint, and
	 * processing options
	 * @return Batch job information with ID, status, and processing details
	 */
	@POST("batches")
	Single<Batch> batchesCreate(@Body BatchCreateParams batchCreateParams);

	/**
	 * Retrieve details of a specific batch job Gets comprehensive information about batch
	 * processing status and results Status can be: validating, failed, in_progress,
	 * finalizing, completed, expired, cancelling, cancelled
	 * @param batchId Unique identifier of the batch job to retrieve
	 * @return Batch job details including progress, completion status, output_file_id,
	 * and error_file_id
	 */
	@GET("batches/{batch_id}")
	Single<Batch> batchesRetrieve(@Path("batch_id") String batchId);

	/**
	 * List all batch jobs with pagination Retrieves a paginated list of all batch
	 * processing jobs with filtering options Useful for monitoring multiple batch
	 * operations and their completion status
	 * @param after Cursor for pagination to get batches after this point
	 * @param limit Maximum number of batch jobs to return per page (default: 20)
	 * @return Paginated list of batch jobs with status, request counts, and metadata
	 */
	@GET("batches")
	Single<BatchPage> batchesList(@Query("after") String after, @Query("limit") Integer limit);

	/**
	 * Cancel a running batch job Stops the batch processing and marks the job as
	 * cancelled (may take up to 10 minutes) Only works for batches in 'validating' or
	 * 'in_progress' status
	 * @param batchId Unique identifier of the batch job to cancel
	 * @return Updated batch job status after cancellation with final request counts
	 */
	@POST("batches/{batch_id}/cancel")
	Single<Batch> batchesCancel(@Path("batch_id") String batchId);

}
