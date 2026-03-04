package com.bytedesk.video.webrtc;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.video.webrtc.dto.WebrtcActionRequest;
import com.bytedesk.video.webrtc.dto.WebrtcInviteRequest;
import com.bytedesk.video.webrtc.dto.WebrtcRecordingResponse;

public interface IWebrtcService {

    public WebrtcResponse invite(WebrtcInviteRequest request);

    public WebrtcResponse accept(WebrtcActionRequest request);
    
    public WebrtcResponse reject(WebrtcActionRequest request);

    public WebrtcResponse cancel(WebrtcActionRequest request);

    public WebrtcResponse hangup(WebrtcActionRequest request);

    public WebrtcResponse queryByUid(String callUid);

    public Map<String, Object> queryJanusStatus();

    public WebrtcRecordingResponse saveRecording(String callUid, String actorUid, MultipartFile file);

}
