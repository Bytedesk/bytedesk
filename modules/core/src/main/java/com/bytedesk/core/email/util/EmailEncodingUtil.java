/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-01 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 08:45:48
 * @Description: 邮件编码解码工具类
 */
package com.bytedesk.core.email.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮件编码解码工具类
 * 用于处理MIME编码的邮件头和邮件内容提取
 */
@Slf4j
public class EmailEncodingUtil {

    /**
     * 解码MIME编码的字符串
     * 处理 =?UTF-8?B?...?= 和 =?UTF-8?Q?...?= 格式
     */
    public static String decodeMimeString(String encodedString) {
        if (encodedString == null || encodedString.trim().isEmpty()) {
            return "";
        }

        try {
            // 使用JavaMail的MimeUtility来解码
            return MimeUtility.decodeText(encodedString);
        } catch (Exception e) {
            log.warn("Failed to decode MIME string: {}, error: {}", encodedString, e.getMessage());
            return encodedString;
        }
    }

    /**
     * 提取并解码发件人地址
     */
    public static String extractFromAddress(Message message) {
        try {
            if (message.getFrom() != null && message.getFrom().length > 0) {
                Address fromAddress = message.getFrom()[0];
                if (fromAddress instanceof InternetAddress) {
                    InternetAddress internetAddress = (InternetAddress) fromAddress;
                    return internetAddress.getAddress();
                } else {
                    String addressStr = fromAddress.toString();
                    // 尝试从 "Name <email@domain.com>" 格式中提取邮箱地址
                    return extractEmailFromString(addressStr);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract from address: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 提取并解码发件人姓名
     */
    public static String extractFromName(Message message) {
        try {
            if (message.getFrom() != null && message.getFrom().length > 0) {
                Address fromAddress = message.getFrom()[0];
                if (fromAddress instanceof InternetAddress) {
                    InternetAddress internetAddress = (InternetAddress) fromAddress;
                    String personal = internetAddress.getPersonal();
                    if (personal != null) {
                        return decodeMimeString(personal);
                    }
                }
                
                // 如果无法获取personal，尝试从完整字符串中提取
                String fromStr = fromAddress.toString();
                return extractNameFromString(fromStr);
            }
        } catch (Exception e) {
            log.warn("Failed to extract from name: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 从字符串中提取邮箱地址
     */
    public static String extractEmailFromString(String addressStr) {
        if (addressStr == null) {
            return "";
        }
        
        // 匹配 <email@domain.com> 格式
        Pattern pattern = Pattern.compile("<([^>]+)>");
        Matcher matcher = pattern.matcher(addressStr);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // 如果没有尖括号，直接返回原字符串
        return addressStr.trim();
    }

    /**
     * 从字符串中提取姓名
     */
    public static String extractNameFromString(String addressStr) {
        if (addressStr == null) {
            return "";
        }
        
        // 匹配 "Name <email@domain.com>" 格式
        Pattern pattern = Pattern.compile("^(.+?)\\s*<[^>]+>$");
        Matcher matcher = pattern.matcher(addressStr);
        if (matcher.find()) {
            String name = matcher.group(1).trim();
            return decodeMimeString(name);
        }
        
        // 如果没有尖括号，返回原字符串
        return decodeMimeString(addressStr);
    }

    /**
     * 提取邮件文本内容
     */
    public static String extractTextContent(Message message) {
        try {
            if (message.isMimeType("text/plain")) {
                Object content = message.getContent();
                if (content instanceof String) {
                    return (String) content;
                } else if (content instanceof InputStream) {
                    return readInputStream((InputStream) content);
                }
            } else if (message.isMimeType("multipart/*")) {
                return extractTextFromMultipart(message);
            }
        } catch (Exception e) {
            log.warn("Failed to extract text content: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 提取邮件HTML内容
     */
    public static String extractHtmlContent(Message message) {
        try {
            if (message.isMimeType("text/html")) {
                Object content = message.getContent();
                if (content instanceof String) {
                    return (String) content;
                } else if (content instanceof InputStream) {
                    return readInputStream((InputStream) content);
                }
            } else if (message.isMimeType("multipart/*")) {
                return extractHtmlFromMultipart(message);
            }
        } catch (Exception e) {
            log.warn("Failed to extract HTML content: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 从多部分邮件中提取文本内容
     */
    private static String extractTextFromMultipart(Message message) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            return extractTextFromMultipart(multipart);
        } catch (Exception e) {
            log.warn("Failed to extract text from multipart: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 从多部分邮件中提取HTML内容
     */
    private static String extractHtmlFromMultipart(Message message) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            return extractHtmlFromMultipart(multipart);
        } catch (Exception e) {
            log.warn("Failed to extract HTML from multipart: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 递归处理多部分邮件，提取文本内容
     */
    private static String extractTextFromMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder textContent = new StringBuilder();
        
        for (int i = 0; i < multipart.getCount(); i++) {
            Part part = multipart.getBodyPart(i);
            
            if (part.isMimeType("text/plain")) {
                Object content = part.getContent();
                if (content instanceof String) {
                    textContent.append((String) content);
                } else if (content instanceof InputStream) {
                    textContent.append(readInputStream((InputStream) content));
                }
            } else if (part.isMimeType("multipart/*")) {
                textContent.append(extractTextFromMultipart((Multipart) part.getContent()));
            }
        }
        
        return textContent.toString();
    }

    /**
     * 递归处理多部分邮件，提取HTML内容
     */
    private static String extractHtmlFromMultipart(Multipart multipart) throws MessagingException, IOException {
        StringBuilder htmlContent = new StringBuilder();
        
        for (int i = 0; i < multipart.getCount(); i++) {
            Part part = multipart.getBodyPart(i);
            
            if (part.isMimeType("text/html")) {
                Object content = part.getContent();
                if (content instanceof String) {
                    htmlContent.append((String) content);
                } else if (content instanceof InputStream) {
                    htmlContent.append(readInputStream((InputStream) content));
                }
            } else if (part.isMimeType("multipart/*")) {
                htmlContent.append(extractHtmlFromMultipart((Multipart) part.getContent()));
            }
        }
        
        return htmlContent.toString();
    }

    /**
     * 从InputStream读取内容
     */
    private static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }

    /**
     * 提取并解码收件人地址
     */
    public static String extractToAddresses(Message message) {
        try {
            if (message.getAllRecipients() != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < message.getAllRecipients().length; i++) {
                    if (i > 0) sb.append(",");
                    Address recipient = message.getAllRecipients()[i];
                    if (recipient instanceof InternetAddress) {
                        InternetAddress internetAddress = (InternetAddress) recipient;
                        String address = internetAddress.getAddress();
                        String personal = internetAddress.getPersonal();
                        if (personal != null) {
                            // 如果有姓名，格式化为 "姓名 <邮箱>" 或直接返回邮箱
                            String decodedPersonal = decodeMimeString(personal);
                            sb.append(decodedPersonal).append(" <").append(address).append(">");
                        } else {
                            sb.append(address);
                        }
                    } else {
                        // 对于非InternetAddress类型，尝试解码整个字符串
                        String addressStr = recipient.toString();
                        sb.append(decodeMimeString(addressStr));
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("Failed to extract to addresses: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 提取并解码抄送地址
     */
    public static String extractCcAddresses(Message message) {
        try {
            if (message.getRecipients(Message.RecipientType.CC) != null) {
                StringBuilder sb = new StringBuilder();
                Address[] ccAddresses = message.getRecipients(Message.RecipientType.CC);
                for (int i = 0; i < ccAddresses.length; i++) {
                    if (i > 0) sb.append(",");
                    Address ccAddress = ccAddresses[i];
                    if (ccAddress instanceof InternetAddress) {
                        InternetAddress internetAddress = (InternetAddress) ccAddress;
                        String address = internetAddress.getAddress();
                        String personal = internetAddress.getPersonal();
                        if (personal != null) {
                            String decodedPersonal = decodeMimeString(personal);
                            sb.append(decodedPersonal).append(" <").append(address).append(">");
                        } else {
                            sb.append(address);
                        }
                    } else {
                        String addressStr = ccAddress.toString();
                        sb.append(decodeMimeString(addressStr));
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("Failed to extract CC addresses: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 提取并解码密送地址
     */
    public static String extractBccAddresses(Message message) {
        try {
            if (message.getRecipients(Message.RecipientType.BCC) != null) {
                StringBuilder sb = new StringBuilder();
                Address[] bccAddresses = message.getRecipients(Message.RecipientType.BCC);
                for (int i = 0; i < bccAddresses.length; i++) {
                    if (i > 0) sb.append(",");
                    Address bccAddress = bccAddresses[i];
                    if (bccAddress instanceof InternetAddress) {
                        InternetAddress internetAddress = (InternetAddress) bccAddress;
                        String address = internetAddress.getAddress();
                        String personal = internetAddress.getPersonal();
                        if (personal != null) {
                            String decodedPersonal = decodeMimeString(personal);
                            sb.append(decodedPersonal).append(" <").append(address).append(">");
                        } else {
                            sb.append(address);
                        }
                    } else {
                        String addressStr = bccAddress.toString();
                        sb.append(decodeMimeString(addressStr));
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("Failed to extract BCC addresses: {}", e.getMessage());
        }
        return "";
    }

    /**
     * 检查邮件是否有附件
     */
    public static boolean hasAttachments(Message message) {
        try {
            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                return hasAttachmentsInMultipart(multipart);
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to check attachments: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 递归检查多部分邮件中是否有附件
     */
    private static boolean hasAttachmentsInMultipart(Multipart multipart) throws MessagingException {
        for (int i = 0; i < multipart.getCount(); i++) {
            Part part = multipart.getBodyPart(i);
            
            // 检查是否为附件
            String disposition = part.getDisposition();
            if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || 
                Part.INLINE.equalsIgnoreCase(disposition)) {
                return true;
            }
            
            // 检查文件名
            String fileName = part.getFileName();
            if (fileName != null && !fileName.trim().isEmpty()) {
                return true;
            }
            
            // 递归检查子部分
            if (part.isMimeType("multipart/*")) {
                try {
                    if (hasAttachmentsInMultipart((Multipart) part.getContent())) {
                        return true;
                    }
                } catch (IOException e) {
                    log.warn("Failed to check attachments in multipart: {}", e.getMessage());
                }
            }
        }
        return false;
    }
} 