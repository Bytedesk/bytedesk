/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 13:53:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 15:45:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Media;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.dashscope.chat.enabled", havingValue = "true")
public class SpringAIAlibabaImageService {

    private final ImageModel dashScopeImageModel;

	private final ChatClient dashScopeChatClient;  

    public Flux<String> image2Text(MultipartFile file) {

		UserMessage message = new UserMessage(
				"解释图片中的内容",
				new Media(
						MimeTypeUtils.IMAGE_PNG,
						new FileSystemResource(Objects.requireNonNull(file.getOriginalFilename()))
				)
		);
		message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.IMAGE);

		List<ChatResponse> response = dashScopeChatClient.prompt(
				new Prompt(message)
		).stream().chatResponse().collectList().block();

		StringBuilder result = new StringBuilder();
		if (response != null) {
			for (ChatResponse chatResponse : response) {
				String outputContent = chatResponse.toString();
				result.append(outputContent).append("\n");
			}
		}

		return Flux.just(result.toString());
	}

	public void text2Image(String prompt, HttpServletResponse response) {

		ImageResponse imageResponse = dashScopeImageModel.call(new ImagePrompt(prompt));
		String imageUrl = imageResponse.getResult().getOutput().getUrl();

		try {
			URL url = URI.create(imageUrl).toURL();
			InputStream in = url.openStream();

			response.setHeader("Content-Type", MediaType.IMAGE_PNG_VALUE);
			response.getOutputStream().write(in.readAllBytes());
			response.getOutputStream().flush();
		}
		catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
