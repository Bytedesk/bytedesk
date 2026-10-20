package com.bytedesk.meet.gateway;

import java.io.InputStream;
import java.util.Optional;

/**
 * 会议录制来源网关（2026-10-19 规划 §4.1）
 *
 * modules/meet 只定义契约；enterprise/webrtc 提供实现，把既有录制实体转成
 * 受控描述与后台文件读取入口。上层不得绕过网关直接拼文件路径。
 */
public interface MeetRecordingSourceGateway {

    /** 按录制 uid 读取描述（含组织/房间/来源/音轨范围/存储定位），不存在返回 empty */
    Optional<MeetRecordingDescriptor> findByUid(String recordingUid);

    MeetRecordingDescriptor requireVisible(String recordingUid);

    MeetRecordingDescriptor requireManageable(String recordingUid);

    boolean canManage(String recordingUid);

    boolean canExecuteAs(String recordingUid, String userUid, String orgUid);

    String requireRoomOrg(String roomUid);

    /** Lock the source row inside the caller's transaction before creating derived tasks. */
    void lock(String recordingUid);

    /**
     * 后台受控文件读取：使用已持久化的可信 orgUid/recordingUid 定位，
     * 不接受任意路径/URL；调用方负责流的生命周期。
     */
    InputStream openAudioStream(MeetRecordingDescriptor descriptor);

    /** 录制是否已被删除（派生材料失效判定） */
    boolean existsAndAvailable(String recordingUid);
}
