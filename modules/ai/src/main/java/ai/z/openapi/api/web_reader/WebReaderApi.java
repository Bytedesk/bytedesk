package ai.z.openapi.api.web_reader;

import ai.z.openapi.service.web_reader.WebReaderRequest;
import ai.z.openapi.service.web_reader.WebReaderResult;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Web Reader API for reading and parsing web page content.
 */
public interface WebReaderApi {

	/**
	 * Read and parse content from a given URL.
	 * @param request reader parameters including url and options
	 * @return parsed content and metadata
	 */
	@POST("reader")
	Single<WebReaderResult> reader(@Body WebReaderRequest request);

}