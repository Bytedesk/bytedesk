package ai.z.openapi.service.assistant.query_support;

import com.fasterxml.jackson.annotation.JsonProperty;
import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Parameters for querying assistant support status. This class contains the parameters
 * needed to query the support status of specific assistants.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistantQuerySupportParams extends CommonRequest implements ClientRequest<AssistantQuerySupportParams> {

	/**
	 * List of assistant IDs to query support status for.
	 */
	@JsonProperty("assistant_id_list")
	private List<String> assistantIdList;

}
