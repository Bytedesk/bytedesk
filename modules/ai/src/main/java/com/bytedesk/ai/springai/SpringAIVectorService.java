/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 21:27:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 16:45:09
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
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
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
import java.util.stream.Collectors;
import java.util.HashMap;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.springai.event.VectorSplitEvent;
import com.bytedesk.ai.springai.reader.WebDocumentReader;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.upload.UploadRestService;
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
import com.bytedesk.kbase.website.WebsiteEntity;
import com.bytedesk.kbase.website.WebsiteRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;

@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty(
    name = {
        "spring.ai.ollama.embedding.enabled",  // Ollama embedding 配置
    },
    havingValue = "true",                      // 期望值为 true
    matchIfMissing = false                     // 如果配置不存在则不匹配
)
public class SpringAIVectorService {

	private final Optional<RedisVectorStore> bytedeskOllamaRedisVectorStore;

	private final Optional<RedisVectorStore> bytedeskZhipuaiRedisVectorStore;

	private final FileRestService fileRestService;

	private final TextRestService textRestService;

	private final SplitRestService splitRestService;

	private final WebsiteRestService websiteRestService;

	private final FaqRestService faqRestService;

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
		} else if (fileName.toLowerCase().endsWith(".md")) {
			readMarkdown(fileName, file);
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

	// 使用spring ai markdown reader
	public void readMarkdown(String fileName, FileEntity file) {
		log.info("Loading document from markdown: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".md")) {
			throw new IllegalArgumentException(String.format("File URL must end with .md, got %s", fileName));
		}

		Resource resource = uploadRestService.loadAsResource(fileName);
		MarkdownDocumentReader markdownReader = new MarkdownDocumentReader(resource, MarkdownDocumentReaderConfig.builder().build());
		List<Document> documents = markdownReader.read();
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
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		}
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
					// .userUid(textEntity.getUserUid())
					.build();
			splitRequest.setUserUid(textEntity.getUserUid());
			splitRequest.setType(SplitTypeEnum.TEXT.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(textEntity.getOrgUid());
			splitRestService.create(splitRequest);
		}
		textEntity.setDocIdList(docIdList);
		textEntity.setStatus(SplitStatusEnum.SUCCESS.name());
		textRestService.save(textEntity);
		// log.info("Parsing document, this will take a while.");
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		}

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
		String content = fqaEntity.getQuestion() + "\n" + fqaEntity.getAnswer();
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
					// .userUid(fqaEntity.getUserUid())
					.build();
			splitRequest.setUserUid(fqaEntity.getUserUid());
			splitRequest.setType(SplitTypeEnum.QA.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(fqaEntity.getOrgUid());
			splitRestService.create(splitRequest);
		}
		fqaEntity.setDocIdList(docIdList);
		fqaEntity.setStatus(SplitStatusEnum.SUCCESS.name());
		faqRestService.save(fqaEntity);
		// log.info("Parsing document, this will take a while.");
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		}

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
						// .userUid(websiteEntity.getUserUid())
						.build();
					splitRequest.setUserUid(websiteEntity.getUserUid());
				splitRequest.setType(SplitTypeEnum.WEBSITE.name());
				splitRequest.setContent(doc.getText());
				splitRequest.setOrgUid(websiteEntity.getOrgUid());
				splitRestService.create(splitRequest);
			}
			// 如果需要存储到向量数据库
			if (websiteEntity.getKbUid() != null) {
				bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
				log.info("Website content stored in vector store for kbUid: {}", websiteEntity.getKbUid());
			}
			//
			websiteEntity.setDocIdList(docIdList);
			websiteEntity.setStatus(SplitStatusEnum.SUCCESS.name());
			websiteRestService.save(websiteEntity);
			// log.info("Parsing document, this will take a while.");
			bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
			// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		}

			return docList;

		} catch (Exception e) {
			log.error("Error reading website content: {}", e.getMessage());
			throw new RuntimeException("Failed to read website content: " + e.getMessage(), e);
		}
	}

	// 存储到vector store
	private void storeDocuments(List<Document> docList, FileEntity file) {
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
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
					// .userUid(file.getUserUid())
					.build();
					splitRequest.setUserUid(file.getUserUid());
			splitRequest.setType(SplitTypeEnum.FILE.name());
			splitRequest.setContent(doc.getText());
			splitRequest.setOrgUid(file.getOrgUid());
			splitRestService.create(splitRequest);
		}
		file.setDocIdList(docIdList);
		file.setStatus(SplitStatusEnum.SUCCESS.name());
		fileRestService.save(file);
		
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> {
			try {
				redisVectorStore.write(docList);
				log.info("Successfully stored documents in vector store");
			} catch (Exception e) {
				log.error("Failed to store documents in vector store: {}", e.getMessage());
			}
		});
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		}

		bytedeskEventPublisher.publishEvent(new VectorSplitEvent(file.getKbUid(), file.getOrgUid(), docList));
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	// https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
	public List<String> searchText(String query) {
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			log.warn("Vector store is not available");
			return List.of();
		}

		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.topK(2)
				.build();
		List<Document> similarDocuments = bytedeskOllamaRedisVectorStore.map(redisVectorStore -> 
			redisVectorStore.similaritySearch(searchRequest)).orElse(List.of());
		return similarDocuments.stream().map(Document::getText).toList();
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	public List<String> searchText(String query, String kbUid) {
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
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
		List<Document> similarDocuments = bytedeskOllamaRedisVectorStore.map(redisVectorStore -> 
			redisVectorStore.similaritySearch(searchRequest)).orElse(List.of());
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		log.info("kbUid {}, query: {} , contentList.size: {}", kbUid, query, contentList.size());
		return contentList;
	}

	/**
	 * 更新向量存储中的文档内容
	 * 
	 * @param docId 文档ID
	 * @param content 新的文档内容
	 */
	public void updateDoc(String docId, String content, String kbUid) {
		log.info("updateDoc docId: {}, content: {}", docId, content);
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> {
			try {
				// 创建新的Document对象
				Document document = new Document(docId, content, Map.of(KbaseConst.KBASE_KB_UID, kbUid));
				// 更新向量存储
				redisVectorStore.delete(List.of(docId));  // 先删除旧的
				redisVectorStore.add(List.of(document));  // 再添加新的
				
				log.info("Successfully updated document: {}", docId);
			} catch (Exception e) {
				log.error("Failed to update document: {}", docId, e);
				throw new RuntimeException("Failed to update document", e);
			}
		});
	}

	/**
	 * 批量更新向量存储中的文档内容
	 * List<Document> documents = List.of(
     * new Document("doc1", "内容1", null),
     * new Document("doc2", "内容2", null)
     * );
	 * 
	 * @param documents 要更新的文档列表
	 */
	public void updateDocs(List<Document> documents) {
		log.info("Updating {} documents", documents.size());
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> {
			try {
				List<String> docIds = documents.stream()
					.map(Document::getId)
					.collect(Collectors.toList());
				
				// 先删除旧的文档
				redisVectorStore.delete(docIds);
				// 添加新的文档
				redisVectorStore.add(documents);
				
				log.info("Successfully updated {} documents", documents.size());
			} catch (Exception e) {
				log.error("Failed to update documents", e);
				throw new RuntimeException("Failed to update documents", e);
			}
		});
	}

	// 删除一个docId
	public void deleteDoc(String docId) {
		deleteDoc(List.of(docId));
	}

	public void deleteDoc(List<String> docIdList) {
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.delete(docIdList));
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.delete(docIdList));
		}
	}

}
