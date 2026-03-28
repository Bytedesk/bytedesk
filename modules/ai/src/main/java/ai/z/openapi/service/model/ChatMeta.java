package ai.z.openapi.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Meta information for hyper-realistic conversations. Contains role and user information
 * data where: - user_info: user information - bot_info: role/bot information - bot_name:
 * role/bot name - user_name: user name
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMeta {

	/**
	 * User information.
	 */
	@JsonProperty("user_info")
	private String userInfo;

	/**
	 * Bot/role information.
	 */
	@JsonProperty("bot_info")
	private String botInfo;

	/**
	 * Bot/role name.
	 */
	@JsonProperty("bot_name")
	private String botName;

	/**
	 * User name.
	 */
	@JsonProperty("user_name")
	private String userName;

}
