/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @LastEditors: bytedesk.com
 * @LastEditTime: 2025-01-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: support@bytedesk.com
 *  联系：support@bytedesk.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.conference.service;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.conference.dto.request.ConferenceCreateRequest;
// import com.bytedesk.conference.dto.request.ConferenceJoinRequest;
// import com.bytedesk.conference.dto.response.ConferenceResponse;
// import com.bytedesk.conference.dto.response.ParticipantResponse;
// import com.bytedesk.conference.entity.ConferenceEntity;
// import com.bytedesk.conference.entity.ConferenceEntity.ConferenceStatus;
// import com.bytedesk.conference.entity.ConferenceEntity.ConferenceType;
// import com.bytedesk.conference.entity.ParticipantEntity;
// import com.bytedesk.conference.entity.ParticipantEntity.ParticipantRole;
// import com.bytedesk.conference.entity.ParticipantEntity.ParticipantStatus;
// import com.bytedesk.conference.repository.ConferenceRepository;
// import com.bytedesk.conference.repository.ParticipantRepository;

// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Conference Service
//  *
//  * 会议服务类，处理会议相关的业务逻辑
//  */
// @Slf4j
// @Service
// @AllArgsConstructor
// public class ConferenceService {

//     @Autowired
//     private ConferenceRepository conferenceRepository;

//     @Autowired
//     private ParticipantRepository participantRepository;

//     @Autowired
//     private SimpMessagingTemplate messagingTemplate;

//     /**
//      * 创建快速会议
//      */
//     @Transactional
//     public ConferenceResponse createQuickMeeting(ConferenceCreateRequest request, String hostUid) {
//         log.info("Creating quick meeting for user: {}", hostUid);

//         // 构建会议实体
//         ConferenceEntity conference = ConferenceEntity.builder()
//             .uid(UUID.randomUUID().toString())
//             .topic(request.getTopic() != null ? request.getTopic() : "快速会议")
//             .description(request.getDescription())
//             .hostUid(hostUid)
//             .roomId(generateRoomId())
//             .password(request.getPassword())
//             .type(ConferenceType.QUICK_MEETING)
//             .status(ConferenceStatus.NOT_STARTED)
//             .startTime(LocalDateTime.now())
//             .endTime(LocalDateTime.now().plusMinutes(request.getDuration() != null ? request.getDuration() : 60))
//             .duration(request.getDuration() != null ? request.getDuration() : 60)
//             .maxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 100)
//             .currentParticipants(0)
//             .muteOnEntry(request.getMuteOnEntry() != null ? request.getMuteOnEntry() : false)
//             .videoOnEntry(request.getVideoOnEntry() != null ? request.getVideoOnEntry() : true)
//             .waitingRoomEnabled(request.getWaitingRoomEnabled() != null ? request.getWaitingRoomEnabled() : false)
//             .locked(false)
//             .recording(false)
//             .build();

//         // 保存会议
//         conference = conferenceRepository.save(conference);

//         log.info("Quick meeting created successfully: {}", conference.getUid());

//         // 转换为响应对象
//         return convertToResponse(conference);
//     }

//     /**
//      * 加入会议
//      */
//     @Transactional
//     public ConferenceResponse joinMeeting(ConferenceJoinRequest request, String userUid) {
//         log.info("User {} joining meeting: {}", userUid, request.getMeetingId());

//         // 查找会议
//         ConferenceEntity conference = conferenceRepository.findByUid(request.getMeetingId())
//             .orElseThrow(() -> new RuntimeException("Meeting not found"));

//         // 检查密码
//         if (conference.hasPassword()) {
//             if (!conference.getPassword().equals(request.getPassword())) {
//                 throw new RuntimeException("Invalid password");
//             }
//         }

//         // 检查会议是否已满
//         if (conference.isFull()) {
//             throw new RuntimeException("Meeting is full");
//         }

//         // 检查会议是否已锁定
//         if (conference.isLocked()) {
//             throw new RuntimeException("Meeting is locked");
//         }

//         // 创建参与者记录
//         ParticipantEntity participant = ParticipantEntity.builder()
//             .uid(UUID.randomUUID().toString())
//             .conferenceUid(conference.getUid())
//             .userUid(userUid)
//             .nickname(request.getNickname())
//             .role(ParticipantRole.ATTENDEE)
//             .status(ParticipantStatus.IN_LOBBY) // 如果启用等候室，则在等候室
//             .audioEnabled(request.getEnableAudio() != null ? request.getEnableAudio() : true)
//             .videoEnabled(request.getEnableVideo() != null ? request.getEnableVideo() : true)
//             .screenSharing(false)
//             .handRaised(false)
//             .joinTime(LocalDateTime.now())
//             .signalQuality(100)
//             .build();

//         // 如果是主持人或没有等候室，直接进入
//         if (userUid.equals(conference.getHostUid()) || !conference.getWaitingRoomEnabled()) {
//             participant.setStatus(ParticipantStatus.ONLINE);
//             conference.addParticipant(userUid);
//         }

//         // 保存参与者
//         participant = participantRepository.save(participant);
//         conference = conferenceRepository.save(conference);

//         // 如果会议未开始，则开始会议
//         if (conference.getStatus() == ConferenceStatus.NOT_STARTED) {
//             conference.setStatus(ConferenceStatus.IN_PROGRESS);
//             conference = conferenceRepository.save(conference);
//         }

//         // 发送WebSocket通知
//         messagingTemplate.convertAndSend("/topic/conference/" + conference.getUid() + "/participant-joined",
//             convertParticipantToResponse(participant));

//         log.info("User {} joined meeting {} successfully", userUid, conference.getUid());

//         return convertToResponse(conference);
//     }

//     /**
//      * 离开会议
//      */
//     @Transactional
//     public void leaveMeeting(String conferenceUid, String userUid) {
//         log.info("User {} leaving meeting: {}", userUid, conferenceUid);

//         // 查找会议
//         ConferenceEntity conference = conferenceRepository.findByUid(conferenceUid)
//             .orElseThrow(() -> new RuntimeException("Meeting not found"));

//         // 查找参与者
//         ParticipantEntity participant = participantRepository.findByConferenceUidAndUserUid(conferenceUid, userUid)
//             .orElseThrow(() -> new RuntimeException("Participant not found"));

//         // 更新参与者状态
//         participant.setStatus(ParticipantStatus.OFFLINE);
//         participant.setLeaveTime(LocalDateTime.now());
//         participantRepository.save(participant);

//         // 移除参与者
//         conference.removeParticipant(userUid);
//         conferenceRepository.save(conference);

//         // 发送WebSocket通知
//         messagingTemplate.convertAndSend("/topic/conference/" + conferenceUid + "/participant-left",
//             convertParticipantToResponse(participant));

//         log.info("User {} left meeting {} successfully", userUid, conferenceUid);
//     }

//     /**
//      * 结束会议
//      */
//     @Transactional
//     public void endMeeting(String conferenceUid, String userUid) {
//         log.info("Ending meeting: {} by user: {}", conferenceUid, userUid);

//         // 查找会议
//         ConferenceEntity conference = conferenceRepository.findByUid(conferenceUid)
//             .orElseThrow(() -> new RuntimeException("Meeting not found"));

//         // 检查是否是主持人
//         if (!conference.getHostUid().equals(userUid)) {
//             throw new RuntimeException("Only host can end the meeting");
//         }

//         // 更新会议状态
//         conference.setStatus(ConferenceStatus.ENDED);
//         conference.setEndTime(LocalDateTime.now());
//         conferenceRepository.save(conference);

//         // 更新所有参与者状态
//         List<ParticipantEntity> participants = participantRepository.findByConferenceUidOrderByJoinTimeAsc(conferenceUid);
//         participants.forEach(p -> {
//             p.setStatus(ParticipantStatus.OFFLINE);
//             p.setLeaveTime(LocalDateTime.now());
//         });
//         participantRepository.saveAll(participants);

//         // 发送WebSocket通知
//         messagingTemplate.convertAndSend("/topic/conference/" + conferenceUid + "/ended",
//             "Meeting ended");

//         log.info("Meeting {} ended successfully", conferenceUid);
//     }

//     /**
//      * 获取会议信息
//      */
//     public ConferenceResponse getMeetingInfo(String conferenceUid) {
//         ConferenceEntity conference = conferenceRepository.findByUid(conferenceUid)
//             .orElseThrow(() -> new RuntimeException("Meeting not found"));

//         ConferenceResponse response = convertToResponse(conference);

//         // 加载参与者列表
//         List<ParticipantEntity> participants = participantRepository.findByConferenceUidOrderByJoinTimeAsc(conferenceUid);
//         response.setParticipants(participants.stream()
//             .map(this::convertParticipantToResponse)
//             .toList());

//         return response;
//     }

//     /**
//      * 获取会议列表
//      */
//     public Page<ConferenceResponse> getMeetingList(String userUid, Pageable pageable) {
//         // TODO: 实现分页查询
//         return null;
//     }

//     /**
//      * 生成会议室ID
//      */
//     private String generateRoomId() {
//         return UUID.randomUUID().toString().substring(0, 8);
//     }

//     /**
//      * 转换为响应对象
//      */
//     private ConferenceResponse convertToResponse(ConferenceEntity conference) {
//         ConferenceResponse response = new ConferenceResponse();
//         response.setUid(conference.getUid());
//         response.setTopic(conference.getTopic());
//         response.setDescription(conference.getDescription());
//         response.setHostUid(conference.getHostUid());
//         response.setHostNickname(conference.getHostNickname());
//         response.setRoomId(conference.getRoomId());
//         response.setType(conference.getType());
//         response.setStatus(conference.getStatus());
//         response.setStartTime(conference.getStartTime());
//         response.setEndTime(conference.getEndTime());
//         response.setDuration(conference.getDuration());
//         response.setMaxParticipants(conference.getMaxParticipants());
//         response.setCurrentParticipants(conference.getCurrentParticipants());
//         response.setMuteOnEntry(conference.getMuteOnEntry());
//         response.setVideoOnEntry(conference.getVideoOnEntry());
//         response.setWaitingRoomEnabled(conference.getWaitingRoomEnabled());
//         response.setLocked(conference.getLocked());
//         response.setRecording(conference.getRecording());
//         response.setCreatedAt(conference.getCreatedAt());
//         response.setUpdatedAt(conference.getUpdatedAt());
//         response.setHasPassword(conference.hasPassword());
//         // TODO: 设置WebSocket URL
//         response.setWsUrl("wss://ws.bytedesk.com/conference/" + conference.getUid());
//         return response;
//     }

//     /**
//      * 转换参与者为响应对象
//      */
//     private ParticipantResponse convertParticipantToResponse(ParticipantEntity participant) {
//         ParticipantResponse response = new ParticipantResponse();
//         response.setUid(participant.getUid());
//         response.setConferenceUid(participant.getConferenceUid());
//         response.setUserUid(participant.getUserUid());
//         response.setNickname(participant.getNickname());
//         response.setAvatar(participant.getAvatar());
//         response.setEmail(participant.getEmail());
//         response.setPhone(participant.getPhone());
//         response.setRole(participant.getRole());
//         response.setStatus(participant.getStatus());
//         response.setAudioEnabled(participant.getAudioEnabled());
//         response.setVideoEnabled(participant.getVideoEnabled());
//         response.setScreenSharing(participant.getScreenSharing());
//         response.setHandRaised(participant.getHandRaised());
//         response.setJoinTime(participant.getJoinTime());
//         response.setDevice(participant.getDevice());
//         response.setSignalQuality(participant.getSignalQuality());
//         response.setDuration(participant.getDurationMinutes());
//         return response;
//     }
// }
