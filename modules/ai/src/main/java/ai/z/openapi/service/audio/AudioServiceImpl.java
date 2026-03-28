package ai.z.openapi.service.audio;

import ai.z.openapi.AbstractAiClient;
import ai.z.openapi.api.audio.AudioApi;
import ai.z.openapi.service.deserialize.MessageDeserializeFactory;
import ai.z.openapi.service.model.ModelData;
import ai.z.openapi.utils.FlowableRequestSupplier;
import ai.z.openapi.utils.RequestSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Audio service implementation
 */
public class AudioServiceImpl implements AudioService {

	private static final Logger log = LoggerFactory.getLogger(AudioServiceImpl.class);

	protected static final ObjectMapper mapper = MessageDeserializeFactory.defaultObjectMapper();

	private final AbstractAiClient zAiClient;

	private final AudioApi audioApi;

	public AudioServiceImpl(AbstractAiClient zAiClient) {
		this.zAiClient = zAiClient;
		this.audioApi = zAiClient.retrofit().create(AudioApi.class);
	}

	@Override
	public AudioSpeechResponse createSpeech(AudioSpeechRequest request) {
		validateSpeechParams(request);
		RequestSupplier<AudioSpeechRequest, java.io.File> supplier = (params) -> {
			try {
				Single<ResponseBody> responseBody = audioApi.audioSpeech(params);
				Path tempDirectory = Files.createTempFile("audio_speech" + UUID.randomUUID(),
						"." + request.getResponseFormat());
				java.io.File file = tempDirectory.toFile();
				writeResponseBodyToFile(responseBody.blockingGet(), file);
				return Single.just(file);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
		return this.zAiClient.executeRequest(request, supplier, AudioSpeechResponse.class);
	}

	@Override
	@Deprecated
	public AudioSpeechStreamingResponse createStreamingSpeechStreaming(AudioSpeechRequest request) {
		validateSpeechParams(request);
		FlowableRequestSupplier<AudioSpeechRequest, retrofit2.Call<ResponseBody>> supplier = audioApi::audioSpeechStreaming;
		return this.zAiClient.streamRequest(request, supplier, AudioSpeechStreamingResponse.class, ModelData.class);
	}

	@Override
	public AudioSpeechStreamingResponse createStreamingSpeech(AudioSpeechRequest request) {
		validateSpeechParams(request);
		FlowableRequestSupplier<AudioSpeechRequest, retrofit2.Call<ResponseBody>> supplier = audioApi::audioSpeechStreaming;
		return this.zAiClient.streamRequest(request, supplier, AudioSpeechStreamingResponse.class, ModelData.class);
	}

	@Override
	public AudioCustomizationResponse createCustomSpeech(AudioCustomizationRequest request) {
		validateCustomSpeechParams(request);
		RequestSupplier<AudioCustomizationRequest, java.io.File> supplier = (params) -> {
			try {
				java.io.File voiceFile = params.getVoiceData();
				RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), voiceFile);
				MultipartBody.Part voiceData = MultipartBody.Part.createFormData("voice_data", voiceFile.getName(),
						requestFile);

				Map<String, RequestBody> requestMap = new HashMap<>();
				if (params.getInput() != null) {
					requestMap.put("input", RequestBody.create(MediaType.parse("text/plain"), params.getInput()));
				}
				if (params.getModel() != null) {
					requestMap.put("model", RequestBody.create(MediaType.parse("text/plain"), params.getModel()));
				}
				if (params.getVoiceText() != null) {
					requestMap.put("voice_text",
							RequestBody.create(MediaType.parse("text/plain"), params.getVoiceText()));
				}
				if (params.getResponseFormat() != null) {
					requestMap.put("response_format",
							RequestBody.create(MediaType.parse("text/plain"), params.getResponseFormat()));
				}
				if (params.getSensitiveWordCheck() != null) {
					try {
						String sensitiveWordCheckJson = mapper.writeValueAsString(params.getSensitiveWordCheck());
						requestMap.put("sensitive_word_check",
								RequestBody.create(MediaType.parse("application/json"), sensitiveWordCheckJson));
					}
					catch (Exception e) {
						log.error("Error serializing sensitive_word_check: {}", e.getMessage(), e);
					}
				}
				if (params.getRequestId() != null) {
					requestMap.put("request_id",
							RequestBody.create(MediaType.parse("text/plain"), params.getRequestId()));
				}
				if (params.getUserId() != null) {
					requestMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), params.getUserId()));
				}

				Single<ResponseBody> responseBody = audioApi.audioCustomization(requestMap, voiceData);
				Path tempDirectory = Files.createTempFile("audio_customization" + UUID.randomUUID(), ".wav");
				java.io.File file = tempDirectory.toFile();
				writeResponseBodyToFile(responseBody.blockingGet(), file);
				return Single.just(file);
			}
			catch (IOException e) {
				log.error("Error create custom speak: {}", e.getMessage(), e);
				throw new RuntimeException(e);
			}
		};
		return this.zAiClient.executeRequest(request, supplier, AudioCustomizationResponse.class);
	}

	@Override
	public AudioTranscriptionResponse createTranscription(AudioTranscriptionRequest request) {
		validateTranscriptionParams(request);
		if (request.getStream()) {
			return createTranscriptionStream(request);
		}
		else {
			return createTranscriptionBlock(request);
		}
	}

	private AudioTranscriptionResponse createTranscriptionStream(AudioTranscriptionRequest request) {
		FlowableRequestSupplier<AudioTranscriptionRequest, retrofit2.Call<ResponseBody>> supplier = params -> {
			java.io.File file = params.getFile();
			String contentType = detectContentType(file);
			RequestBody requestFile = RequestBody.create(MediaType.parse(contentType), file);
			MultipartBody.Part fileData = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

			Map<String, RequestBody> requestMap = new HashMap<>();
			if (params.getModel() != null) {
				requestMap.put("model", RequestBody.create(MediaType.parse("text/plain"), params.getModel()));
			}
			if (params.getStream() != null) {
				requestMap.put("stream",
						RequestBody.create(MediaType.parse("text/plain"), params.getStream().toString()));
			}
			if (params.getRequestId() != null) {
				requestMap.put("request_id", RequestBody.create(MediaType.parse("text/plain"), params.getRequestId()));
			}
			if (params.getUserId() != null) {
				requestMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), params.getUserId()));
			}

			return audioApi.audioTranscriptionStream(requestMap, fileData);
		};
		return this.zAiClient.biStreamRequest(request, supplier, AudioTranscriptionResponse.class,
				AudioTranscriptionChunk.class);
	}

	private AudioTranscriptionResponse createTranscriptionBlock(AudioTranscriptionRequest request) {
		RequestSupplier<AudioTranscriptionRequest, AudioTranscriptionResult> supplier = (params) -> {
			java.io.File file = params.getFile();
			String contentType = detectContentType(file);
			RequestBody requestFile = RequestBody.create(MediaType.parse(contentType), file);
			MultipartBody.Part fileData = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

			Map<String, RequestBody> requestMap = new HashMap<>();
			if (params.getModel() != null) {
				requestMap.put("model", RequestBody.create(MediaType.parse("text/plain"), params.getModel()));
			}
			if (params.getStream() != null) {
				requestMap.put("stream",
						RequestBody.create(MediaType.parse("text/plain"), params.getStream().toString()));
			}
			if (params.getRequestId() != null) {
				requestMap.put("request_id", RequestBody.create(MediaType.parse("text/plain"), params.getRequestId()));
			}
			if (params.getUserId() != null) {
				requestMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), params.getUserId()));
			}

			return audioApi.audioTranscription(requestMap, fileData);
		};
		return this.zAiClient.executeRequest(request, supplier, AudioTranscriptionResponse.class);
	}

	private void validateSpeechParams(AudioSpeechRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getModel() == null) {
			throw new IllegalArgumentException("request model cannot be null");
		}
		if (request.getInput() == null || request.getInput().trim().isEmpty()) {
			throw new IllegalArgumentException("request input cannot be null or empty");
		}
		if (request.getVoice() == null || request.getVoice().trim().isEmpty()) {
			throw new IllegalArgumentException("request voice cannot be null or empty");
		}
	}

	private void validateCustomSpeechParams(AudioCustomizationRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getModel() == null) {
			throw new IllegalArgumentException("request model cannot be null");
		}
		if (request.getInput() == null || request.getInput().trim().isEmpty()) {
			throw new IllegalArgumentException("request input cannot be null or empty");
		}
		if (request.getVoiceData() == null) {
			throw new IllegalArgumentException("request voice data cannot be null");
		}
	}

	private void validateTranscriptionParams(AudioTranscriptionRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request cannot be null");
		}
		if (request.getModel() == null) {
			throw new IllegalArgumentException("request model cannot be null");
		}
		if (request.getFile() == null) {
			throw new IllegalArgumentException("request file cannot be null");
		}
		if (!request.getFile().exists()) {
			throw new IllegalArgumentException("request file does not exist");
		}
	}

	private void writeResponseBodyToFile(ResponseBody body, java.io.File file) {
		try (InputStream inputStream = body.byteStream();
				OutputStream outputStream = Files.newOutputStream(file.toPath())) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		}
		catch (IOException e) {
			log.error("writeResponseBodyToFile error,msg:{}", e.getMessage(), e);
		}
	}

	/**
	 * Detect MIME type based on file extension
	 * @param file the file to detect
	 * @return MIME type string
	 */
	private String detectContentType(java.io.File file) {
		String fileName = file.getName().toLowerCase();
		if (fileName.endsWith(".mp3")) {
			return "audio/mpeg";
		}
		else if (fileName.endsWith(".wav")) {
			return "audio/wav";
		}
		else if (fileName.endsWith(".m4a")) {
			return "audio/mp4";
		}
		else if (fileName.endsWith(".aac")) {
			return "audio/aac";
		}
		else if (fileName.endsWith(".ogg")) {
			return "audio/ogg";
		}
		else if (fileName.endsWith(".flac")) {
			return "audio/flac";
		}
		else if (fileName.endsWith(".wma")) {
			return "audio/x-ms-wma";
		}
		else {
			// Default to audio/mpeg for unknown audio files
			return "audio/mpeg";
		}
	}

}