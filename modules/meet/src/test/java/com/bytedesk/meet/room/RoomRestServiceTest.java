package com.bytedesk.meet.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.relation.RelationEntity;
import com.bytedesk.core.relation.RelationRepository;
import com.bytedesk.core.relation.RelationTypeEnum;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class RoomRestServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private PermissionService permissionService;

    private RoomRestService roomRestService;

    @BeforeEach
    void setUp() {
        roomRestService = new RoomRestService(
                roomRepository,
                relationRepository,
                modelMapper,
                uidUtils,
                authService,
                permissionService);
    }

    @Test
    void getVisibleRoomUidsForUserIncludesCreatedJoinedAndInvitedRooms() {
        RoomEntity createdRoom = RoomEntity.builder().uid("room-created").build();
        RoomEntity duplicatedRoom = RoomEntity.builder().uid("room-joined").build();

        RelationEntity joinedRoom = RelationEntity.builder()
                .objectContentUid("room-joined")
                .build();
        RelationEntity joinedByLinkRoom = RelationEntity.builder()
                .objectContentUid("room-invite")
                .build();
        RelationEntity invitedRoom = RelationEntity.builder()
                .objectContentUid("room-assigned")
                .build();
        RelationEntity invalidRelation = RelationEntity.builder()
                .objectContentUid(" ")
                .build();

        when(roomRepository.findByUserUidAndDeletedFalse("user-1"))
                .thenReturn(List.of(createdRoom, duplicatedRoom));
        when(relationRepository.findBySubjectUserUidAndTypeAndDeletedFalse("user-1", RelationTypeEnum.ROOM.name()))
                .thenReturn(List.of(joinedRoom, joinedByLinkRoom, invalidRelation));
        when(relationRepository.findByObjectUserUidAndTypeAndDeletedFalse("user-1", RelationTypeEnum.ROOM.name()))
                .thenReturn(List.of(invitedRoom));

        Set<String> visibleRoomUids = roomRestService.getVisibleRoomUidsForUser("user-1");

        assertThat(visibleRoomUids).containsExactly("room-created", "room-joined", "room-invite", "room-assigned");
    }

    @Test
    void recordRoomParticipationCreatesRelationForJoinedRoom() {
        UserEntity user = UserEntity.builder().uid("user-1").build();
        RoomEntity room = RoomEntity.builder().uid("room-1").orgUid("org-1").inviteUid("meet-1").build();

        when(authService.getUser()).thenReturn(user);
        when(roomRepository.findByInviteUid("meet-1")).thenReturn(Optional.of(room));
        when(relationRepository.findBySubjectUserUidAndObjectContentUidAndTypeAndDeletedFalse(
                "user-1", "room-1", RelationTypeEnum.ROOM.name()))
                .thenReturn(Optional.empty());
        when(uidUtils.getUid()).thenReturn("rel-1");

        roomRestService.recordRoomParticipation("meet-1");

        ArgumentCaptor<RelationEntity> captor = ArgumentCaptor.forClass(RelationEntity.class);
        verify(relationRepository).save(captor.capture());
        RelationEntity saved = captor.getValue();
        assertThat(saved.getUid()).isEqualTo("rel-1");
        assertThat(saved.getOrgUid()).isEqualTo("org-1");
        assertThat(saved.getUserUid()).isEqualTo("user-1");
        assertThat(saved.getSubjectUserUid()).isEqualTo("user-1");
        assertThat(saved.getObjectContentUid()).isEqualTo("room-1");
        assertThat(saved.getType()).isEqualTo(RelationTypeEnum.ROOM.name());
        assertThat(saved.getSource()).isEqualTo("MEET_JOIN");
    }
}