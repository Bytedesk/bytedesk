package ai.z.openapi.service.web_reader;

/**
 * Web reader service interface
 */
public interface WebReaderService {

	/**
	 * Creates a web reader request to parse a URL.
	 * @param request the web reader request
	 * @return WebReaderResponse containing the reader result
	 */
	WebReaderResponse createWebReader(WebReaderRequest request);

}