package com.bytedesk.video.janus;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.video.janus.client.plugins.audiobridge.models.AudioBridgeRoom;
import com.bytedesk.video.janus.client.plugins.videoroom.models.CreateRoomResponse;
import com.bytedesk.video.janus.client.plugins.videoroom.models.VideoRoom;
import com.bytedesk.video.janus.utils.ServerInfo;

public class JanusGatewayServiceImpl implements IJanusGatewayService {

    @Override
    public ServerInfo info() {
        return null;
    }

    @Override
    public List<VideoRoom> listVideoRooms() {
        return null;
    }

    @Override
    public CreateRoomResponse createVideoRoom(Long room, String description, Integer publishers, String secret, String pin, Boolean permanent) {
        return null;
    }

    @Override
    public void destroyVideoRoom(long room, String secret, Boolean permanent) {

    }

    @Override
    public List<AudioBridgeRoom> listAudioRooms() {
        return null;
    }

    @Override
    public AudioBridgeRoom createAudioRoom(Long room, String description, String secret, String pin, Boolean permanent) {
        return null;
    }

    @Override
    public void destroyAudioRoom(long room, String secret, Boolean permanent) {

    }

    @Override
    public JSONObject adminPing() {
        return null;
    }

    @Override
    public ServerInfo adminInfo() {
        return null;
    }

    @Override
    public List<Long> adminListSessions() {
        return null;
    }

    @Override
    public List<Long> adminListHandles(long sessionId) {
        return null;
    }

    @Override
    public String openSipContext() {
        return null;
    }

    @Override
    public JSONObject registerSip(String contextId, String username, String secret, String server) {
        return null;
    }

    @Override
    public JSONObject unregisterSip(String contextId) {
        return null;
    }

    @Override
    public JSONObject sipCall(String contextId, String phoneNumber, String sdp) {
        return null;
    }

    @Override
    public JSONObject sipAccept(String contextId, String sdp) {
        return null;
    }

    @Override
    public JSONObject sipDecline(String contextId, Integer code) {
        return null;
    }

    @Override
    public JSONObject sipHangup(String contextId) {
        return null;
    }

    @Override
    public void closeSipContext(String contextId) {

    }
    
}
