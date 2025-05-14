/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 09:08:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 09:31:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

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
public class FileChunkService {

    private final UploadRestService uploadRestService;

    /**
	 * https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
	 */
	public String parseFileContent(UploadEntity upload) {
		String fileUrl = upload.getFileUrl();
		log.info("Loading document from URL: {}", fileUrl);
		Assert.hasText(fileUrl, "File URL must not be empty");
		Assert.isTrue(fileUrl.startsWith("http"), String.format("File URL must start with http, got %s", fileUrl));

		String filePathList[] = fileUrl.split("/");
		String fileName = filePathList[filePathList.length - 1];
		log.info("fileName {}", fileName);
        Resource resource = uploadRestService.loadAsResource(fileName);

		if (fileName.toLowerCase().endsWith(".pdf")) {
			return readPdfPage(resource);
		} else if (fileName.toLowerCase().endsWith(".json")) {
			return readJson(resource);
		} else if (fileName.toLowerCase().endsWith(".txt")) {
			return readTxt(resource);
		} else if (fileName.toLowerCase().endsWith(".md")) {
			return readMarkdown(resource);
		} else {
			return readByTika(resource);
		}
	}

	public String readPdfPage(Resource resource) {
        // 
		PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
				.withPageTopMargin(0)
				.withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
						.withNumberOfTopTextLinesToDelete(0)
						.build())
				.withPagesPerDocument(1)
				.build();
		PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);

		// 读取所有文档
		List<Document> documents = pdfReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		// upload.setContent(contentBuilder.toString());
        return contentBuilder.toString();

		// 继续原有的分割和存储逻辑
		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	public String readPdfParagraph(Resource resource) {
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
		// 读取所有文档
		List<Document> documents = pdfReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		// upload.setContent(contentBuilder.toString());
        return contentBuilder.toString();

		// 继续原有的分割和存储逻辑
		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	public String readJson(Resource resource) {
		// log.info("Loading document from json: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".json"), String.format("File must end with .json, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		JsonReader jsonReader = new JsonReader(resource, "description");

		// 读取所有文档
		List<Document> documents = jsonReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		// upload.setContent(contentBuilder.toString());
        return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	// 使用spring ai markdown reader
	public String readMarkdown(Resource resource) {
		// log.info("Loading document from markdown: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".md"), String.format("File must end with .md, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		MarkdownDocumentReader markdownReader = new MarkdownDocumentReader(resource,
				MarkdownDocumentReaderConfig.builder().build());
		List<Document> documents = markdownReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		// upload.setContent(contentBuilder.toString());
        return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	public String readTxt(Resource resource) {
		// log.info("Loading document from txt: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".txt"), String.format("File must end with .txt, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		TextReader textReader = new TextReader(resource);
		// textReader.getCustomMetadata().put("filename", fileName);

		// 读取所有文档
		List<Document> documents = textReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		// upload.setContent(contentBuilder.toString());
        return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

	// https://tika.apache.org/2.9.0/formats.html
	// PDF, DOC/DOCX, PPT/PPTX, and HTML
	public String readByTika(Resource resource) {
		// log.info("Loading document from tika: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.notNull(file, "FileEntity must not be null");

		// Resource resource = uploadRestService.loadAsResource(fileName);
		TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

		// 读取所有文档
		List<Document> documents = tikaDocumentReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		// upload.setContent(contentBuilder.toString());
        return contentBuilder.toString();

		// var tokenTextSplitter = new TokenTextSplitter();
		// List<Document> docList = tokenTextSplitter.split(documents);
		// storeDocuments(docList, file);
	}

    
}
