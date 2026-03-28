package ai.z.openapi.service.layoutparsing;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LayoutParsingCreateParams extends CommonRequest implements ClientRequest<LayoutParsingCreateParams> {

	private String model;

	private String file;

	@JsonProperty("return_crop_images")
	private Boolean returnCropImages;

	@JsonProperty("need_layout_visualization")
	private Boolean needLayoutVisualization;

	@JsonProperty("start_page_id")
	private Integer startPageId;

	@JsonProperty("end_page_id")
	private Integer endPageId;

}
