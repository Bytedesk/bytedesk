package ai.z.openapi.service.realtime.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ConversationObj {

	@JsonProperty("id")
	private String id;

	@JsonProperty("object")
	private String object;

}
