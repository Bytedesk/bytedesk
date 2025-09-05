/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 09:08:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 11:10:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
// import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadRestService uploadRestService;

    /**
	 * https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
	 */
	public List<Document> parseFileContent(UploadEntity upload) {
		String fileUrl = upload.getFileUrl();
		log.info("Loading document from URL: {}", fileUrl);
		Assert.hasText(fileUrl, "File URL must not be empty");
		Assert.isTrue(fileUrl.startsWith("http"), String.format("File URL must start with http, got %s", fileUrl));

		// 从URL中提取实际的文件名和路径
		String filePathList[] = fileUrl.split("/");
		String actualFileName = filePathList[filePathList.length - 1];
		log.info("actualFileName from URL: {}, original fileName: {}", actualFileName, upload.getFileName());
		
		// 从URL中提取日期路径部分，例如：2025/09/05
		// URL格式：http://127.0.0.1:9003/file/2025/09/05/1757037537985_5810.pdf
		String datePath = filePathList[filePathList.length - 4] + "/" + 
						 filePathList[filePathList.length - 3] + "/" + 
						 filePathList[filePathList.length - 2];
		
		// 使用合并后的 loadAsResource 方法，传入完整路径
		String fullPath = datePath + "/" + actualFileName;
        Resource resource = uploadRestService.loadAsResource(fullPath);

		List<Document> documents;
		if (actualFileName.toLowerCase().endsWith(".pdf")) {
			documents = readPdfPage(resource);
		} else if (actualFileName.toLowerCase().endsWith(".json")) {
			documents = readJson(resource);
		} else if (actualFileName.toLowerCase().endsWith(".txt")) {
			documents = readTxt(resource);
		} else if (actualFileName.toLowerCase().endsWith(".md")) {
			documents = readMarkdown(resource);
		} else {
			documents = readByTika(resource);
		}
		
		// 清理文档内容，移除可能导致问题的特殊字符
		List<Document> cleanedDocuments = new ArrayList<>();
		for (Document doc : documents) {
			String cleanText = cleanDocumentText(doc.getText());
			if (!cleanText.trim().isEmpty()) {
				Document cleanDoc = new Document(cleanText, doc.getMetadata());
				cleanedDocuments.add(cleanDoc);
			}
		}
		
		log.info("文档解析和清理完成，原始文档数: {}, 清理后文档数: {}", documents.size(), cleanedDocuments.size());
		return cleanedDocuments;
	}

	public List<Document> readPdfPage(Resource resource) {
        // 
		PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
				.withPageTopMargin(0)
				.withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
						.withNumberOfTopTextLinesToDelete(0)
						.build())
				.withPagesPerDocument(1)
				.build();
		PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);

		return pdfReader.read();
	}

	public List<Document> readPdfParagraph(Resource resource) {
		// log.info("Loading document from pdfParagraph: {}", fileName);

		ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(
				resource,
				PdfDocumentReaderConfig.builder()
						.withPageTopMargin(0)
						.withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
								.withNumberOfTopTextLinesToDelete(0)
								.build())
						.withPagesPerDocument(1)
						.build());

		return pdfReader.read();
		// 读取所有文档
		// List<Document> documents = pdfReader.read();
		// // 提取文本内容
		// StringBuilder contentBuilder = new StringBuilder();
		// for (Document doc : documents) {
		// 	contentBuilder.append(doc.getText()).append("\n");
		// }
		// // 保存文本内容到file
		// // upload.setContent(contentBuilder.toString());
        // return contentBuilder.toString();

		// 继续原有的分割和存储逻辑
		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	public List<Document> readJson(Resource resource) {
		// log.info("Loading document from json: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".json"), String.format("File must end with .json, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		JsonReader jsonReader = new JsonReader(resource, "description");

		return jsonReader.read();
		// 读取所有文档
		// List<Document> documents = jsonReader.read();
		// // 提取文本内容
		// StringBuilder contentBuilder = new StringBuilder();
		// for (Document doc : documents) {
		// 	contentBuilder.append(doc.getText()).append("\n");
		// }
		// // 保存文本内容到file
		// // upload.setContent(contentBuilder.toString());
        // return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	// 使用spring ai markdown reader
	public List<Document> readMarkdown(Resource resource) {
		// log.info("Loading document from markdown: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".md"), String.format("File must end with .md, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		MarkdownDocumentReader markdownReader = new MarkdownDocumentReader(resource,
				MarkdownDocumentReaderConfig.builder().build());

		return markdownReader.read();
		// List<Document> documents = markdownReader.read();
		// // 提取文本内容
		// StringBuilder contentBuilder = new StringBuilder();
		// for (Document doc : documents) {
		// 	contentBuilder.append(doc.getText()).append("\n");
		// }
		// // 保存文本内容到file
		// // upload.setContent(contentBuilder.toString());
        // return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	public List<Document> readTxt(Resource resource) {
		// log.info("Loading document from txt: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".txt"), String.format("File must end with .txt, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		TextReader textReader = new TextReader(resource);
		// textReader.getCustomMetadata().put("filename", fileName);

		return textReader.read();
		// 读取所有文档
		// List<Document> documents = textReader.read();
		// // 提取文本内容
		// StringBuilder contentBuilder = new StringBuilder();
		// for (Document doc : documents) {
		// 	contentBuilder.append(doc.getText()).append("\n");
		// }
		// // 保存文本内容到file
		// // upload.setContent(contentBuilder.toString());
        // return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	// https://tika.apache.org/2.9.0/formats.html
	// PDF, DOC/DOCX, PPT/PPTX, and HTML
	public List<Document> readByTika(Resource resource) {
		// log.info("Loading document from tika: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.notNull(file, "FileEntity must not be null");

		// Resource resource = uploadRestService.loadAsResource(fileName);
		TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

		return tikaDocumentReader.read();
		// 读取所有文档
		// List<Document> documents = tikaDocumentReader.read();
		// // 提取文本内容
		// StringBuilder contentBuilder = new StringBuilder();
		// for (Document doc : documents) {
		// 	contentBuilder.append(doc.getText()).append("\n");
		// }
		// // 保存文本内容到file
		// // upload.setContent(contentBuilder.toString());
        // return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	/**
	 * 智能截取文档内容摘要
	 * @param documents 文档列表
	 * @param maxLength 最大长度
	 * @return 截取后的内容摘要
	 */
	public String extractContentSummary(List<Document> documents, int maxLength) {
		if (documents == null || documents.isEmpty()) {
			return "";
		}
		
		StringBuilder contentBuilder = new StringBuilder();
		int currentLength = 0;
		
		// 优先取每个文档的前部分内容
		for (Document doc : documents) {
			String docText = doc.getText();
			if (docText == null || docText.trim().isEmpty()) {
				continue;
			}
			
			// 清理文本，移除多余的空白字符和潜在问题字符
			docText = cleanDocumentText(docText);
			
			if (currentLength + docText.length() <= maxLength) {
				contentBuilder.append(docText).append("\n");
				currentLength += docText.length() + 1; // +1 for newline
			} else {
				// 如果剩余空间足够，添加部分内容
				int remainingSpace = maxLength - currentLength;
				if (remainingSpace > 100) { // 至少保留100个字符的空间
					String truncatedText = docText.substring(0, Math.min(docText.length(), remainingSpace - 50));
					// 尝试在句号、问号或感叹号处截断
					int lastSentence = Math.max(
						Math.max(truncatedText.lastIndexOf('.'), truncatedText.lastIndexOf('。')),
						Math.max(truncatedText.lastIndexOf('!'), truncatedText.lastIndexOf('？'))
					);
					if (lastSentence > truncatedText.length() / 2) {
						truncatedText = truncatedText.substring(0, lastSentence + 1);
					}
					contentBuilder.append(truncatedText).append("...[内容已截断，完整内容将在文档分块中处理]");
				}
				break;
			}
		}
		
		String result = contentBuilder.toString();
		log.info("内容摘要生成完成，原始文档数: {}, 摘要长度: {}", documents.size(), result.length());
		return result;
	}

	/**
	 * 清理文档文本，移除可能导致问题的特殊字符
	 * @param text 原始文本
	 * @return 清理后的文本
	 */
	public String cleanDocumentText(String text) {
		if (text == null) return "";
		
		return text
			// 移除控制字符（除了常见的换行、回车、制表符）
			.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", " ")
			// 规范化空白字符
			.replaceAll("\\s+", " ")
			// 移除可能的特殊Unicode字符
			.replaceAll("[\\uFFF0-\\uFFFF]", "")
			// 移除一些可能导致token编码问题的特殊字符
			.replaceAll("[\\uE000-\\uF8FF]", "") // 私用区字符
			.trim();
	}

    
}
