package ai.z.openapi.service.web_reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebReaderResult {

	@JsonProperty("reader_result")
	private ReaderData readerResult;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class ReaderData {

		private Map<String, String> images;

		private Map<String, String> links;

		private String title;

		private String description;

		private String url;

		private String content;

		private String publishedTime;

		private Map<String, Object> metadata;

		private Map<String, Object> external;

	}

}
