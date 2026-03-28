package ai.z.openapi.core;

/**
 * Constants class containing all the configuration values and model identifiers used
 * throughout the Z.AI OpenAPI SDK.
 * <p>
 * This class provides centralized access to: - API base URLs - Model identifiers for
 * different AI capabilities - Invocation method constants
 */
public final class Constants {

	// Private constructor to prevent instantiation
	private Constants() {
		throw new UnsupportedOperationException("Constants class cannot be instantiated");
	}

	// =============================================================================
	// API Configuration
	// =============================================================================

	/**
	 * Base URL for the ZHIPU AI OpenAPI service. All API requests will be made to
	 * endpoints under this base URL.
	 */
	public static final String ZHIPU_AI_BASE_URL = "https://open.bigmodel.cn/api/paas/v4/";

	/**
	 * Base URL for the Z.AI OpenAPI service. All API requests will be made to endpoints
	 * under this base URL.
	 */
	public static final String Z_AI_BASE_URL = "https://api.z.ai/api/paas/v4/";

	// =============================================================================
	// Text Generation Models
	// =============================================================================
	/**
	 * GLM-4.7 model code
	 */
	public static final String ModelGLM4_7 = "glm-4.7";

	/**
	 * GLM-4.6 model code
	 */
	public static final String ModelGLM4_6 = "glm-4.6";

	/**
	 * GLM-4.6v model code
	 */
	public static final String ModelGLM4_6V = "glm-4.6v";

	/**
	 * GLM-4.6v flash model code
	 */
	public static final String ModelGLM4_6V_FLASH = "glm-4.6v-flash";

	/**
	 * GLM-4.6-air model code
	 */
	public static final String ModelGLM4_6_AIR = "glm-4.6-air";

	/**
	 * GLM-4.6-flash model code
	 */
	public static final String ModelGLM4_6_FLASH = "glm-4.6-flash";

	/**
	 * GLM-4.5 model code
	 */
	public static final String ModelChatGLM4_5 = "glm-4.5";

	/**
	 * GLM-4.5V model code
	 */
	public static final String ModelChatGLM4_5V = "glm-4.5v";

	/**
	 * GLM-4.5-Air model code
	 */
	public static final String ModelChatGLM4_5_AIR = "glm-4.5-air";

	/**
	 * GLM-4.5-X model code
	 */
	public static final String ModelChatGLM4_5_X = "glm-4.5-x";

	/**
	 * GLM-4.5-AirX model code
	 */
	public static final String ModelChatGLM4_5_AIRX = "glm-4.5-airx";

	/**
	 * GLM-4 Plus model - Enhanced version with improved capabilities.
	 */
	public static final String ModelChatGLM4Plus = "glm-4-plus";

	/**
	 * GLM-4 Air model - Lightweight version optimized for speed.
	 */
	public static final String ModelChatGLM4Air = "glm-4-air";

	/**
	 * GLM-4 Flash model - Ultra-fast response model.
	 */
	public static final String ModelChatGLM4Flash = "glm-4-flash";

	/**
	 * GLM-4 standard model - Balanced performance and capability.
	 */
	public static final String ModelChatGLM4 = "glm-4";

	/**
	 * GLM-4 model version 0520 - Specific version release.
	 */
	public static final String ModelChatGLM40520 = "glm-4-0520";

	/**
	 * GLM-4 AirX model - Extended Air model with additional features.
	 */
	public static final String ModelChatGLM4Airx = "glm-4-airx";

	/**
	 * GLM-4 Long model - Optimized for long-context conversations.
	 */
	public static final String ModelChatGLMLong = "glm-4-long";

	/**
	 * GLM-4 Voice model - Specialized for voice-related tasks.
	 */
	public static final String ModelChatGLM4Voice = "glm-4-voice";

	/**
	 * GLM-4.1V Thinking Flash model - Visual reasoning model with thinking capabilities.
	 */
	public static final String ModelChatGLM41VThinkingFlash = "glm-4.1v-thinking-flash";

	/**
	 * GLM-Z1 Air model - Optimized for mathematical and logical reasoning.
	 */
	public static final String ModelChatGLMZ1Air = "glm-z1-air";

	/**
	 * GLM-Z1 AirX model - Fastest domestic inference model with 200 tokens/s.
	 */
	public static final String ModelChatGLMZ1AirX = "glm-z1-airx";

	/**
	 * GLM-Z1 Flash model - Completely free reasoning model service.
	 */
	public static final String ModelChatGLMZ1Flash = "glm-z1-flash";

	/**
	 * GLM-4 Air 250414 model - Enhanced with reinforcement learning optimization.
	 */
	public static final String ModelChatGLM4Air250414 = "glm-4-air-250414";

	/**
	 * GLM-4 Flash 250414 model - Latest free language model.
	 */
	public static final String ModelChatGLM4Flash250414 = "glm-4-flash-250414";

	/**
	 * GLM-4 FlashX model - Enhanced Flash version with ultra-fast inference speed.
	 */
	public static final String ModelChatGLM4FlashX = "glm-4-flashx";

	/**
	 * GLM-4 9B model - Open-source model with 9 billion parameters.
	 */
	public static final String ModelChatGLM49B = "glm-4-9b";

	/**
	 * GLM-4 Assistant model - AI assistant for various business scenarios.
	 */
	public static final String ModelChatGLM4Assistant = "glm-4-assistant";

	/**
	 * GLM-4 AllTools model - Agent model for complex task planning and execution.
	 */
	public static final String ModelChatGLM4AllTools = "glm-4-alltools";

	/**
	 * ChatGLM3 6B model - Open-source base model with 6 billion parameters.
	 */
	public static final String ModelChatGLM36B = "chatglm3-6b";

	/**
	 * CodeGeeX-4 model - Code generation and completion model.
	 */
	public static final String ModelCodeGeeX4 = "codegeex-4";

	// =============================================================================
	// Audio Speech Recognition Models
	// =============================================================================

	/**
	 * GLM-ASR model - Context-aware audio transcription model that converts audio to
	 * fluent and readable text. Supports Chinese, English, and various Chinese dialects.
	 * Improved performance in noisy environments.
	 */
	public static final String ModelGLMASR = "glm-asr-2512";

	// =============================================================================
	// Real-time Interaction Models
	// =============================================================================

	/**
	 * GLM-Realtime Air model - Real-time video call model with cross-modal reasoning
	 * capabilities across text, audio, and video. Supports real-time interruption.
	 */
	public static final String ModelGLMRealtimeAir = "glm-realtime-air";

	/**
	 * GLM-Realtime Flash model - Fast real-time video call model with cross-modal
	 * reasoning capabilities. Supports camera interaction and screen sharing.
	 */
	public static final String ModelGLMRealtimeFlash = "glm-realtime-flash";

	// =============================================================================
	// Vision Models (Image Understanding)
	// =============================================================================

	/**
	 * GLM-4V Plus model - Enhanced vision model for image understanding.
	 */
	public static final String ModelChatGLM4VPlus = "glm-4v-plus";

	/**
	 * GLM-4V standard model - Standard vision model for image analysis.
	 */
	public static final String ModelChatGLM4V = "glm-4v";

	/**
	 * GLM-4V Plus 0111 model - Variable resolution video and image understanding.
	 */
	public static final String ModelChatGLM4VPlus0111 = "glm-4v-plus-0111";

	/**
	 * GLM-4.1V-Thinking-FlashX Variable resolution video and image understanding.
	 */
	public static final String ModelChatGLM4ThinkingFlashX = "glm-4.1v-thinking-flashx";

	/**
	 * GLM-4V Flash model - Free and powerful image understanding model.
	 */
	public static final String ModelChatGLM4VFlash = "glm-4v-flash";

	// =============================================================================
	// Image Generation Models
	// =============================================================================

	/**
	 * CogView-3 Plus model - Enhanced image generation capabilities.
	 */
	public static final String ModelCogView3Plus = "cogview-3-plus";

	/**
	 * CogView-3 standard model - Standard image generation model.
	 */
	public static final String ModelCogView3 = "cogview-3";

	/**
	 * CogView-3 Flash model - Free image generation model.
	 */
	public static final String ModelCogView3Flash = "cogview-3-flash";

	/**
	 * CogView-4 250304 model - Advanced image generation with text capabilities.
	 */
	public static final String ModelCogView4250304 = "cogview-4-250304";

	/**
	 * CogView-4 model - Advanced image generation for precise and personalized AI image
	 * expression.
	 */
	public static final String ModelCogView4 = "cogview-4";

	// =============================================================================
	// Video Generation Models
	// =============================================================================

	/**
	 * CogVideoX model - Video generation from text or images.
	 */
	public static final String ModelCogVideoX = "cogvideox";

	/**
	 * CogVideoX Flash model - Free video generation model.
	 */
	public static final String ModelCogVideoXFlash = "cogvideox-flash";

	/**
	 * CogVideoX-2 model - New video generation model.
	 */
	public static final String ModelCogVideoX2 = "cogvideox-2";

	/**
	 * CogVideoX-3 model
	 */
	public static final String ModelCogVideoX3 = "cogvideox-3";

	/**
	 * Vidu Q1 Text model - High-performance video generation from text input. Supports
	 * general and anime styles.
	 */
	public static final String ModelViduQ1Text = "viduq1-text";

	/**
	 * Vidu Q1 Image model - Video generation from first frame image and text description.
	 */
	public static final String ModelViduQ1Image = "viduq1-image";

	/**
	 * Vidu Q1 Start-End model - Video generation from first and last frame images.
	 */
	public static final String ModelViduQ1StartEnd = "viduq1-start-end";

	/**
	 * Vidu 2 Image model - Enhanced video generation from first frame image and text
	 * description.
	 */
	public static final String ModelVidu2Image = "vidu2-image";

	/**
	 * Vidu 2 Start-End model - Enhanced video generation from first and last frame
	 * images.
	 */
	public static final String ModelVidu2StartEnd = "vidu2-start-end";

	/**
	 * Vidu 2 Reference model - Video generation with reference images of people, objects,
	 * etc.
	 */
	public static final String ModelVidu2Reference = "vidu2-reference";

	// =============================================================================
	// Embedding Models
	// =============================================================================

	/**
	 * Embedding model version 2 - Text embedding generation.
	 */
	public static final String ModelEmbedding2 = "embedding-2";

	/**
	 * Embedding model version 3 - Latest text embedding generation.
	 */
	public static final String ModelEmbedding3 = "embedding-3";

	// =============================================================================
	// Specialized Models
	// =============================================================================

	/**
	 * CharGLM-3 model - Anthropomorphic character interaction model.
	 */
	public static final String ModelCharGLM3 = "charglm-3";

	/**
	 * GLM-TTS model - Text-to-Speech synthesis model.
	 */
	public static final String ModelTTS = "glm-tts";

	/**
	 * Rerank model - Text reordering and relevance scoring.
	 */
	public static final String ModelRerank = "rerank";

}
