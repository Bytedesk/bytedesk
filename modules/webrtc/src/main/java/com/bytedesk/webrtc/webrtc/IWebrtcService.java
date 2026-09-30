package com.bytedesk.webrtc.webrtc;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.webrtc.webrtc.dto.WebrtcActionRequest;
import com.bytedesk.webrtc.webrtc.dto.WebrtcInviteRequest;
import com.bytedesk.webrtc.webrtc.dto.WebrtcRecordingContent;
import com.bytedesk.webrtc.webrtc.dto.WebrtcRecordingResponse;

public interface IWebrtcService {

    public WebrtcResponse invite(WebrtcInviteRequest request);

    public WebrtcResponse accept(WebrtcActionRequest request);
    
    public WebrtcResponse reject(WebrtcActionRequest request);

    public WebrtcResponse cancel(WebrtcActionRequest request);

    public WebrtcResponse hangup(WebrtcActionRequest request);

    public WebrtcResponse queryByUid(String callUid);

    public Map<String, Object> queryJanusStatus();

    public WebrtcRecordingResponse saveRecording(String callUid, String actorUid, Long duration, MultipartFile file);

    /**
     * 解析通话录制文件（管理端播放/下载用）：返回存储根目录内的规范化路径与派生 MIME。
     * 社区版 stub 保持不可用：default 实现直接抛 UnsupportedOperationException。
     */
    default WebrtcRecordingContent resolveRecordingContent(String callUid) {
        throw new UnsupportedOperationException("webrtc recording content is only available in enterprise edition");
    }

    /**
     * 受邀客服离开通话（规划 §5.2.1）：仅移除自身发布，不结束访客与其他客服之间的通话——
     * 主通话状态机与信令均不变，客户端直接 unpublish/destroy 即可，服务端仅做存在性校验。
     * 主叫/被叫（endpoint）调用时等同 hangup（保持既有语义）。
     * 社区版 stub 保持不可用：default 实现直接抛 UnsupportedOperationException。
     */
    default WebrtcResponse leave(WebrtcActionRequest request) {
        throw new UnsupportedOperationException("webrtc leave is only available in enterprise edition");
    }

}
