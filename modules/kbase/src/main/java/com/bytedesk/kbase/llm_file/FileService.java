/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 09:08:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-28 16:51:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;

import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 解析文件内容
 * https://tika.apache.org/3.2.1/formats.html
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
				// 先用 PDF 文本解析，若为空将自动回退 OCR
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
			} else if (isImageExt(ext)) {
				// 图片文件不在 FileService 中处理，已在 FileEventListener 中过滤
				// 此处作为兜底机制，返回空文档列表
				log.debug("图片文件跳过 FileService 处理（兜底机制）: {}", actualFileName);
				documents = new ArrayList<>();
			} else if (isTikaPreferred(ext)) {
				documents = readByTika(resource);
			} else {
				// 兜底
				documents = readByTika(resource);
			}

			if (documents == null || documents.isEmpty() || allBlank(documents)) {
				log.warn("Primary reader produced empty content, fallback to Tika. fileName={}", actualFileName);
				// 对 PDF 优先尝试 OCR，图片文件跳过
				if (ext.endsWith(".pdf")) {
					documents = readByTikaWithOcr(resource, getOcrLanguage());
				} else if (isImageExt(ext)) {
					// 图片文件不进行 fallback OCR 处理
					log.debug("图片文件跳过 fallback OCR 处理（兜底机制）: {}", actualFileName);
					documents = new ArrayList<>();
				} else {
					documents = readByTika(resource);
				}
			}
		} catch (Exception ex) {
			log.warn("Primary reader failed, fallback to Tika. fileName={}, error={}", actualFileName, ex.getMessage());
			// 异常时对 PDF 优先尝试 OCR，图片文件跳过
			if (ext.endsWith(".pdf")) {
				documents = readByTikaWithOcr(resource, getOcrLanguage());
			} else if (isImageExt(ext)) {
				// 图片文件异常时也不进行 OCR 处理
				log.debug("图片文件异常时跳过 OCR 处理（兜底机制）: {}", actualFileName);
				documents = new ArrayList<>();
			} else {
				documents = readByTika(resource);
			}
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
	 * 使用 Apache Tika + Tesseract 进行 OCR 解析，适用扫描 PDF 或图片。
	 * 依赖系统已安装 tesseract 可执行程序与对应语言数据，默认使用 chi_sim+eng。
	 */
	public List<Document> readByTikaWithOcr(Resource resource, String ocrLanguage) {
		List<Document> docs = new ArrayList<>();
		try (InputStream is = resource.getInputStream()) {
			AutoDetectParser parser = new AutoDetectParser();
			ParseContext context = new ParseContext();

			// 配置 Tesseract OCR
			TesseractOCRConfig tessConfig = new TesseractOCRConfig();
			if (ocrLanguage != null && !ocrLanguage.isBlank()) {
				tessConfig.setLanguage(ocrLanguage);
			}
			log.info("Tika OCR enabled, language={}", (ocrLanguage == null || ocrLanguage.isBlank()) ? "default" : ocrLanguage);
			context.set(TesseractOCRConfig.class, tessConfig);

			// 针对 PDF：开启 OCR 与内嵌图像提取
			PDFParserConfig pdfConfig = new PDFParserConfig();
			pdfConfig.setExtractInlineImages(true);
			pdfConfig.setOcrStrategy(PDFParserConfig.OCR_STRATEGY.OCR_AND_TEXT_EXTRACTION);
			context.set(PDFParserConfig.class, pdfConfig);

			BodyContentHandler handler = new BodyContentHandler(-1);
			Metadata metadata = new Metadata();
			parser.parse(is, handler, metadata, context);

			String content = handler.toString();
			Map<String, Object> meta = new HashMap<>();
			for (String name : metadata.names()) {
				meta.put(name, metadata.get(name));
			}
			docs.add(new Document(content, meta));
		} catch (Exception e) {
			log.warn("Tika OCR parse failed: {}", e.getMessage());
			// 失败时回退普通 Tika 解析
			return readByTika(resource);
		}
		return docs;
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

	private boolean isImageExt(String ext) {
		if (ext == null) return false;
		switch (ext) {
			case ".png":
			case ".jpg":
			case ".jpeg":
			case ".bmp":
			case ".gif":
			case ".tif":
			case ".tiff":
			case ".webp":
			case ".heic":
			case ".heif":
				return true;
			default:
				return false;
		}
	}

	/**
	 * 返回 OCR 使用的 Tesseract 语言设置，默认中英混合。
	 * 可后续改为读取配置或环境变量。
	 */
	private String getOcrLanguage() {
		String fromEnv = System.getenv("OCR_LANGUAGE");
		if (fromEnv != null && !fromEnv.isBlank()) {
			return fromEnv.trim();
		}
		return "chi_sim+eng";
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
		tikaExts.add(".z"); tikaExts.add(".lzma"); tikaExts.add(".lz4"); tikaExts.add(".snappy"); tikaExts.add(".br");
		// 邮件与消息
		tikaExts.add(".mbox"); tikaExts.add(".eml"); tikaExts.add(".msg"); tikaExts.add(".pst"); tikaExts.add(".tnef"); tikaExts.add(".winmail");
		// 文本类
		tikaExts.add(".csv"); tikaExts.add(".tsv");
		// 多媒体（可抽取元数据/有限文本）
		tikaExts.add(".mp3"); tikaExts.add(".mp4"); tikaExts.add(".mov"); tikaExts.add(".m4v");
		tikaExts.add(".ogg"); tikaExts.add(".opus"); tikaExts.add(".flac"); tikaExts.add(".wav"); tikaExts.add(".aiff");
		tikaExts.add(".jpg"); tikaExts.add(".jpeg"); tikaExts.add(".png"); tikaExts.add(".gif"); tikaExts.add(".bmp");
		tikaExts.add(".tif"); tikaExts.add(".tiff"); tikaExts.add(".webp"); tikaExts.add(".psd"); tikaExts.add(".heic"); tikaExts.add(".heif"); tikaExts.add(".icns"); tikaExts.add(".jxl"); tikaExts.add(".bpg");
		// 其他
		tikaExts.add(".chm"); tikaExts.add(".wmf"); tikaExts.add(".emf"); tikaExts.add(".dgn"); tikaExts.add(".dwg");
		tikaExts.add(".dbf"); tikaExts.add(".mdb"); tikaExts.add(".accdb"); tikaExts.add(".sqlite");
		tikaExts.add(".java"); tikaExts.add(".class"); tikaExts.add(".jar");
		tikaExts.add(".xps"); tikaExts.add(".xlf"); tikaExts.add(".xliff"); tikaExts.add(".xlz");
		tikaExts.add(".wacz"); tikaExts.add(".warc");
		// Executables – include common Unix/Darwin Mach-O indicators by extension, though Tika focuses on magic detection
		tikaExts.add(".dylib"); tikaExts.add(".bundle"); tikaExts.add(".kext");

		return tikaExts.contains(ext);
	}

    
}
