/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 09:08:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-28 16:27:13
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
// import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 解析文件内容
 * https://tika.apache.org/2.9.0/formats.html
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadRestService uploadRestService;

	/**
	 * https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
	 */
	public List<Document> parseFileContent(UploadEntity upload) {
		log.info("Loading document from upload: fileUrl={}, fileName={}", upload.getFileUrl(), upload.getFileName());
		
		// 使用新的简化方法直接从UploadEntity加载资源
		Resource resource = uploadRestService.loadAsResource(upload);
		
		// 从fileUrl或fileName中提取实际的文件名用于类型判断
		String actualFileName;
		if (upload.getFileUrl() != null && !upload.getFileUrl().isEmpty()) {
			String[] filePathList = upload.getFileUrl().split("/");
			actualFileName = filePathList[filePathList.length - 1];
		} else {
			actualFileName = upload.getFileName();
		}
		log.info("Using fileName for type detection: {}", actualFileName);

		List<Document> documents;
		String ext = getFileExtension(actualFileName);
		try {
			if (ext.endsWith(".pdf")) {
				documents = readPdfPage(resource);
			} else if (ext.endsWith(".json")) {
				documents = readJson(resource);
			} else if (ext.endsWith(".txt")) {
				documents = readTxt(resource);
			} else if (ext.endsWith(".md")) {
				documents = readMarkdown(resource);
			} else if (ext.endsWith(".html") || ext.endsWith(".htm") || ext.endsWith(".xhtml")) {
				// HTML 使用 Jsoup 解析，自动过滤脚本/样式
				documents = readHtml(resource);
			} else if (isTikaPreferred(ext)) {
				documents = readByTika(resource);
			} else {
				// 兜底
				documents = readByTika(resource);
			}

			if (documents == null || documents.isEmpty() || allBlank(documents)) {
				log.warn("Primary reader produced empty content, fallback to Tika. fileName={}", actualFileName);
				documents = readByTika(resource);
			}
		} catch (Exception ex) {
			log.warn("Primary reader failed, fallback to Tika. fileName={}, error={}", actualFileName, ex.getMessage());
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
	}

	public List<Document> readJson(Resource resource) {
		// log.info("Loading document from json: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".json"), String.format("File must end with .json, got %s", fileName));

		// Resource resource = uploadRestService.loadAsResource(fileName);
		JsonReader jsonReader = new JsonReader(resource, "description");

		return jsonReader.read();
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
	}

	public List<Document> readTxt(Resource resource) {
		// log.info("Loading document from txt: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.isTrue(fileName.endsWith(".txt"), String.format("File must end with .txt, got %s", fileName));

		TextReader textReader = new TextReader(resource);
		// textReader.getCustomMetadata().put("filename", fileName);

		return textReader.read();
	}

	// 新增：HTML 专用解析（过滤脚本/样式，保留正文结构）
	public List<Document> readHtml(Resource resource) {
		JsoupDocumentReader htmlReader = new JsoupDocumentReader(resource);
		return htmlReader.read();
	}

	// https://tika.apache.org/2.9.0/formats.html
	// PDF, DOC/DOCX, PPT/PPTX, and HTML
	public List<Document> readByTika(Resource resource) {
		// log.info("Loading document from tika: {}", fileName);
		// Assert.hasText(fileName, "File name must not be empty");
		// Assert.notNull(file, "FileEntity must not be null");

		TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
		return tikaDocumentReader.read();
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

	// ---------------------------------------------------------
	// 辅助方法
	// ---------------------------------------------------------

	private String getFileExtension(String fileName) {
		if (fileName == null) return "";
		String lower = fileName.toLowerCase(Locale.ROOT);
		int idx = lower.lastIndexOf('.');
		if (idx < 0) return "";
		return lower.substring(idx);
	}

	private boolean allBlank(List<Document> docs) {
		for (Document d : docs) {
			if (d != null && d.getText() != null && !d.getText().trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据扩展名判定是否优先交由 Tika 解析。
	 * 覆盖范围参考 Tika 2.9.0 支持清单（示例集合，非穷举）。
	 */
	private boolean isTikaPreferred(String ext) {
		if (ext == null || ext.isEmpty()) return true;
		Set<String> tikaExts = new HashSet<>();
		// Office OOXML / OLE2
		tikaExts.add(".doc"); tikaExts.add(".docx");
		tikaExts.add(".xls"); tikaExts.add(".xlsx");
		tikaExts.add(".ppt"); tikaExts.add(".pptx");
		tikaExts.add(".vsd"); tikaExts.add(".vsdx");
		tikaExts.add(".mpp");
		// OpenDocument / iWorks / RTF / EPUB / PDF（PDF 单独已处理）
		tikaExts.add(".odt"); tikaExts.add(".ods"); tikaExts.add(".odp");
		tikaExts.add(".pages"); tikaExts.add(".numbers"); tikaExts.add(".key");
		tikaExts.add(".rtf"); tikaExts.add(".epub");
		// XML / SVG（HTML 已由 Jsoup 处理）
		tikaExts.add(".xml"); tikaExts.add(".svg");
		// 压缩与打包
		tikaExts.add(".zip"); tikaExts.add(".jar"); tikaExts.add(".war"); tikaExts.add(".ear");
		tikaExts.add(".7z"); tikaExts.add(".rar"); tikaExts.add(".tar");
		tikaExts.add(".gz"); tikaExts.add(".bz2"); tikaExts.add(".xz");
		// 邮件与消息
		tikaExts.add(".mbox"); tikaExts.add(".eml"); tikaExts.add(".msg"); tikaExts.add(".pst"); tikaExts.add(".tnef"); tikaExts.add(".winmail");
		// 文本类
		tikaExts.add(".csv"); tikaExts.add(".tsv");
		// 多媒体（可抽取元数据/有限文本）
		tikaExts.add(".mp3"); tikaExts.add(".mp4"); tikaExts.add(".mov"); tikaExts.add(".m4v");
		tikaExts.add(".ogg"); tikaExts.add(".opus"); tikaExts.add(".flac"); tikaExts.add(".wav"); tikaExts.add(".aiff");
		tikaExts.add(".jpg"); tikaExts.add(".jpeg"); tikaExts.add(".png"); tikaExts.add(".gif"); tikaExts.add(".bmp");
		tikaExts.add(".tif"); tikaExts.add(".tiff"); tikaExts.add(".webp"); tikaExts.add(".psd"); tikaExts.add(".heic"); tikaExts.add(".heif"); tikaExts.add(".icns"); tikaExts.add(".jxl");
		// 其他
		tikaExts.add(".chm"); tikaExts.add(".wmf"); tikaExts.add(".emf");
		tikaExts.add(".dbf"); tikaExts.add(".mdb"); tikaExts.add(".accdb"); tikaExts.add(".sqlite");
		tikaExts.add(".java"); tikaExts.add(".class"); tikaExts.add(".jar");
		tikaExts.add(".xps");

		return tikaExts.contains(ext);
	}

    
}
