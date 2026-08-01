package ai.z.openapi.service.audio;

/**
 * Audio service interface
 */
public interface AudioService {

	/**
	 * Creates speech from text using text-to-speech.
	 * @param request the speech generation request
	 * @return AudioSpeechApiResponse containing the generated speech
	 */
	AudioSpeechResponse createSpeech(AudioSpeechRequest request);

	/**
	 * Creates speech from text using text-to-speech.
	 * @param request the speech generation request
	 * @return AudioSpeechStreamingResponse containing the generated speech streaming
	 * &#064;Deprecated This method is deprecated and will be removed in a future release.
	 * Please use createStreamingSpeech instead.
	 */
	@Deprecated
	AudioSpeechStreamingResponse createStreamingSpeechStreaming(AudioSpeechRequest request);

	/**
	 * Creates speech from text using text-to-speech. 该方法接收一个音频语音请求对象，并返回一个流式响应对象
	 * @param request the speech generation request
	 * @return AudioSpeechStreamingResponse containing the generated speech streaming
	 */
	AudioSpeechStreamingResponse createStreamingSpeech(AudioSpeechRequest request);

	/**
	 * Creates customized speech with specific voice characteristics.
	 * @param request the speech customization request
	 * @return AudioCustomizationResponse containing the customized speech result
	 */
	AudioCustomizationResponse createCustomSpeech(AudioCustomizationRequest request);

	/**
	 * Creates audio transcription from audio files.
	 * @param request the transcription request
	 * @return AudioTranscriptionResponse containing the transcription result
	 */
	AudioTranscriptionResponse createTranscription(AudioTranscriptionRequest request);

}