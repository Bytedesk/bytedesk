/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 13:52:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-03 13:14:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.providers.dashscope;

// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
// import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
// import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
// import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
// import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;

// import lombok.RequiredArgsConstructor;
// import reactor.core.publisher.Flux;

// import java.io.ByteArrayOutputStream;
// import java.io.File;
// import java.io.IOException;
// import java.nio.ByteBuffer;
// import java.util.concurrent.CountDownLatch;

// import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
// import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.core.io.FileSystemResource;

// @RequiredArgsConstructor
// @Service
// @ConditionalOnProperty(name = {"spring.ai.dashscope.audio.transcription.enabled", "spring.ai.dashscope.audio.synthesis.enabled"}, havingValue = "true")
// public class SpringAIDashscopeAudioService {

//     @Qualifier("bytedeskDashScopeAudioTranscriptionModel")
//     private final AudioTranscriptionModel bytedeskDashScopeAudioTranscriptionModel;

//     @Qualifier("bytedeskDashScopeSpeechSynthesisModel")
// 	private final SpeechSynthesisModel bytedeskDashScopeSpeechSynthesisModel;
	
//     /**
// 	 * 将文本转为语音
// 	 */
// 	public byte[] text2audio(String text) {

// 		Flux<SpeechSynthesisResponse> response = bytedeskDashScopeSpeechSynthesisModel.stream(
// 				new SpeechSynthesisPrompt(text)
// 		);

// 		CountDownLatch latch = new CountDownLatch(1);
// 		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

// 		try {
// 			response.doFinally(
// 					signal -> latch.countDown()
// 			).subscribe(synthesisResponse -> {

// 				ByteBuffer byteBuffer = synthesisResponse.getResult().getOutput().getAudio();
// 				byte[] bytes = new byte[byteBuffer.remaining()];
// 				byteBuffer.get(bytes);

// 				try {
// 					outputStream.write(bytes);
// 				}
// 				catch (IOException e) {
// 					throw new RuntimeException("Error writing to output stream " + e.getMessage());
// 				}
// 			});

// 			latch.await();
// 		}
// 		catch (InterruptedException e) {
// 			throw new RuntimeException("Operation interrupted. " + e.getMessage());
// 		}

// 		return outputStream.toByteArray();
// 	}

// 	/**
// 	 * 将语音转为文本
// 	 */
// 	public Flux<String> audio2text(MultipartFile audio) {

// 		CountDownLatch latch = new CountDownLatch(1);
// 		StringBuilder stringBuilder = new StringBuilder();

// 		File tempFile;
// 		try {
// 			tempFile = File.createTempFile("audio", ".pcm");
// 			audio.transferTo(tempFile);
// 		}
// 		catch (IOException e) {
// 			throw new RuntimeException("Failed to create temporary file " + e.getMessage());
// 		}

// 		Flux<AudioTranscriptionResponse> response = bytedeskDashScopeAudioTranscriptionModel.stream(
// 				new AudioTranscriptionPrompt(
// 						new FileSystemResource(tempFile),
// 						DashScopeAudioTranscriptionOptions.builder()
// 								.withModel("paraformer-realtime-v2")
// 								.withSampleRate(16000)
// 								.withFormat(DashScopeAudioTranscriptionOptions.AudioFormat.PCM)
// 								.withDisfluencyRemovalEnabled(false)
// 								.build()
// 				)
// 		);

// 		response.doFinally(signal -> latch.countDown())
// 				.subscribe(resp -> stringBuilder.append(resp.getResult().getOutput()).append("\n"));

// 		try {
// 			latch.await();
// 		}
// 		catch (InterruptedException e) {
// 			throw new RuntimeException("Transcription was interrupted " + e.getMessage());
// 		}
// 		finally {
// 			tempFile.delete();
// 		}

// 		return Flux.just(stringBuilder.toString());
// 	}

// }
