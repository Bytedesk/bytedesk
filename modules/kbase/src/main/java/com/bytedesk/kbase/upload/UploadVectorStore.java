/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 21:27:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 18:05:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

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

import com.bytedesk.kbase.config.KbaseConst;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UploadVectorStore {

	private final RedisVectorStore vectorStore;

	private final UploadService uploadService;

	/**
	 * https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
	 * 
	 * @param fileUrl
	 */
	public void readSplitWriteToVectorStore(@NonNull UploadEntity upload) {
		if (!upload.getType().equals(UploadTypeEnum.LLM.name())) {
			return;
		}
		String fileUrl = upload.getFileUrl();
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
			readPdfPage(fileName, upload);
		} else if (fileName.toLowerCase().endsWith(".json")) {
			readJson(fileName, upload);
		} else if (fileName.toLowerCase().endsWith(".txt")) {
			readTxt(fileName, upload);
		} else {
			readByTika(fileName, upload);
		}
	}

	public void readPdfPage(String fileName, UploadEntity upload) {
		log.info("Loading document from pdfPage: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".pdf")) {
			throw new IllegalArgumentException(String.format("File URL must end with .pdf, got %s", fileName));
		}
		//
		Resource resource = uploadService.loadAsResource(fileName);
		//
		PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
				.withPageTopMargin(0)
				.withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
						.withNumberOfTopTextLinesToDelete(0)
						.build())
				.withPagesPerDocument(1)
				.build();
		PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);
		//
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(pdfReader.read());
		storeDocuments(docList, upload);
	}

	public void readPdfParagraph(String fileName, UploadEntity upload) {
		log.info("Loading document from pdfParagraph: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".pdf")) {
			throw new IllegalArgumentException(String.format("File URL must end with .pdf, got %s", fileName));
		}
		//
		Resource resource = uploadService.loadAsResource(fileName);
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
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(pdfReader.read());
		storeDocuments(docList, upload);
	}

	public void readJson(String fileName, UploadEntity upload) {
		log.info("Loading document from json: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".json")) {
			throw new IllegalArgumentException(String.format("File URL must end with .json, got %s", fileName));
		}
		//
		Resource resource = uploadService.loadAsResource(fileName);
		//
		JsonReader jsonReader = new JsonReader(resource, "description");
		//
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(jsonReader.read());
		storeDocuments(docList, upload);
	}

	public void readTxt(String fileName, UploadEntity upload) {
		log.info("Loading document from txt: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		if (!fileName.endsWith(".txt")) {
			throw new IllegalArgumentException(String.format("File URL must end with .txt, got %s", fileName));
		}
		//
		Resource resource = uploadService.loadAsResource(fileName);
		//
		TextReader textReader = new TextReader(resource);
		textReader.getCustomMetadata().put("filename", fileName);
		//
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(textReader.read());
		storeDocuments(docList, upload);
	}

	// https://tika.apache.org/2.9.0/formats.html
	// PDF, DOC/DOCX, PPT/PPTX, and HTML
	public void readByTika(String fileName, UploadEntity upload) {
		log.info("Loading document from tika: {}", fileName);
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File URL must not be empty");
		}
		//
		Resource resource = uploadService.loadAsResource(fileName);
		//
		TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
		//
		var tokenTextSplitter = new TokenTextSplitter();
		List<Document> docList = tokenTextSplitter.split(tikaDocumentReader.read());
		storeDocuments(docList, upload);
	}

	// 存储到vector store
	private void storeDocuments(List<Document> docList, UploadEntity upload) {
		// log.info("Parsing document, this will take a while.");
		List<String> docIdList = new ArrayList<>();
		Iterator<Document> iterator = docList.iterator();
		while (iterator.hasNext()) {
			Document doc = iterator.next();
			// log.info("doc id: {}", doc.getId());
			docIdList.add(doc.getId());
			// 添加元数据: 文件file_uid, 知识库kb_uid
			doc.getMetadata().put(KbaseConst.KBASE_FILE_UID, upload.getUid());
			doc.getMetadata().put(KbaseConst.KBASE_KB_UID, upload.getKbUid());
		}
		upload.setDocIdList(docIdList);
		upload.setStatus(UploadStatusEnum.PARSE_FILE_SUCCESS.name());
		// FIXME: ObjectOptimisticLockingFailureException: Row was updated or deleted by
		// another transaction (or unsaved-value mapping was incorrect) :
		// [com.bytedesk.kbase.upload.Upload#52]
		uploadService.save(upload);
		// log.info("Parsing document, this will take a while.");
		vectorStore.write(docList);
		log.info("Done parsing document, splitting, creating embeddings and storing in vector store");
		// TODO: 通知相关组件，文件处理成功
	}

	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	// https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
	public List<String> searchText(String query) {
		// Retrieve documents similar to a query
		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.topK(2)
				.build();
		List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		// TODO: 将 query, kbUid 对应的 contentList 缓存到Redis中，下次直接从Redis中取
		//
		return contentList;
	}

	// FIXME: kb_uid 没有起到过滤作用？
	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	public List<String> searchText(String query, String kbUid) {
		// log.info("searchText kbUid {}, query: {}", kbUid, query);
		// Retrieve documents similar to a query
		FilterExpressionBuilder b = new FilterExpressionBuilder();
		Expression expression = b.eq(KbaseConst.KBASE_KB_UID, kbUid).build();
		log.info("expression: {}", expression.toString());
		//
		SearchRequest searchRequest = SearchRequest.builder()
				.query(query)
				.filterExpression(expression)
				.build();
		// .withTopK(2);
		// .withSimilarityThreshold(0.5)
		// .withFilterExpression(expression);
		List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
		List<String> contentList = similarDocuments.stream().map(Document::getText).toList();
		log.info("kbUid {}, query: {} , contentList.size: {}", kbUid, query, contentList.size());
		//
		return contentList;
	}

	public void deleteDoc(List<String> docIdList) {
		vectorStore.delete(docIdList);
	}

	//
	// https://docs.spring.io/spring-ai/reference/api/vectordbs.html
	// https://docs.spring.io/spring-ai/reference/api/vectordbs/redis.html
	// public List<String> searchText(String query) {
	// // Retrieve documents similar to a query
	// SearchRequest searchRequest = SearchRequest.query(query).withTopK(2);
	// List<Document> similarDocuments =
	// vectorStore.similaritySearch(searchRequest);
	// List<String> contentList =
	// similarDocuments.stream().map(Document::getContent).toList();
	// //
	// return contentList;
	// }

}
