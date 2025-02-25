/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 21:27:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 13:50:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.springai.event.VectorSplitEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.file.FileEntity;
import com.bytedesk.kbase.file.FileRestService;
import com.bytedesk.kbase.upload.UploadRestService;
import com.bytedesk.kbase.upload.UploadStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SpringAIVectorService {

	private final RedisVectorStore ollamaRedisVectorStore;

	private final FileRestService fileRestService;

	private final UploadRestService uploadRestService;

	private final BytedeskEventPublisher bytedeskEventPublisher;

	/**
	 * https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
	 */
	public void readSplitWriteToVectorStore(@NonNull FileEntity file) {
		// 
		String fileUrl = file.getFileUrl();
		log.info("Loading document from URL: {}", fileUrl);
		if (fileUrl == null || fileUrl.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileUrl.startsWith("http")) {
			throw new IllegalArgumentException(String.format("File URL must start with http, got %s", fileUrl));
		}
		String filePathList[] = fileUrl.split("/");
		String fileName = filePathList[filePathList.length - 1];
		log.info("fileName {}", fileName);
		//
		if (fileName.toLowerCase().endsWith(".pdf")) {
			readPdfPage(fileName, file);
		} else if (fileName.toLowerCase().endsWith(".json")) {
			readJson(fileName, file);
		} else if (fileName.toLowerCase().endsWith(".txt")) {
			readTxt(fileName, file);
		} else {
			readByTika(fileName, file);
		}
	}

	public void readPdfPage(String fileName, FileEntity file) {
		log.info("Loading document from pdfPage: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".pdf")) {
			throw new IllegalArgumentException(String.format("File URL must end with .pdf, got %s", fileName));
		}
		
		Resource resource = uploadRestService.loadAsResource(fileName);
		
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
		file.setContent(contentBuilder.toString());
		
		// 继续原有的分割和存储逻辑
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(documents);
		storeDocuments(docList, file);
	}

	public void readPdfParagraph(String fileName, FileEntity file) {
		log.info("Loading document from pdfParagraph: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".pdf")) {
			throw new IllegalArgumentException(String.format("File URL must end with .pdf, got %s", fileName));
		}
		//
		Resource resource = uploadRestService.loadAsResource(fileName);
		//
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
		file.setContent(contentBuilder.toString());
		
		// 继续原有的分割和存储逻辑
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(documents);
		storeDocuments(docList, file);
	}

	public void readJson(String fileName, FileEntity file) {
		log.info("Loading document from json: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".json")) {
			throw new IllegalArgumentException(String.format("File URL must end with .json, got %s", fileName));
		}
		
		Resource resource = uploadRestService.loadAsResource(fileName);
		JsonReader jsonReader = new JsonReader(resource, "description");
		
		// 读取所有文档
		List<Document> documents = jsonReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		file.setContent(contentBuilder.toString());
		
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(documents);
		storeDocuments(docList, file);
	}

	public void readTxt(String fileName, FileEntity file) {
		log.info("Loading document from txt: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".txt")) {
			throw new IllegalArgumentException(String.format("File URL must end with .txt, got %s", fileName));
		}
		
		Resource resource = uploadRestService.loadAsResource(fileName);
		TextReader textReader = new TextReader(resource);
		textReader.getCustomMetadata().put("filename", fileName);
		
		// 读取所有文档
		List<Document> documents = textReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		file.setContent(contentBuilder.toString());
		
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(documents);
		storeDocuments(docList, file);
	}

	// https://tika.apache.org/2.9.0/formats.html
	// PDF, DOC/DOCX, PPT/PPTX, and HTML
	public void readByTika(String fileName, FileEntity file) {
		log.info("Loading document from tika: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		
		Resource resource = uploadRestService.loadAsResource(fileName);
		TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
		
		// 读取所有文档
		List<Document> documents = tikaDocumentReader.read();
		// 提取文本内容
		StringBuilder contentBuilder = new StringBuilder();
		for (Document doc : documents) {
			contentBuilder.append(doc.getText()).append("\n");
		}
		// 保存文本内容到file
		file.setContent(contentBuilder.toString());
		
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(documents);
		storeDocuments(docList, file);
	}

	// 存储到vector store
	private void storeDocuments(List<Document> docList, FileEntity file) {
		log.info("Parsing document, this will take a while. docList.size={}", docList.size());
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			log.info("doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			// 添加元数据: 文件file_uid, 知识库kb_uid
			doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, file.getUid());
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, file.getKbUid());
		}
		file.setDocIdList(docIdList);
		file.setStatus(UploadStatusEnum.PARSE_FILE_SUCCESS.name());
		// 
		fileRestService.save(file);
		// log.info("Parsing document, this will take a while.");
		ollamaRedisVectorStore.write(docList);
		log.info("Done parsing document, splitting, creating embeddings and storing in vector store");
		// 通知相关组件，文件处理成功
		bytedeskEventPublisher.publishEvent(new VectorSplitEvent(file.getKbUid(), file.getOrgUid(), docList));
		// 生成问答对
		// generateQaPairs(docList, upload);
	}


	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	// https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
	public List<String> searchText(String query) {
		// Retrieve documents similar to a query
		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.topK(2)
				.build();
		List<Document> similarDocuments = ollamaRedisVectorStore.similaritySearch(searchRequest);
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		// TODO: 将 query, kbUid 对应的 contentList 缓存到Redis中，下次直接从Redis中取
		//
		return contentList;
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	public List<String> searchText(String query, String kbUid) {
		log.info("searchText kbUid {}, query: {}", kbUid, query);
		// Retrieve documents similar to a query
		FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
		Expression expression = expressionBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid).build();
		log.info("expression: {}", expression.toString());
		//
		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.filterExpression(expression)
				.build();
		// .withTopK(2);
		// .withSimilarityThreshold(0.5)
		// .withFilterExpression(expression);
		List<Document> similarDocuments = ollamaRedisVectorStore.similaritySearch(searchRequest);
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		log.info("kbUid {}, query: {} , contentList.size: {}", kbUid, query, contentList.size());
		//
		return contentList;
	}

	public void deleteDoc(List<String> docIdList) {
		ollamaRedisVectorStore.delete(docIdList);
	}


}
