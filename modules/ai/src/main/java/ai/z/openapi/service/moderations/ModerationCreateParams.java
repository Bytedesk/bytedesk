package ai.z.openapi.service.moderations;

import ai.z.openapi.core.model.ClientRequest;
import ai.z.openapi.service.CommonRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Parameters for creating a moderation request to check content safety.
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ModerationCreateParams extends CommonRequest implements ClientRequest<ModerationCreateParams> {

	/**
	 * The model to use for moderation. Currently, supports "moderation".
	 */
	private String model;

	/**
	 * The input content to moderate. Can be text, image, video, or audio.
	 */
	private List<ModerationInput> input;

}