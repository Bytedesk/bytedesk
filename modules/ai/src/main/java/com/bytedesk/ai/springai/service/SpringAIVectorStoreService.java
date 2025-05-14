/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-27 21:27:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 14:11:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.service;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import com.bytedesk.kbase.config.KbaseConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAIVectorStoreService {

	// private final RedisVectorStore vectorStore;
	private final ElasticsearchVectorStore vectorStore;

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
		// List<Document> similarDocuments = bytedeskOllamaRedisVectorStore
		// 		.map(redisVectorStore -> redisVectorStore.similaritySearch(searchRequest))
		// 		.orElseGet(() -> {
		// 			// 如果bytedeskOllamaRedisVectorStore不存在，则尝试使用bytedeskZhipuaiRedisVectorStore
		// 			return bytedeskZhipuaiRedisVectorStore
		// 					.map(redisVectorStore -> redisVectorStore.similaritySearch(searchRequest))
		// 					.orElse(List.of());
		// 		});
		List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);
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
	@Transactional
	public void updateDoc(String docId, String content, String kbUid) {
		Assert.hasText(docId, "Document ID must not be empty");
		Assert.hasText(content, "Content must not be empty");
		Assert.hasText(kbUid, "Knowledge base UID must not be empty");
		log.info("updateDoc docId: {}, content: {}", docId, content);

		// 创建新的Document对象
		Document document = new Document(docId, content, Map.of(KbaseConst.KBASE_KB_UID, kbUid));
		// 更新向量存储
		vectorStore.delete(List.of(docId)); // 先删除旧的
		vectorStore.add(List.of(document)); // 再添加新的

	}

	// 删除一个docId
	public void deleteDoc(String docId) {
		Assert.hasText(docId, "Document ID must not be empty");
		deleteDocs(List.of(docId));
	}

	@Transactional
	public void deleteDocs(List<String> docIdList) {
		Assert.notEmpty(docIdList, "Document ID list must not be empty");
		// 删除向量存储中的文档
		vectorStore.delete(docIdList);
	}

}
