package ai.z.openapi.service.moderations;

/**
 * Moderation service for content safety detection Provides content moderation
 * capabilities for text, image, audio, and video formats Accurately identifies risky
 * content including adult content, violence, illegal content, etc. Returns structured
 * moderation results including content type, risk type, and specific risk segments
 */
public interface ModerationService {

	/**
	 * Create a content moderation request for safety detection Analyzes content for
	 * potential risks including adult content, violence, illegal activities Supports
	 * multiple content types: text strings, images, audio, and video files Returns
	 * detailed risk assessment with structured results and specific risk segments
	 * @param request Moderation parameters including content to analyze (text string or
	 * multimedia object) Text: maximum 2000 characters Images: less than 10M, minimum
	 * resolution 20x20, maximum 6000x6000 Video: recommended duration 30 seconds Audio:
	 * recommended duration 60 seconds
	 * @return Moderation response with risk level (PASS/REVIEW/REJECT), content type,
	 * risk types, and processing time information
	 */
	ModerationResponse createModeration(ModerationCreateParams request);

}