package com.bytedesk.video.janus;

import com.bytedesk.video.janus.client.plugins.audiobridge.models.AudioBridgeRoom;
import com.bytedesk.video.janus.client.plugins.videoroom.models.CreateRoomResponse;
import com.bytedesk.video.janus.client.plugins.videoroom.models.VideoRoom;
import com.bytedesk.video.janus.utils.ServerInfo;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public interface IJanusGatewayService {

	ServerInfo info();

	List<VideoRoom> listVideoRooms();

	CreateRoomResponse createVideoRoom(Long room, String description, Integer publishers, String secret, String pin, Boolean permanent);

	void destroyVideoRoom(long room, String secret, Boolean permanent);

	List<AudioBridgeRoom> listAudioRooms();

	AudioBridgeRoom createAudioRoom(Long room, String description, String secret, String pin, Boolean permanent);

	void destroyAudioRoom(long room, String secret, Boolean permanent);

	JSONObject adminPing();

	ServerInfo adminInfo();

	List<Long> adminListSessions();

	List<Long> adminListHandles(long sessionId);

	String openSipContext();

	JSONObject registerSip(String contextId, String username, String secret, String server);

	JSONObject unregisterSip(String contextId);

	JSONObject sipCall(String contextId, String phoneNumber, String sdp);

	JSONObject sipAccept(String contextId, String sdp);

	JSONObject sipDecline(String contextId, Integer code);

	JSONObject sipHangup(String contextId);

	void closeSipContext(String contextId);
}
