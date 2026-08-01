package ai.z.openapi.service.layoutparsing;

import ai.z.openapi.service.model.Usage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayoutParsingResult {

	private String id;

	private Long created;

	private String model;

	@JsonProperty("md_results")
	private String mdResults;

	@JsonProperty("layout_details")
	private List<List<LayoutDetail>> layoutDetails;

	@JsonProperty("layout_visualization")
	private List<String> layoutVisualization;

	@JsonProperty("data_info")
	private DataInfo dataInfo;

	@JsonProperty("request_id")
	private String requestId;

	private Usage usage;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class LayoutDetail {

		private Integer index;

		private String label;

		@JsonProperty("bbox_2d")
		private Object bbox2dRaw;

		private List<Integer> bbox2d;

		private String content;

		private Integer height;

		private Integer width;

		@JsonProperty("bbox_2d")
		public void setBbox2dRaw(Object bbox2dRaw) {
			this.bbox2dRaw = bbox2dRaw;
			this.bbox2d = convertBbox2d(bbox2dRaw);
		}

		private List<Integer> convertBbox2d(Object bbox2dRaw) {
			List<Integer> result = new ArrayList<>();
			if (bbox2dRaw == null) {
				return result;
			}

			try {
				if (bbox2dRaw instanceof List) {
					List<?> rawList = (List<?>) bbox2dRaw;
					if (!rawList.isEmpty() && rawList.get(0) instanceof List) {
						// Handle nested array: [[x1, y1, x2, y2]]
						List<?> innerList = (List<?>) rawList.get(0);
						for (Object item : innerList) {
							if (item instanceof Number) {
								result.add(((Number) item).intValue());
							}
						}
					}
					else {
						// Handle flat array: [x1, y1, x2, y2]
						for (Object item : rawList) {
							if (item instanceof Number) {
								result.add(((Number) item).intValue());
							}
						}
					}
				}
			}
			catch (Exception e) {
				// Log error if needed, but return empty list to avoid breaking the
				// parsing
			}

			return result;
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DataInfo {

		@JsonProperty("num_pages")
		private Integer numPages;

		private List<PageInfo> pages;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PageInfo {

		private Integer width;

		private Integer height;

	}

}
