package ai.z.openapi.service.image;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import lombok.NoArgsConstructor;

/**
 * An object with a list of image results.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResult {

	/**
	 * The creation time in epoch seconds.
	 */
	@JsonProperty("created")
	Long created;

	/**
	 * List of image results.
	 */
	@JsonProperty("data")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	List<Image> data;

}
