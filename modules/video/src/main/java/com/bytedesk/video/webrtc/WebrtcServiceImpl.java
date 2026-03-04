package com.bytedesk.video.webrtc;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.video.webrtc.dto.WebrtcActionRequest;
import com.bytedesk.video.webrtc.dto.WebrtcInviteRequest;
import com.bytedesk.video.webrtc.dto.WebrtcRecordingResponse;

@Service
public class WebrtcServiceImpl implements IWebrtcService {

    @Override
    public WebrtcResponse invite(WebrtcInviteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'invite'");
    }

    @Override
    public WebrtcResponse accept(WebrtcActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accept'");
    }

    @Override
    public WebrtcResponse reject(WebrtcActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reject'");
    }

    @Override
    public WebrtcResponse cancel(WebrtcActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancel'");
    }

    @Override
    public WebrtcResponse hangup(WebrtcActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hangup'");
    }

    @Override
    public WebrtcResponse queryByUid(String callUid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public Map<String, Object> queryJanusStatus() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryJanusStatus'");
    }

    @Override
    public WebrtcRecordingResponse saveRecording(String callUid, String actorUid, MultipartFile file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveRecording'");
    }

    
    
}
