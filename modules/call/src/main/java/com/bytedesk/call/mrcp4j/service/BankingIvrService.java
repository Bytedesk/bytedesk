package com.bytedesk.call.mrcp4j.service;

import com.bytedesk.call.mrcp4j.client.*;
import com.bytedesk.call.mrcp4j.message.MrcpEvent;
import com.bytedesk.call.mrcp4j.message.MrcpResponse;
import com.bytedesk.call.mrcp4j.message.header.MrcpHeaderName;
import com.bytedesk.call.mrcp4j.message.request.MrcpRequest;

import java.net.InetAddress;

/**
 * 银行IVR业务示例
 * 完整的银行客服 IVR 流程实现
 * 
 * @author bytedesk.com
 */
public class BankingIvrService {

    private MrcpProvider asrProvider;
    private MrcpProvider ttsProvider;
    private MrcpChannel asrChannel;
    private MrcpChannel ttsChannel;
    
    private String recognizedIntent = null;
    private boolean recognitionComplete = false;
    private boolean synthesisComplete = false;

    /**
     * 初始化
     */
    public void init(String host, int port) throws Exception {
        MrcpFactory factory = MrcpFactory.newInstance();
        InetAddress serverAddress = InetAddress.getByName(host);
        
        // 初始化语音识别通道
        asrProvider = factory.createProvider();
        asrChannel = asrProvider.createChannel(
            "bank-asr-channel", 
            serverAddress,
            port,
            MrcpProvider.PROTOCOL_TCP_MRCPv2
        );
        asrChannel.addEventListener(new MrcpEventListener() {
            @Override
            public void eventReceived(MrcpEvent event) {
                handleAsrEvent(event);
            }
        });
        
        // 初始化语音合成通道
        ttsProvider = factory.createProvider();
        ttsChannel = ttsProvider.createChannel(
            "bank-tts-channel", 
            serverAddress,
            port,
            MrcpProvider.PROTOCOL_TCP_MRCPv2
        );
        ttsChannel.addEventListener(new MrcpEventListener() {
            @Override
            public void eventReceived(MrcpEvent event) {
                handleTtsEvent(event);
            }
        });
        
        System.out.println("银行IVR系统初始化完成");
    }

    /**
     * 执行IVR流程
     */
    public void executeIvrFlow() throws Exception {
        // 步骤1: 播放欢迎语
        playWelcome();
        waitForSynthesisComplete();
        
        // 步骤2: 识别用户意图
        recognizeIntent();
        waitForRecognitionComplete();
        
        // 步骤3: 处理用户请求
        processUserRequest();
    }

    /**
     * 播放欢迎语
     */
    private void playWelcome() throws Exception {
        System.out.println("播放欢迎语...");
        synthesisComplete = false;
        
        String welcomeText = "欢迎致电银行客户服务中心。请说出您需要的服务,比如:查询余额、转账、人工服务。";
        String ssml = buildSsml(welcomeText);
        
        MrcpRequest request = ttsChannel.createVendorSpecificRequest("SPEAK");
        request.setContent("application/ssml+xml", null, ssml);
        
        MrcpResponse response = ttsChannel.sendRequest(request);
        System.out.println("欢迎语播放请求响应: " + response.getStatusCode());
    }

    /**
     * 识别用户意图
     */
    private void recognizeIntent() throws Exception {
        System.out.println("开始识别用户意图...");
        recognitionComplete = false;
        
        String grammar = buildBankingGrammar();
        
        MrcpRequest request = asrChannel.createVendorSpecificRequest("RECOGNIZE");
        request.setContent("application/srgs+xml", null, grammar);
        request.addHeader(MrcpHeaderName.CONFIDENCE_THRESHOLD.constructHeader("0.6"));
        request.addHeader(MrcpHeaderName.NO_INPUT_TIMEOUT.constructHeader("5000"));
        request.addHeader(MrcpHeaderName.START_INPUT_TIMERS.constructHeader("true"));
        
        MrcpResponse response = asrChannel.sendRequest(request);
        System.out.println("识别请求响应: " + response.getStatusCode());
    }

    /**
     * 处理用户请求
     */
    private void processUserRequest() throws Exception {
        if (recognizedIntent == null) {
            System.out.println("未识别到用户意图");
            return;
        }
        
        System.out.println("处理用户意图: " + recognizedIntent);
        
        String responseText;
        if (recognizedIntent.contains("余额")) {
            responseText = "正在为您查询账户余额,请稍候。您的账户余额为一万两千三百四十五元。感谢使用,再见。";
        } else if (recognizedIntent.contains("转账")) {
            responseText = "转账服务需要您提供更多信息。正在为您转接人工服务,请稍候。";
        } else if (recognizedIntent.contains("人工")) {
            responseText = "正在为您转接人工客服,请稍候。当前排队人数较多,预计等待时间三分钟。";
        } else {
            responseText = "抱歉,没有听清您的需求。您可以说:查询余额、转账、人工服务等。";
        }
        
        // 播放响应
        synthesisComplete = false;
        String ssml = buildSsml(responseText);
        
        MrcpRequest request = ttsChannel.createVendorSpecificRequest("SPEAK");
        request.setContent("application/ssml+xml", null, ssml);
        
        MrcpResponse response = ttsChannel.sendRequest(request);
        System.out.println("响应播放请求响应: " + response.getStatusCode());
        
        waitForSynthesisComplete();
    }

    /**
     * 构建银行业务语法
     */
    private String buildBankingGrammar() {
        return "<?xml version=\"1.0\"?>\n" +
               "<grammar xmlns=\"http://www.w3.org/2001/06/grammar\" " +
               "version=\"1.0\" xml:lang=\"zh-CN\" mode=\"voice\">\n" +
               "  <rule id=\"banking_service\" scope=\"public\">\n" +
               "    <one-of>\n" +
               "      <item>查询余额</item>\n" +
               "      <item>查余额</item>\n" +
               "      <item>余额查询</item>\n" +
               "      <item>转账</item>\n" +
               "      <item>我要转账</item>\n" +
               "      <item>人工服务</item>\n" +
               "      <item>转人工</item>\n" +
               "      <item>人工客服</item>\n" +
               "    </one-of>\n" +
               "  </rule>\n" +
               "</grammar>";
    }

    /**
     * 构建SSML
     */
    private String buildSsml(String text) {
        return "<?xml version=\"1.0\"?>\n" +
               "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"zh-CN\">\n" +
               "  <prosody rate=\"medium\" pitch=\"medium\">" + text + "</prosody>\n" +
               "</speak>";
    }

    /**
     * 处理ASR事件
     */
    private void handleAsrEvent(MrcpEvent event) {
        String eventName = event.getEventName().toString();
        System.out.println("收到ASR事件: " + eventName);
        
        if ("START-OF-INPUT".equals(eventName)) {
            System.out.println("检测到语音输入开始");
        } else if ("RECOGNITION-COMPLETE".equals(eventName)) {
            recognizedIntent = event.getContent();
            System.out.println("识别完成,结果: " + recognizedIntent);
            recognitionComplete = true;
        } else if ("RECOGNITION-FAILED".equals(eventName)) {
            System.out.println("识别失败");
            recognitionComplete = true;
        }
    }

    /**
     * 处理TTS事件
     */
    private void handleTtsEvent(MrcpEvent event) {
        String eventName = event.getEventName().toString();
        System.out.println("收到TTS事件: " + eventName);
        
        if ("SPEAK-COMPLETE".equals(eventName)) {
            System.out.println("语音播放完成");
            synthesisComplete = true;
        } else if ("SPEAK-FAILED".equals(eventName)) {
            System.out.println("语音播放失败");
            synthesisComplete = true;
        }
    }

    /**
     * 等待识别完成
     */
    private void waitForRecognitionComplete() throws InterruptedException {
        int timeout = 30000; // 30秒超时
        int waited = 0;
        while (!recognitionComplete && waited < timeout) {
            Thread.sleep(100);
            waited += 100;
        }
        if (!recognitionComplete) {
            System.out.println("识别超时");
        }
    }

    /**
     * 等待合成完成
     */
    private void waitForSynthesisComplete() throws InterruptedException {
        int timeout = 30000; // 30秒超时
        int waited = 0;
        while (!synthesisComplete && waited < timeout) {
            Thread.sleep(100);
            waited += 100;
        }
        if (!synthesisComplete) {
            System.out.println("合成超时");
        }
    }

    /**
     * 主函数示例
     */
    public static void main(String[] args) {
        BankingIvrService example = new BankingIvrService();
        
        try {
            // 初始化
            example.init("localhost", 1544);
            
            // 执行IVR流程
            example.executeIvrFlow();
            
            System.out.println("IVR流程执行完成");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
