package com.bytedesk.call.mrcp4j.service;

import com.bytedesk.call.mrcp4j.client.*;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 * MRCP4J 简单示例
 * 演示基本的语音识别和语音合成功能
 * 
 * @author bytedesk.com
 */
@Slf4j
public class SimpleExample {

    private MrcpProvider provider;
    private MrcpChannel channel;

    /**
     * 初始化
     */
    public void init(String host, int port) throws Exception {
        MrcpFactory factory = MrcpFactory.newInstance();
        provider = factory.createProvider();
        
        InetAddress serverAddress = InetAddress.getByName(host);
        channel = provider.createChannel(
            "simple-channel", 
            serverAddress,
            port,
            MrcpProvider.PROTOCOL_TCP_MRCPv2
        );
        
        channel.addEventListener(new MrcpEventListener() {
            @Override
            public void eventReceived(MrcpEvent event) {
                log.info("收到事件: {}", event.getEventName());
                if (event.getContent() != null) {
                    log.debug("事件内容: {}", event.getContent());
                }
            }
        });
        
        log.info("MRCP客户端初始化完成");
    }

    /**
     * 执行语音识别
     */
    public void recognize() throws Exception {
        log.info("开始语音识别...");
        
        // 使用 channel 创建请求
        MrcpRequest request = channel.createVendorSpecificRequest("RECOGNIZE");
        
        // 设置语法
        String grammar =
            "<?xml version=\"1.0\"?>\n" +
            "<grammar xmlns=\"http://www.w3.org/2001/06/grammar\" " +
            "version=\"1.0\" xml:lang=\"zh-CN\" mode=\"voice\">\n" +
            "  <rule id=\"command\" scope=\"public\">\n" +
            "    <one-of>\n" +
            "      <item>查询余额</item>\n" +
            "      <item>转账</item>\n" +
            "      <item>人工服务</item>\n" +
            "    </one-of>\n" +
            "  </rule>\n" +
            "</grammar>";

        // 使用正确的参数顺序：contentType, contentId, content(String)
        request.setContent("application/srgs+xml", null, grammar);

        // 设置参数
        request.addHeader(MrcpHeaderName.CONFIDENCE_THRESHOLD.constructHeader("0.7"));
        request.addHeader(MrcpHeaderName.NO_INPUT_TIMEOUT.constructHeader("5000"));

        // 发送请求
        MrcpResponse response = channel.sendRequest(request);
        log.info("识别请求响应: {}", response.getStatusCode());
    }

    /**
     * 执行语音合成
     */
    public void synthesize() throws Exception {
        log.info("开始语音合成...");
        
        // 使用 channel 创建请求
        MrcpRequest request = channel.createVendorSpecificRequest("SPEAK");
        
        // 设置 SSML
        String ssml =
            "<?xml version=\"1.0\"?>\n" +
            "<speak version=\"1.0\" " +
            "xmlns=\"http://www.w3.org/2001/10/synthesis\" " +
            "xml:lang=\"zh-CN\">\n" +
            "  <prosody rate=\"medium\" pitch=\"medium\">\n" +
            "    欢迎使用语音服务系统\n" +
            "  </prosody>\n" +
            "</speak>";

        // 使用正确的参数顺序：contentType, contentId, content(String)
        request.setContent("application/ssml+xml", null, ssml);

        // 发送请求
        MrcpResponse response = channel.sendRequest(request);
        log.info("合成请求响应: {}", response.getStatusCode());
    }

    /**
     * 主函数示例
     */
    public static void main(String[] args) {
        SimpleExample example = new SimpleExample();
        
        try {
            // 初始化
            example.init("localhost", 1544);
            
            // 执行语音识别
            example.recognize();
            Thread.sleep(10000); // 等待识别完成
            
            // 执行语音合成
            example.synthesize();
            Thread.sleep(5000); // 等待合成完成
            
            log.info("示例执行完成");
            
        } catch (Exception e) {
            log.error("SimpleExample 执行异常", e);
        }
    }
}
