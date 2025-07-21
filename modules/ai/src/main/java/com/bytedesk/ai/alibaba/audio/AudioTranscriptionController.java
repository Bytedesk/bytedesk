/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bytedesk.ai.alibaba.audio;

import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.common.DashScopeException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 语音转文本（语音合成）
 * spring ai alibaba 1.0.0.3 待发布，暂时无法使用
 * 
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@RestController
@RequestMapping("/ai/transcription")
public class AudioTranscriptionController {

	// private final AudioTranscriptionModel transcriptionModel;

	// private static final Logger log = LoggerFactory.getLogger(AudioTranscriptionController.class);

	// 模型列表：https://help.aliyun.com/zh/model-studio/sambert-websocket-api
	// private static final String DEFAULT_MODEL = DashScopeAudioTranscriptionApi.AudioTranscriptionModel.PARAFORMER_REALTIME_V2.getValue();

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public AudioTranscriptionController(AudioTranscriptionModel transcriptionModel) {

		// this.transcriptionModel = transcriptionModel;
	}

	@GetMapping
	public String stt() {

		// String currentDir = System.getProperty("user.dir");
		// Path filePath = Paths.get(currentDir, "hello_world_male_16k_16bit_mono.wav");

		// AudioTranscriptionResponse response = transcriptionModel.call(
		// 		new AudioTranscriptionPrompt(
		// 				new FileSystemResource(filePath),
		// 				DashScopeAudioTranscriptionOptions.builder()
		// 						.withModel(DEFAULT_MODEL)
		// 						.build()
		// 		)
		// );

		// return response.getResult().getOutput();
		return "";
	}

	/**
	 * 以 Audio Speech 的输出作为输入
	 */
	@GetMapping("/stream")
	public String streamSTT() {

		// String currentDir = System.getProperty("user.dir");
		// Path filePath = Paths.get(currentDir, "spring-ai-alibaba-audio-example/dashscope-audio/src/main/resources/gen/tts/output.mp3");

		CountDownLatch latch = new CountDownLatch(1);
		StringBuilder stringBuilder = new StringBuilder();

		// Flux<AudioTranscriptionResponse> response = transcriptionModel
		// 		.stream(
		// 				new AudioTranscriptionPrompt(
		// 						new FileSystemResource(filePath),
		// 						DashScopeAudioTranscriptionOptions.builder()
		// 								.withModel(DEFAULT_MODEL)
		// 								.withSampleRate(16000)
		// 								.withFormat(DashScopeAudioTranscriptionOptions.AudioFormat.PCM)
		// 								.withDisfluencyRemovalEnabled(false)
		// 								.build()
		// 				)
		// 		);

		// response.doFinally(
		// 		signal -> latch.countDown()
		// ).subscribe(
		// 		resp -> stringBuilder.append(resp.getResult().getOutput())
		// );

		try {
			latch.await();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return stringBuilder.toString();
	}

	@GetMapping("/async")
	public String asyncSTT() {
		StringBuilder stringBuilder = new StringBuilder();
		CountDownLatch latch = new CountDownLatch(1);

		// String currentDir = System.getProperty("user.dir");
		// Path filePath = Paths.get(currentDir, "spring-ai-alibaba-audio-example/dashscope-audio/src/main/resources/gen/tts/output-stream.mp3");

		try {
			// AudioTranscriptionResponse submitResponse = transcriptionModel.asyncCall(
			// 		new AudioTranscriptionPrompt(
			// 				new FileSystemResource(filePath),
			// 				DashScopeAudioTranscriptionOptions.builder()
			// 						.withModel(DEFAULT_MODEL)
			// 						.build()
			// 		)
			// );

			// DashScopeAudioTranscriptionApi.Response.Output submitOutput = Objects.requireNonNull(submitResponse.getMetadata()
					// .get("output"));
			// String taskId = submitOutput.taskId();

			// scheduler.scheduleAtFixedRate(
			// 		() -> checkTaskStatus(taskId, stringBuilder, latch), 0, 1, TimeUnit.SECONDS);
			latch.await();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new DashScopeException("Thread was interrupted: " + e.getMessage());
		}
		finally {
			scheduler.shutdown();
		}

		return stringBuilder.toString();
	}

	// private void checkTaskStatus(String taskId, StringBuilder stringBuilder, CountDownLatch latch) {

	// 	try {
	// 		AudioTranscriptionResponse fetchResponse = transcriptionModel.fetch(taskId);
	// 		DashScopeAudioTranscriptionApi.Response.Output fetchOutput =
	// 				Objects.requireNonNull(fetchResponse.getMetadata().get("output"));
	// 		DashScopeAudioTranscriptionApi.TaskStatus taskStatus = fetchOutput.taskStatus();

	// 		if (taskStatus.equals(DashScopeAudioTranscriptionApi.TaskStatus.SUCCEEDED)) {
	// 			stringBuilder.append(fetchResponse.getResult().getOutput());
	// 			latch.countDown();
	// 		}
	// 		else if (taskStatus.equals(DashScopeAudioTranscriptionApi.TaskStatus.FAILED)) {
	// 			log.warn("Transcription failed.");
	// 			latch.countDown();
	// 		}
	// 	}
	// 	catch (Exception e) {
	// 		latch.countDown();
	// 		throw new RuntimeException("Error occurred while checking task status: " + e.getMessage());
	// 	}
	// }

}
