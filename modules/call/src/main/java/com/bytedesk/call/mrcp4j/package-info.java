/**
 * MRCP4J - Media Resource Control Protocol v2 (MRCPv2) 实现库
 * 
 * <p>
 * 本包提供了 MRCP 协议的完整 Java 实现,用于与语音识别(ASR)和语音合成(TTS)服务器通信。
 * MRCP 是一种基于 SIP 和 RTP 的应用层协议,广泛用于 VoIP 和 IVR 系统。
 * </p>
 * 
 * <h2>主要组件</h2>
 * <ul>
 *   <li>{@link com.bytedesk.call.mrcp4j.client.MrcpFactory} - 客户端工厂类,用于创建 MRCP 会话</li>
 *   <li>{@link com.bytedesk.call.mrcp4j.client.MrcpChannel} - MRCP 通道接口,管理客户端与服务器的连接</li>
 *   <li>{@link com.bytedesk.call.mrcp4j.message.MrcpMessage} - MRCP 消息基类</li>
 *   <li>{@link com.bytedesk.call.mrcp4j.server.MrcpServerSocket} - MRCP 服务器实现</li>
 * </ul>
 * 
 * <h2>快速开始</h2>
 * <pre>{@code
 * // 创建 MRCP 客户端
 * MrcpFactory factory = MrcpFactory.newInstance();
 * MrcpChannel channel = factory.createChannel(
 *     "localhost", 
 *     1544, 
 *     MrcpResourceType.SPEECHRECOG
 * );
 * 
 * // 发送 RECOGNIZE 请求
 * MrcpRequest request = MrcpRequestFactory.createRecognizeRequest();
 * request.setContent(grammarContent);
 * MrcpResponse response = channel.sendRequest(request);
 * }</pre>
 * 
 * <h2>支持的资源类型</h2>
 * <ul>
 *   <li>speechrecog - 语音识别</li>
 *   <li>speechsynth - 语音合成</li>
 *   <li>recorder - 录音</li>
 *   <li>speakverify - 说话人验证</li>
 * </ul>
 * 
 * <h2>协议标准</h2>
 * <p>
 * 实现基于 IETF RFC 6787 - Media Resource Control Protocol Version 2 (MRCPv2)
 * </p>
 * 
 * @see <a href="https://tools.ietf.org/html/rfc6787">RFC 6787 - MRCPv2</a>
 * @see com.bytedesk.call.mrcp4j.client
 * @see com.bytedesk.call.mrcp4j.server
 * 
 * @author bytedesk.com
 * @version 1.0
 * @since 1.0
 */
@NonNullApi
package com.bytedesk.call.mrcp4j;

import org.springframework.lang.NonNullApi;
