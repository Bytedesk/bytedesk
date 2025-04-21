/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 21:27:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-17 16:11:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

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
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.time.LocalDateTime;

import com.bytedesk.ai.vector_store.reader.WebDocumentReader;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.kbase.config.KbaseConst;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.llm.file.FileEntity;
import com.bytedesk.kbase.llm.file.FileRestService;
import com.bytedesk.kbase.llm.qa.QaEntity;
import com.bytedesk.kbase.llm.qa.QaRestService;
import com.bytedesk.kbase.llm.split.SplitRequest;
import com.bytedesk.kbase.llm.split.SplitRestService;
import com.bytedesk.kbase.llm.split.SplitStatusEnum;
import com.bytedesk.kbase.llm.split.SplitTypeEnum;
import com.bytedesk.kbase.llm.text.TextEntity;
import com.bytedesk.kbase.llm.text.TextRestService;
import com.bytedesk.kbase.llm.website.WebsiteEntity;
import com.bytedesk.kbase.llm.website.WebsiteRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;

@Slf4j
@Service
@AllArgsConstructor
public class SpringAIVectorService {

	private final Optional<RedisVectorStore> bytedeskOllamaRedisVectorStore;

	private final Optional<RedisVectorStore> bytedeskZhipuaiRedisVectorStore;

	private final FileRestService fileRestService;

	private final TextRestService textRestService;

	private final SplitRestService splitRestService;

	private final WebsiteRestService websiteRestService;

	private final FaqRestService faqRestService;

	private final QaRestService qaRestService;

	private final UploadRestService uploadRestService;

	/**
	 * https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
	 */
	public void readSplitWriteToVectorStore(@NonNull FileEntity file) {
		String fileUrl = file.getFileUrl();
		log.info("Loading document from URL: {}", fileUrl);
		Assert.hasText(fileUrl, "File URL must not be empty");
		Assert.isTrue(fileUrl.startsWith("http"), String.format("File URL must start with http, got %s", fileUrl));

		String filePathList[] = fileUrl.split("/");
		String fileName = filePathList[filePathList.length - 1];
		log.info("fileName {}", fileName);

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
		Assert.hasText(fileName, "File name must not be empty");
		Assert.isTrue(fileName.endsWith(".pdf"), String.format("File must end with .pdf, got %s", fileName));

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
		Assert.hasText(fileName, "File name must not be empty");
		Assert.isTrue(fileName.endsWith(".pdf"), String.format("File must end with .pdf, got %s", fileName));

		Resource resource = uploadRestService.loadAsResource(fileName);
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
		Assert.hasText(fileName, "File name must not be empty");
		Assert.isTrue(fileName.endsWith(".json"), String.format("File must end with .json, got %s", fileName));

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
		Assert.hasText(fileName, "File name must not be empty");
		Assert.isTrue(fileName.endsWith(".md"), String.format("File must end with .md, got %s", fileName));

		Resource resource = uploadRestService.loadAsResource(fileName);
		MarkdownDocumentReader markdownReader = new MarkdownDocumentReader(resource,
				MarkdownDocumentReaderConfig.builder().build());
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
		Assert.hasText(fileName, "File name must not be empty");
		Assert.isTrue(fileName.endsWith(".txt"), String.format("File must end with .txt, got %s", fileName));

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
		Assert.hasText(fileName, "File name must not be empty");
		Assert.notNull(file, "FileEntity must not be null");

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
	public List<Document> readTextDemo(String name, String content, String kbUid, String orgUid) {
		log.info("Converting string content to documents");
		Assert.hasText(content, "Content must not be empty");
		// 创建Document对象
		Document document = new Document(content);
		// 使用TokenTextSplitter分割文本
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(List.of(document));
		// 添加元数据: 文件file_uid, 知识库kb_uid
		docList.forEach(doc -> {
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, kbUid);
			// 添加元数据: 启用状态和有效期，对于简单文本，默认启用且无有效期限制
			doc.getMetadata().put("enabled", "true");
			doc.getMetadata().put("startDate", LocalDateTime.now().toString());
			doc.getMetadata().put("endDate", LocalDateTime.now().plusYears(100).toString());
			
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(name)
					.content(doc.getText())
					.type(SplitTypeEnum.TEXT.name())
					.docId(doc.getId())
					.kbUid(kbUid)
					.orgUid(orgUid)
					.enabled(true) // 默认启用
					.startDate(LocalDateTime.now()) // 默认从现在开始
					.endDate(LocalDateTime.now().plusYears(100)) // 默认有效期100年
					.build();
			splitRestService.create(splitRequest);
		});
		// log.info("Parsing document, this will take a while.");
		// bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		// if (!bytedeskOllamaRedisVectorStore.isPresent()) {
		// 	bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
		// }
		//
		return docList;
	}

	// 使用reader直接将content字符串，转换成 List<Document> documents
	public List<Document> readText(TextEntity textEntity) {
		log.info("Converting string content to documents");
		Assert.notNull(textEntity, "TextEntity must not be null");
		Assert.hasText(textEntity.getContent(), "Content must not be empty");
		// 创建Document对象
		Document document = new Document(textEntity.getContent());
		// 使用TokenTextSplitter分割文本
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(List.of(document));
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			// log.info("doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			// 添加元数据: 知识库kb_uid、启用状态、有效期
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, textEntity.getKbaseEntity().getUid());
			// doc.getMetadata().put("enabled", String.valueOf(textEntity.isEnabled()));
			// doc.getMetadata().put("startDate", textEntity.getStartDate() != null ? textEntity.getStartDate().toString() : LocalDateTime.now().toString());
			// doc.getMetadata().put("endDate", textEntity.getEndDate() != null ? textEntity.getEndDate().toString() : LocalDateTime.now().plusYears(100).toString());
			
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(textEntity.getName())
					.content(doc.getText())
					.type(SplitTypeEnum.TEXT.name())
					.docId(doc.getId())
					.typeUid(textEntity.getUid())
					.categoryUid(textEntity.getCategoryUid())
					.kbUid(textEntity.getKbaseEntity().getUid())
					.userUid(textEntity.getUserUid())
					.orgUid(textEntity.getOrgUid())
					.enabled(textEntity.isEnabled())
					.startDate(textEntity.getStartDate())
					.endDate(textEntity.getEndDate())
					.build();
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

	//
	public List<Document> readQa(QaEntity qaEntity) {
		log.info("Converting string content to documents");
		Assert.notNull(qaEntity, "QaEntity must not be null");
		//
		String content = qaEntity.getQuestion() + "\n" + qaEntity.getAnswer();
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
			// 添加元数据: 知识库kb_uid、启用状态、有效期
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, qaEntity.getKbaseEntity().getUid());
			// doc.getMetadata().put("enabled", String.valueOf(qaEntity.isEnabled()));
			// doc.getMetadata().put("startDate", qaEntity.getStartDate() != null ? qaEntity.getStartDate().toString() : LocalDateTime.now().toString());
			// doc.getMetadata().put("endDate", qaEntity.getEndDate() != null ? qaEntity.getEndDate().toString() : LocalDateTime.now().plusYears(100).toString());
			
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(qaEntity.getQuestion())
					.content(doc.getText())
					.type(SplitTypeEnum.QA.name())
					.docId(doc.getId())
					.typeUid(qaEntity.getUid())
					.categoryUid(qaEntity.getCategoryUid())
					.kbUid(qaEntity.getKbaseEntity().getUid())
					.userUid(qaEntity.getUserUid())
					.orgUid(qaEntity.getOrgUid())
					.enabled(qaEntity.isEnabled())
					.startDate(qaEntity.getStartDate() != null ? qaEntity.getStartDate() : LocalDateTime.now())
					.endDate(qaEntity.getEndDate() != null ? qaEntity.getEndDate() : LocalDateTime.now().plusYears(100))
					.build();
			splitRestService.create(splitRequest);
		}
		qaEntity.setDocIdList(docIdList);
		qaEntity.setStatus(SplitStatusEnum.SUCCESS.name());
		qaRestService.save(qaEntity);
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
		Assert.notNull(fqaEntity, "FaqEntity must not be null");
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
			// 添加元数据: 知识库kb_uid、启用状态、有效期
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, fqaEntity.getKbaseEntity().getUid());
			// doc.getMetadata().put("enabled", String.valueOf(fqaEntity.isEnabled()));
			// doc.getMetadata().put("startDate", fqaEntity.getStartDate() != null ? fqaEntity.getStartDate().toString() : LocalDateTime.now().toString());
			// doc.getMetadata().put("endDate", fqaEntity.getEndDate() != null ? fqaEntity.getEndDate().toString() : LocalDateTime.now().plusYears(100).toString());
			
			// 将doc写入到splitEntity
			SplitRequest splitRequest = SplitRequest.builder()
					.name(fqaEntity.getQuestion())
					.content(doc.getText())
					.type(SplitTypeEnum.FAQ.name())
					.docId(doc.getId())
					.typeUid(fqaEntity.getUid())
					.categoryUid(fqaEntity.getCategoryUid())
					.kbUid(fqaEntity.getKbaseEntity().getUid())
					.userUid(fqaEntity.getUserUid())
					.orgUid(fqaEntity.getOrgUid())
					.enabled(fqaEntity.isEnabled())
					.startDate(fqaEntity.getStartDate() != null ? fqaEntity.getStartDate() : LocalDateTime.now())
					.endDate(fqaEntity.getEndDate() != null ? fqaEntity.getEndDate() : LocalDateTime.now().plusYears(100))
					.build();
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
		Assert.notNull(websiteEntity, "WebsiteEntity must not be null");
		Assert.hasText(websiteEntity.getUrl(), "URL must not be empty");
		Assert.isTrue(websiteEntity.getUrl().startsWith("http"),
				String.format("URL must start with http, got %s", websiteEntity.getUrl()));
		//
		try {
			// 构建URI
			URI uri = UriComponentsBuilder.fromUriString(websiteEntity.getUrl()).build().toUri();
			// 创建元数据
			Map<String, String> metadata = new HashMap<>();
			metadata.put(KbaseConst.KBASE_KB_UID, websiteEntity.getKbaseEntity().getUid());
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
				// 添加元数据: 知识库kb_uid、启用状态、有效期
				doc.getMetadata().put(KbaseConst.KBASE_KB_UID, websiteEntity.getKbaseEntity().getUid());
				doc.getMetadata().put("enabled", String.valueOf(websiteEntity.isEnabled()));
				doc.getMetadata().put("startDate", websiteEntity.getStartDate() != null ? websiteEntity.getStartDate().toString() : LocalDateTime.now().toString());
				doc.getMetadata().put("endDate", websiteEntity.getEndDate() != null ? websiteEntity.getEndDate().toString() : LocalDateTime.now().plusYears(100).toString());
				
				// 将doc写入到splitEntity
				SplitRequest splitRequest = SplitRequest.builder()
						.name(websiteEntity.getName())
						.content(doc.getText())
						.type(SplitTypeEnum.WEBSITE.name())
						.docId(doc.getId())
						.typeUid(websiteEntity.getUid())
						.categoryUid(websiteEntity.getCategoryUid())
						.kbUid(websiteEntity.getKbaseEntity().getUid())
						.userUid(websiteEntity.getUserUid())
						.orgUid(websiteEntity.getOrgUid())
						.enabled(websiteEntity.isEnabled())
						.startDate(websiteEntity.getStartDate())
						.endDate(websiteEntity.getEndDate())
						.build();
				splitRestService.create(splitRequest);
			}
			// 如果需要存储到向量数据库
			if (websiteEntity.getKbaseEntity().getUid() != null) {
				bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.write(docList));
				log.info("Website content stored in vector store for kbUid: {}", websiteEntity.getKbaseEntity().getUid());
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
		Assert.notNull(docList, "Document list must not be null");
		Assert.notNull(file, "FileEntity must not be null");
		Assert.notNull(file.getUid(), "File UID must not be null");
		Assert.notNull(file.getKbaseEntity().getUid(), "Knowledge base UID must not be null");
		// 
		log.info("Parsing document, this will take a while. docList.size={}", docList.size());
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			log.info("doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, file.getUid());
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, file.getKbaseEntity().getUid());
			// 添加元数据: 启用状态和有效期，使用FileEntity中的字段
			// doc.getMetadata().put("enabled", String.valueOf(file.isEnabled()));
			// doc.getMetadata().put("startDate", file.getStartDate() != null ? file.getStartDate().toString() : LocalDateTime.now().toString());
			// doc.getMetadata().put("endDate", file.getEndDate() != null ? file.getEndDate().toString() : LocalDateTime.now().plusYears(100).toString());
			// 
			SplitRequest splitRequest = SplitRequest.builder()
					.name(file.getFileName())
					.content(doc.getText())
					.type(SplitTypeEnum.FILE.name())
					.docId(doc.getId())
					.typeUid(file.getUid())
					.categoryUid(file.getCategoryUid())
					.kbUid(file.getKbaseEntity().getUid())
					.userUid(file.getUserUid())
					.orgUid(file.getOrgUid())
					.enabled(file.isEnabled()) // 使用文件的启用状态，默认为true
					.startDate(file.getStartDate() != null ? file.getStartDate() : LocalDateTime.now()) // 使用文件的开始日期，默认为当前时间
					.endDate(file.getEndDate() != null ? file.getEndDate() : LocalDateTime.now().plusYears(100)) // 使用文件的结束日期，默认为100年后
					.build();
			splitRestService.create(splitRequest);
		}
		file.setDocIdList(docIdList);
		file.setStatus(SplitStatusEnum.SUCCESS.name());
		fileRestService.save(file);
		// 
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
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	// https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
	public List<String> searchText(String query, String kbUid) {
		Assert.hasText(query, "Search query must not be empty");
		Assert.hasText(kbUid, "Knowledge base UID must not be empty");

		log.info("searchText kbUid {}, query: {}", kbUid, query);
		
		// 构建过滤表达式，只搜索启用状态为true的内容和特定知识库
		FilterExpressionBuilder expressionBuilder = new FilterExpressionBuilder();
		Expression expression = expressionBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid).build();
		log.info("expression: {}", expression.toString());
		
		// 首先创建知识库过滤条件
		// FilterExpressionBuilder.Op kbUidOp = expressionBuilder.eq(KbaseConst.KBASE_KB_UID, kbUid);
		
		// 创建启用状态过滤：启用 或 无此字段
		// FilterExpressionBuilder.Op enabledTrueOp = expressionBuilder.eq("enabled", "true");
		// FilterExpressionBuilder.Op enabledNullOp = expressionBuilder.eq("enabled", null);
		// FilterExpressionBuilder.Op enabledOp = expressionBuilder.or(enabledTrueOp, enabledNullOp);
		
		// 注意：Redis向量存储不支持LTE和GTE操作符用于标签值
		// 我们只使用知识库ID和启用状态作为过滤条件
		// 日期过滤将在内存中进行后处理
		
		// 构建最终的过滤表达式
		// FilterExpressionBuilder.Op finalOp = expressionBuilder.and(kbUidOp, enabledOp);
		
		// 通过build()方法获取最终的Expression对象
		// Expression expression = finalOp.build();
		// log.info("expression: {}", expression.toString());

		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.filterExpression(expression)
				.build();
				
		// 首先尝试使用bytedeskOllamaRedisVectorStore
		List<Document> similarDocuments = bytedeskOllamaRedisVectorStore
				.map(redisVectorStore -> redisVectorStore.similaritySearch(searchRequest))
				.orElseGet(() -> {
					// 如果bytedeskOllamaRedisVectorStore不存在，则尝试使用bytedeskZhipuaiRedisVectorStore
					return bytedeskZhipuaiRedisVectorStore
							.map(redisVectorStore -> redisVectorStore.similaritySearch(searchRequest))
							.orElse(List.of());
				});
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		
		// 获取当前时间，用于内存中过滤日期
		// LocalDateTime currentTime = LocalDateTime.now();
		
		// // 在内存中过滤日期范围
		// List<Document> dateFilteredDocuments = similarDocuments.stream()
		// 		.filter(doc -> {
		// 			// 检查startDate
		// 			Object startDateObj = doc.getMetadata().get("startDate");
		// 			if (startDateObj != null) {
		// 				try {
		// 					String startDateStr = String.valueOf(startDateObj);
		// 					LocalDateTime startDate = LocalDateTime.parse(startDateStr);
		// 					if (startDate.isAfter(currentTime)) {
		// 						return false; // 如果开始日期在当前时间之后，则过滤掉
		// 					}
		// 				} catch (Exception e) {
		// 					log.warn("无法解析startDate: {}", startDateObj);
		// 				}
		// 			}
					
		// 			// 检查endDate
		// 			Object endDateObj = doc.getMetadata().get("endDate");
		// 			if (endDateObj != null) {
		// 				try {
		// 					String endDateStr = String.valueOf(endDateObj);
		// 					LocalDateTime endDate = LocalDateTime.parse(endDateStr);
		// 					if (endDate.isBefore(currentTime)) {
		// 						return false; // 如果结束日期在当前时间之前，则过滤掉
		// 					}
		// 				} catch (Exception e) {
		// 					log.warn("无法解析endDate: {}", endDateObj);
		// 				}
		// 			}
					
		// 			return true; // 通过日期验证
		// 		})
		// 		.toList();
				
		// List<String> contentList = dateFilteredDocuments.stream().map(Document::getText).toList();
		log.info("kbUid {}, query: {} , contentList.size: {}", kbUid, query, contentList.size());
		return contentList;
	}

	/**
	 * 更新向量存储中的文档内容
	 * 
	 * @param docId   文档ID
	 * @param content 新的文档内容
	 */
	public void updateDoc(String docId, String content, String kbUid) {
		Assert.hasText(docId, "Document ID must not be empty");
		Assert.hasText(content, "Content must not be empty");
		Assert.hasText(kbUid, "Knowledge base UID must not be empty");
		log.info("updateDoc docId: {}, content: {}", docId, content);
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> {
			try {
				// 创建新的Document对象
				Document document = new Document(docId, content, Map.of(KbaseConst.KBASE_KB_UID, kbUid));
				// 更新向量存储
				redisVectorStore.delete(List.of(docId)); // 先删除旧的
				redisVectorStore.add(List.of(document)); // 再添加新的

				log.info("Successfully updated document: {}", docId);
			} catch (Exception e) {
				log.error("Failed to update document: {}", docId, e);
				throw new RuntimeException("Failed to update document", e);
			}
		});
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> {
				try {
					// 创建新的Document对象
					Document document = new Document(docId, content, Map.of(KbaseConst.KBASE_KB_UID, kbUid));
					// 更新向量存储
					redisVectorStore.delete(List.of(docId)); // 先删除旧的
					redisVectorStore.add(List.of(document)); // 再添加新的

					log.info("Successfully updated document: {}", docId);
				} catch (Exception e) {
					log.error("Failed to update document: {}", docId, e);
					throw new RuntimeException("Failed to update document", e);
				}
			});
		}
	}

	// 删除一个docId
	public void deleteDoc(String docId) {
		Assert.hasText(docId, "Document ID must not be empty");
		deleteDocs(List.of(docId));
	}

	public void deleteDocs(List<String> docIdList) {
		Assert.notEmpty(docIdList, "Document ID list must not be empty");
		// 删除splitEntity
		splitRestService.deleteByDocList(docIdList);
		// 删除向量存储中的文档
		bytedeskOllamaRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.delete(docIdList));
		// 当二者都启用的情况下，优先使用ollama，否则使用zhipuai
		if (!bytedeskOllamaRedisVectorStore.isPresent()) {
			bytedeskZhipuaiRedisVectorStore.ifPresent(redisVectorStore -> redisVectorStore.delete(docIdList));
		}
	}

}
