package ai.z.openapi.api.audio;

import ai.z.openapi.service.audio.AudioSpeechRequest;
import ai.z.openapi.service.audio.AudioTranscriptionResult;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;

import java.util.Map;

/**
 * Audio API for advanced speech processing capabilities Powered by GLM-4-Voice for
 * end-to-end speech understanding and generation Provides text-to-speech, speech-to-text,
 * voice customization, and real-time conversation Supports emotion control, tone
 * adjustment, speed variation, and dialect generation Features real-time audio
 * processing, voice cloning, and multilingual ASR capabilities
 */
public interface AudioApi {

	/**
	 * Text-to-Speech (TTS) conversion using GLM-4-Voice Converts text input into
	 * natural-sounding speech audio with emotion and tone control Supports multiple
	 * voices, languages, speed adjustment, and various audio formats Features advanced
	 * voice synthesis with customizable emotional expressions and dialects
	 * @param request TTS parameters including text, voice selection, emotion, speed,
	 * tone, and output format
	 * @return Generated high-quality audio content in specified format with natural
	 * prosody
	 */
	@POST("audio/speech")
	Single<ResponseBody> audioSpeech(@Body AudioSpeechRequest request);

	/**
	 * Text-to-Speech (TTS) conversion using GLM-4-Voice Converts text input into
	 * natural-sounding speech audio with emotion and tone control Supports multiple
	 * voices, languages, speed adjustment, and various audio formats Features advanced
	 * voice synthesis with customizable emotional expressions and dialects
	 * @param request TTS parameters including text, voice selection, emotion, speed,
	 * tone, and output format
	 * @return Generated high-quality audio streaming in specified format with natural
	 * prosody
	 */
	@Streaming
	@POST("audio/speech")
	Call<ResponseBody> audioSpeechStreaming(@Body AudioSpeechRequest request);

	/**
	 * Voice cloning and customization using advanced neural models Creates custom voice
	 * models from provided audio samples with high fidelity Enables personalized speech
	 * synthesis preserving unique voice characteristics and speaking style Supports
	 * fine-tuning of voice parameters including pitch, timbre, and speaking patterns
	 * @param request Voice customization parameters including model settings and training
	 * options
	 * @param voiceData High-quality audio file containing voice samples for cloning
	 * (recommended: clear, diverse samples)
	 * @return Voice model creation result with customization status and model performance
	 * metrics
	 */
	@Multipart
	@POST("audio/customization")
	Single<ResponseBody> audioCustomization(@PartMap Map<String, RequestBody> request,
			@Part MultipartBody.Part voiceData);

	/**
	 * Streaming speech-to-text transcription using GLM ASR Converts audio files to text
	 * with real-time streaming results and low latency Returns transcription results
	 * incrementally as they become available for immediate processing Optimized for
	 * real-time applications requiring immediate text feedback
	 * @param request Transcription parameters including language detection, model
	 * selection, and streaming settings
	 * @param file Audio file to be transcribed (supports various formats: wav, mp3, m4a,
	 * etc.)
	 * @return Streaming transcription results with timestamps and confidence scores
	 */
	@Streaming
	@POST("audio/transcriptions")
	@Multipart
	Call<ResponseBody> audioTranscriptionStream(@PartMap Map<String, RequestBody> request,
			@Part MultipartBody.Part file);

	/**
	 * Speech-to-text transcription using GLM ASR models Converts audio files to text with
	 * high accuracy and multilingual support Features advanced noise reduction, speaker
	 * recognition, and punctuation restoration Supports multiple languages with automatic
	 * language detection capabilities
	 * @param request Transcription parameters including language preference, model
	 * selection, and output format options
	 * @param file Audio file to be transcribed (supports wav, mp3, m4a, flac, and other
	 * common formats)
	 * @return Complete transcription result with text, timestamps, confidence scores, and
	 * speaker information
	 */
	@POST("audio/transcriptions")
	@Multipart
	Single<AudioTranscriptionResult> audioTranscription(@PartMap Map<String, RequestBody> request,
			@Part MultipartBody.Part file);

}
