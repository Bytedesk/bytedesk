/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 21:27:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-28 09:58:37
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.springai.event.VectorSplitEvent;
import com.bytedesk.ai.springai.reader.WebDocumentReader;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.file.FileEntity;
import com.bytedesk.kbase.file.FileRestService;
// import com.bytedesk.kbase.qa.FaqEntity;
// import com.bytedesk.kbase.qa.FaqRestService;
import com.bytedesk.kbase.split.SplitRequest;
import com.bytedesk.kbase.split.SplitRestService;
import com.bytedesk.kbase.split.SplitStatusEnum;
import com.bytedesk.kbase.split.SplitTypeEnum;
import com.bytedesk.kbase.text.TextEntity;
import com.bytedesk.kbase.text.TextRestService;
import com.bytedesk.kbase.upload.UploadRestService;
import com.bytedesk.kbase.website.WebsiteEntity;
import com.bytedesk.kbase.website.WebsiteRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true", matchIfMissing = true)
public class SpringAIVectorService {

	private final Optional<RedisVectorStore> ollamaRedisVectorStore;

	private final FileRestService fileRestService;

	private final TextRestService textRestService;

	private final SplitRestService splitRestService;

	private final WebsiteRestService websiteRestService;

	private final FaqRestService faqRestService;

	private final UploadRestService uploadRestService;

	private final BytedeskEventPublisher bytedeskEventPublisher;

	private void handleVectorStoreOperation(String operation, Runnable action) {
		if (!ollamaRedisVectorStore.isPresent()) {
			log.warn("Vector store is not available, skipping {}", operation);
			return;
		}
		try {
			action.run();
			log.debug("Successfully completed {}", operation);
		} catch (Exception e) {
			log.error("Failed to execute {}: {}", operation, e.getMessage(), e);
		}
	}

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

	// string content 转换成 List<Document> documents
	public List<Document> readText(String name, String content, String kbUid, String orgUid) {
		log.info("Converting string content to documents");
		if (content == null || content.isEmpty()) {
			throw new IllegalArgumentException("Content must not be empty");
		}
		// 创建Document对象
		Document document = new Document(content);
		// 使用TokenTextSplitter分割文本
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(List.of(document));
		// 添加元数据: 文件file_uid, 知识库kb_uid
		docList.forEach(doc -> {
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, kbUid);
			// doc.getMetadata().put(KbaseConst.KBASE_ORG_UID, orgUid);
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(name)
					.docId(doc.getId())
					// .typeUid(textEntity.getUid())
					// .categoryUid(textEntity.getCategoryUid())
					.kbUid(kbUid)
					// .userUid(textEntity.getUserUid())
					.build();
			splitRequest.setType(SplitTypeEnum.TEXT.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(orgUid);
			splitRestService.create(splitRequest);
		});
		// log.info("Parsing document, this will take a while.");
		ollamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		//
		return docList;
	}


	// 使用reader直接将content字符串，转换成 List<Document> documents
	public List<Document> readText(TextEntity textEntity) {
		log.info("Converting string content to documents");
		if (textEntity == null || textEntity.getContent() == null || textEntity.getContent().isEmpty()) {
			throw new IllegalArgumentException("Content must not be empty");
		}
		// 创建Document对象
		Document document = new Document(textEntity.getContent());
		// 使用TokenTextSplitter分割文本
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(List.of(document));
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			log.info("doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			// 添加元数据: 文件file_uid, 知识库kb_uid
			// doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, file.getUid());
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, textEntity.getKbUid());
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(textEntity.getName())
					.docId(doc.getId())
					.typeUid(textEntity.getUid())
					.categoryUid(textEntity.getCategoryUid())
					.kbUid(textEntity.getKbUid())
					.userUid(textEntity.getUserUid())
					.build();
			splitRequest.setType(SplitTypeEnum.TEXT.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(textEntity.getOrgUid());
			splitRestService.create(splitRequest);
		}
		textEntity.setDocIdList(docIdList);
		textEntity.setStatus(SplitStatusEnum.SUCCESS.name());
		textRestService.save(textEntity);
		// log.info("Parsing document, this will take a while.");
		ollamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));

		return docList;
	}

	// 使用reader直接将qaEntity字符串，转换成 List<Document> documents
	public List<Document> readFaq(FaqEntity fqaEntity) {
		log.info("Converting string content to documents");
		if (fqaEntity == null || fqaEntity.getAnswer() == null || fqaEntity.getAnswer().isEmpty()) {
			throw new IllegalArgumentException("Content must not be empty");
		}
		if (fqaEntity.getQuestion() == null || fqaEntity.getQuestion().isEmpty()) {
			throw new IllegalArgumentException("Question must not be empty");
		}
		if (fqaEntity.getAnswer() == null || fqaEntity.getAnswer().isEmpty()) {
			throw new IllegalArgumentException("Answer must not be empty");
		}
		//
		String content = JSON.toJSONString(fqaEntity);
		// 创建Document对象
		Document document = new Document(content);
		// 使用TokenTextSplitter分割文本
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(List.of(document));
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			log.info("faq doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			// 添加元数据: 文件file_uid, 知识库kb_uid
			// doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, file.getUid());
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, fqaEntity.getKbUid());
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(fqaEntity.getQuestion())
					.docId(doc.getId())
					.typeUid(fqaEntity.getUid())
					.categoryUid(fqaEntity.getCategoryUid())
					.kbUid(fqaEntity.getKbUid())
					.userUid(fqaEntity.getUserUid())
					.build();
			splitRequest.setType(SplitTypeEnum.QA.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(fqaEntity.getOrgUid());
			splitRestService.create(splitRequest);
		}
		fqaEntity.setDocIdList(docIdList);
		fqaEntity.setStatus(SplitStatusEnum.SUCCESS.name());
		faqRestService.save(fqaEntity);
		// log.info("Parsing document, this will take a while.");
		ollamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));

		return docList;
	}

	// 抓取website
	public List<Document> readWebsite(WebsiteEntity websiteEntity) {
		log.info("Loading document from website: {}", websiteEntity.getUrl());
		if (websiteEntity == null || websiteEntity.getUrl() == null || websiteEntity.getUrl().isEmpty()) {
			throw new IllegalArgumentException("URL must not be empty");
		}
		if (!websiteEntity.getUrl().startsWith("http")) {
			throw new IllegalArgumentException(
					String.format("URL must start with http, got %s", websiteEntity.getUrl()));
		}
		//
		try {
			// 构建URI
			URI uri = UriComponentsBuilder.fromHttpUrl(websiteEntity.getUrl()).build().toUri();
			// 创建元数据
			Map<String, String> metadata = new HashMap<>();
			metadata.put(KbaseConst.KBASE_KB_UID, websiteEntity.getKbUid());
			metadata.put("source_url", websiteEntity.getUrl());
			// 创建WebDocumentReader
			WebDocumentReader webReader = new WebDocumentReader(uri, metadata);
			// 读取网页内容
			List<Document> documents = webReader.read();
			// 使用TokenTextSplitter分割文本
			var tokenTextSplitter = new TokenTextSplitter();
			List<Document> docList = tokenTextSplitter.split(documents);
			List<String> docIdList = new ArrayList<>();
			Iterator<Document> iterator = docList.iterator();
			while (iterator.hasNext()) {
				Document doc = iterator.next();
				log.info("doc id: {}", doc.getId());
				docIdList.add(doc.getId());
				// 添加元数据: 文件file_uid, 知识库kb_uid
				// doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, file.getUid());
				doc.getMetadata().put(KbaseConst.KBASE_KB_UID, websiteEntity.getKbUid());
				// 将doc写入到splitEntity
				SplitRequest splitRequest = SplitRequest.builder()
						.name(websiteEntity.getName())
						.docId(doc.getId())
						.typeUid(websiteEntity.getUid())
						.categoryUid(websiteEntity.getCategoryUid())
						.kbUid(websiteEntity.getKbUid())
						.userUid(websiteEntity.getUserUid())
						.build();
				splitRequest.setType(SplitTypeEnum.WEBSITE.name());
				splitRequest.setContent(doc.getText());
				splitRequest.setOrgUid(websiteEntity.getOrgUid());
				splitRestService.create(splitRequest);
			}
			// 如果需要存储到向量数据库
			if (websiteEntity.getKbUid() != null) {
				ollamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
				log.info("Website content stored in vector store for kbUid: {}", websiteEntity.getKbUid());
			}
			//
			websiteEntity.setDocIdList(docIdList);
			websiteEntity.setStatus(SplitStatusEnum.SUCCESS.name());
			websiteRestService.save(websiteEntity);
			// log.info("Parsing document, this will take a while.");
			ollamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));

			return docList;

		} catch (Exception e) {
			log.error("Error reading website content: {}", e.getMessage());
			throw new RuntimeException("Failed to read website content: " + e.getMessage(), e);
		}
	}

	// 存储到vector store
	private void storeDocuments(List<Document> docList, FileEntity file) {
		if (!ollamaRedisVectorStore.isPresent()) {
			log.warn("Vector store is not available, skipping document storage for file: {}", file.getFileName());
			return;
		}

		log.info("Parsing document, this will take a while. docList.size={}", docList.size());
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			log.info("doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, file.getUid());
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, file.getKbUid());
			
			SplitRequest splitRequest = SplitRequest.builder()
					.name(file.getFileName())
					.docId(doc.getId())
					.typeUid(file.getUid())
					.categoryUid(file.getCategoryUid())
					.kbUid(file.getKbUid())
					.userUid(file.getUserUid())
					.build();
			splitRequest.setType(SplitTypeEnum.FILE.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(file.getOrgUid());
			splitRestService.create(splitRequest);
		}
		file.setDocIdList(docIdList);
		file.setStatus(SplitStatusEnum.SUCCESS.name());
		fileRestService.save(file);
		
		ollamaRedisVectorStore.ifPresent(redisVectorStore -> {
			try {
				redisVectorStore.write(docList);
				log.info("Successfully stored documents in vector store");
			} catch (Exception e) {
				log.error("Failed to store documents in vector store: {}", e.getMessage());
			}
		});

		bytedeskEventPublisher.publishEvent(new VectorSplitEvent(file.getKbUid(), file.getOrgUid(), docList));
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	// https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
	public List<String> searchText(String query) {
		if (!ollamaRedisVectorStore.isPresent()) {
			log.warn("Vector store is not available");
			return List.of();
		}

		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.topK(2)
				.build();
		List<Document> similarDocuments = ollamaRedisVectorStore.map(redisVectorStore -> 
			redisVectorStore.similaritySearch(searchRequest)).orElse(List.of());
		return similarDocuments.stream().map(Document::getText).toList();
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	public List<String> searchText(String query, String kbUid) {
		if (!ollamaRedisVectorStore.isPresent()) {
			log.warn("Vector store is not available for kbUid: {}", kbUid);
			return List.of();
		}

		log.info("searchText kbUid {}, query: {}", kbUid, query);
		FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
		Expression expression = expressionBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid).build();
		log.info("expression: {}", expression.toString());

		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.filterExpression(expression)
				.build();
		List<Document> similarDocuments = ollamaRedisVectorStore.map(redisVectorStore -> 
			redisVectorStore.similaritySearch(searchRequest)).orElse(List.of());
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		log.info("kbUid {}, query: {} , contentList.size: {}", kbUid, query, contentList.size());
		return contentList;
	}

	public void deleteDoc(List<String> docIdList) {
		ollamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.delete(docIdList));
	}

}
