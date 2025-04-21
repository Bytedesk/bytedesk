/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-17 09:53:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-21 14:54:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.vector_store;

// import java.util.List;
// import java.util.Map;

// import org.springframework.ai.document.Document;
// import org.springframework.ai.vectorstore.SearchRequest;

// /**
//  * 向量数据库服务接口
//  * 定义向量数据库的基本操作
//  */
// public interface VectorDBService {
    
//     /**
//      * 将文档添加到向量数据库
//      * 
//      * @param documents 待添加的文档列表
//      * @param metadataFields 需要作为过滤条件的元数据字段
//      * @return 成功添加的文档数量
//      */
//     int addDocuments(List<Document> documents, List<String> metadataFields);
    
//     /**
//      * 根据文本查询相似文档
//      * 
//      * @param query 查询文本
//      * @param k 返回结果数量
//      * @return 相似文档列表
//      */
//     List<Document> similaritySearch(String query, int k);
    
//     /**
//      * 带过滤条件的相似度搜索
//      * 
//      * @param query 查询文本
//      * @param k 返回结果数量
//      * @param filter 过滤条件
//      * @return 相似文档列表
//      */
//     List<Document> similaritySearch(String query, int k, Map<String, Object> filter);
    
//     /**
//      * 高级搜索，使用SearchRequest进行查询
//      * 
//      * @param searchRequest 搜索请求
//      * @return 相似文档列表
//      */
//     List<Document> search(SearchRequest searchRequest);
    
//     /**
//      * 删除文档
//      * 
//      * @param ids 文档ID列表
//      * @return 成功删除的文档数量
//      */
//     int delete(List<String> ids);
    
//     /**
//      * 清空向量库中的所有文档
//      * 
//      * @return 是否清空成功
//      */
//     boolean clear();
    
//     /**
//      * 获取当前向量库中的文档数量
//      * 
//      * @return 文档数量
//      */
//     long count();
    
//     /**
//      * 获取向量数据库类型
//      * 
//      * @return 向量数据库类型名称
//      */
//     String getType();
// }
