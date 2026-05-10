package com.bytedesk.service.agent_seat;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.time.ZonedDateTime;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import com.bytedesk.core.member.MemberEntity;
// import com.bytedesk.core.member.MemberRequest;
// import com.bytedesk.core.member.MemberRestService;
// import com.bytedesk.core.uid.UidUtils;
// import com.bytedesk.service.agent.AgentEntity;
// import com.bytedesk.service.agent.AgentRepository;

// @ExtendWith(MockitoExtension.class)
// class AgentSeatDomainServiceTest {

//     @Mock
//     private AgentSeatRepository agentSeatRepository;

//     @Mock
//     private AgentRepository agentRepository;

//     @Mock
//     private MemberRestService memberRestService;

//     @Mock
//     private UidUtils uidUtils;

//     private AgentSeatDomainService agentSeatDomainService;

//     @BeforeEach
//     void setUp() {
//         agentSeatDomainService = new AgentSeatDomainService(agentSeatRepository, agentRepository, memberRestService, uidUtils);
//     }

//     @Test
//     void assignSeatForAgentShouldPreferOldestBaseSeat() {
//         AgentSeatEntity extraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .build();
//         extraSeat.setCreatedAt(ZonedDateTime.parse("2026-03-12T00:00:00+08:00"));

//         AgentSeatEntity baseSeat = AgentSeatEntity.builder()
//                 .uid("seat-base")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.BASE.name())
//                 .baseSeat(true)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .build();
//         baseSeat.setCreatedAt(ZonedDateTime.parse("2026-03-11T00:00:00+08:00"));

//         when(agentSeatRepository.findByAssignedAgentUidAndDeletedFalse("agent-1")).thenReturn(Optional.empty());
//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1"))
//                 .thenReturn(List.of(extraSeat, baseSeat));
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Optional<AgentSeatEntity> assigned = agentSeatDomainService.assignSeatForAgent("org-1", "member-1", "agent-1");

//         assertThat(assigned).isPresent();
//         assertThat(assigned.get().getUid()).isEqualTo("seat-base");
//         assertThat(assigned.get().getAssignedAgentUid()).isEqualTo("agent-1");
//         assertThat(assigned.get().getStatus()).isEqualTo(AgentSeatStatusEnum.OCCUPIED.name());
//     }

//     @Test
//     void synchronizeShopSeatsShouldCreateBaseAndExtraSeats() {
//         ZonedDateTime editionExpireAt = ZonedDateTime.parse("2027-03-10T00:00:00+08:00");
//         ZonedDateTime extraSeatExpireAt = ZonedDateTime.parse("2027-04-10T00:00:00+08:00");

//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1")).thenReturn(List.of());
//         when(uidUtils.getUid()).thenReturn("seat-1", "seat-2", "seat-3");
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         List<AgentSeatEntity> changed = agentSeatDomainService.synchronizeShopSeats(
//                 "org-1",
//                 "Demo Shop",
//                 1,
//                 2,
//                 3,
//                 editionExpireAt,
//                 extraSeatExpireAt);

//         assertThat(changed).hasSize(3);
//         assertThat(changed.stream().filter(AgentSeatEntity::getBaseSeat).count()).isEqualTo(1);
//         assertThat(changed.stream().filter(seat -> !Boolean.TRUE.equals(seat.getBaseSeat())).count()).isEqualTo(2);
//         assertThat(changed.stream()
//                 .filter(seat -> AgentSeatSourceEnum.EXTRA.name().equals(seat.getSource()))
//                 .allMatch(seat -> extraSeatExpireAt.equals(seat.getExpireAt())))
//                 .isTrue();
//     }

//     @Test
//     void synchronizeShopSeatsShouldRecycleNewestExtraSeatOnShrink() {
//         AgentSeatEntity baseSeat = AgentSeatEntity.builder()
//                 .uid("seat-base")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.BASE.name())
//                 .baseSeat(true)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .build();
//         baseSeat.setCreatedAt(ZonedDateTime.parse("2026-03-11T00:00:00+08:00"));

//         AgentSeatEntity extraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.OCCUPIED.name())
//                 .assignedAgentUid("agent-1")
//                 .build();
//         extraSeat.setCreatedAt(ZonedDateTime.parse("2026-04-11T00:00:00+08:00"));

//         AgentEntity agent = AgentEntity.builder().uid("agent-1").enabled(true).build();
//         MemberEntity member = MemberEntity.builder().uid("member-1").orgUid("org-1").build();

//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1"))
//                 .thenReturn(List.of(baseSeat, extraSeat), List.of(baseSeat));
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(agentRepository.findByUid("agent-1")).thenReturn(Optional.of(agent));
//         when(agentRepository.save(any(AgentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(member));

//         List<AgentSeatEntity> changed = agentSeatDomainService.synchronizeShopSeats(
//                 "org-1",
//                 "Demo Shop",
//                 1,
//                 0,
//                 1,
//                 null,
//                 null);

//         assertThat(changed).hasSize(2);
//         assertThat(extraSeat.isDeleted()).isTrue();
//         assertThat(extraSeat.getStatus()).isEqualTo(AgentSeatStatusEnum.RECYCLED.name());
//         assertThat(agent.getEnabled()).isFalse();
//         assertThat(agent.getForceLogout()).isTrue();
//         verify(agentRepository).save(agent);
//         verify(memberRestService).removeUserFromOrg(any(MemberRequest.class));
//         verify(agentSeatRepository, never()).findByAssignedAgentUidAndDeletedFalse("agent-1");
//     }

//     @Test
//     void synchronizeShopSeatsShouldOnlyRenewOldestExtraSeatsWithinMaxAgents() {
//         ZonedDateTime originalExpireAt = ZonedDateTime.parse("2027-04-10T00:00:00+08:00");
//         ZonedDateTime renewedExpireAt = ZonedDateTime.parse("2027-05-10T00:00:00+08:00");

//         AgentSeatEntity baseSeat = AgentSeatEntity.builder()
//                 .uid("seat-base")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.BASE.name())
//                 .baseSeat(true)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .build();
//         baseSeat.setCreatedAt(ZonedDateTime.parse("2026-03-10T00:00:00+08:00"));

//         AgentSeatEntity oldestExtraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra-1")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .expireAt(originalExpireAt)
//                 .build();
//         oldestExtraSeat.setCreatedAt(ZonedDateTime.parse("2026-03-11T00:00:00+08:00"));

//         AgentSeatEntity newestExtraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra-2")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .expireAt(originalExpireAt)
//                 .build();
//         newestExtraSeat.setCreatedAt(ZonedDateTime.parse("2026-04-11T00:00:00+08:00"));

//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1"))
//                 .thenReturn(List.of(baseSeat, oldestExtraSeat, newestExtraSeat));
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         List<AgentSeatEntity> changed = agentSeatDomainService.synchronizeShopSeats(
//                 "org-1",
//                 "Demo Shop",
//                 1,
//                 2,
//                 2,
//                 null,
//                 renewedExpireAt);

//         assertThat(changed).hasSize(3);
//         assertThat(oldestExtraSeat.getExpireAt()).isEqualTo(renewedExpireAt);
//         assertThat(newestExtraSeat.getExpireAt()).isEqualTo(originalExpireAt);
//     }

//     @Test
//     void synchronizeShopSeatsShouldRenewAllExtraSeatsWhenMaxAgentsIsNull() {
//         ZonedDateTime originalExpireAt = ZonedDateTime.parse("2027-04-10T00:00:00+08:00");
//         ZonedDateTime renewedExpireAt = ZonedDateTime.parse("2027-05-10T00:00:00+08:00");

//         AgentSeatEntity baseSeat = AgentSeatEntity.builder()
//                 .uid("seat-base")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.BASE.name())
//                 .baseSeat(true)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .build();
//         baseSeat.setCreatedAt(ZonedDateTime.parse("2026-03-10T00:00:00+08:00"));

//         AgentSeatEntity firstExtraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra-1")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .expireAt(originalExpireAt)
//                 .build();
//         firstExtraSeat.setCreatedAt(ZonedDateTime.parse("2026-03-11T00:00:00+08:00"));

//         AgentSeatEntity secondExtraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra-2")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .expireAt(originalExpireAt)
//                 .build();
//         secondExtraSeat.setCreatedAt(ZonedDateTime.parse("2026-04-11T00:00:00+08:00"));

//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1"))
//                 .thenReturn(List.of(baseSeat, firstExtraSeat, secondExtraSeat));
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         List<AgentSeatEntity> changed = agentSeatDomainService.synchronizeShopSeats(
//                 "org-1",
//                 "Demo Shop",
//                 1,
//                 2,
//                 null,
//                 null,
//                 renewedExpireAt);

//         assertThat(changed).hasSize(3);
//         assertThat(firstExtraSeat.getExpireAt()).isEqualTo(renewedExpireAt);
//         assertThat(secondExtraSeat.getExpireAt()).isEqualTo(renewedExpireAt);
//     }

//     @Test
//     void synchronizeShopSeatsShouldNotRenewExtraSeatsWhenMaxAgentsFallsBelowBaseSeats() {
//         ZonedDateTime originalExpireAt = ZonedDateTime.parse("2027-04-10T00:00:00+08:00");
//         ZonedDateTime renewedExpireAt = ZonedDateTime.parse("2027-05-10T00:00:00+08:00");

//         AgentSeatEntity baseSeat = AgentSeatEntity.builder()
//                 .uid("seat-base")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.BASE.name())
//                 .baseSeat(true)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .build();
//         baseSeat.setCreatedAt(ZonedDateTime.parse("2026-03-10T00:00:00+08:00"));

//         AgentSeatEntity extraSeat = AgentSeatEntity.builder()
//                 .uid("seat-extra-1")
//                 .orgUid("org-1")
//                 .source(AgentSeatSourceEnum.EXTRA.name())
//                 .baseSeat(false)
//                 .status(AgentSeatStatusEnum.AVAILABLE.name())
//                 .expireAt(originalExpireAt)
//                 .build();
//         extraSeat.setCreatedAt(ZonedDateTime.parse("2026-03-11T00:00:00+08:00"));

//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1"))
//                 .thenReturn(List.of(baseSeat, extraSeat));
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         List<AgentSeatEntity> changed = agentSeatDomainService.synchronizeShopSeats(
//                 "org-1",
//                 "Demo Shop",
//                 1,
//                 1,
//                 0,
//                 null,
//                 renewedExpireAt);

//         assertThat(changed).hasSize(2);
//         assertThat(extraSeat.getExpireAt()).isEqualTo(originalExpireAt);
//     }

//     @Test
//     void synchronizeShopSeatsShouldCreateOverflowExtraSeatsAsExpired() {
//         ZonedDateTime renewedExpireAt = ZonedDateTime.parse("2027-05-10T00:00:00+08:00");
//         ZonedDateTime beforeSync = ZonedDateTime.now();

//         when(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc("org-1")).thenReturn(List.of());
//         when(uidUtils.getUid()).thenReturn("seat-1", "seat-2", "seat-3");
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         List<AgentSeatEntity> changed = agentSeatDomainService.synchronizeShopSeats(
//                 "org-1",
//                 "Demo Shop",
//                 1,
//                 2,
//                 2,
//                 null,
//                 renewedExpireAt);

//         assertThat(changed).hasSize(3);
//         List<AgentSeatEntity> extraSeats = changed.stream()
//                 .filter(seat -> AgentSeatSourceEnum.EXTRA.name().equals(seat.getSource()))
//                 .toList();
//         assertThat(extraSeats).hasSize(2);
//         assertThat(extraSeats.stream().filter(seat -> renewedExpireAt.equals(seat.getExpireAt())).count()).isEqualTo(1);
//         AgentSeatEntity overflowSeat = extraSeats.stream()
//                 .filter(seat -> !renewedExpireAt.equals(seat.getExpireAt()))
//                 .findFirst()
//                 .orElseThrow();
//         assertThat(overflowSeat.getStatus()).isEqualTo(AgentSeatStatusEnum.EXPIRED.name());
//         assertThat(overflowSeat.getExpireAt()).isBeforeOrEqualTo(ZonedDateTime.now());
//         assertThat(overflowSeat.getExpireAt()).isAfter(beforeSync.minusSeconds(2));
//     }

//     @Test
//     void updateSeatExpireByAgentUidShouldRemoveAssignedMemberWhenSeatExpires() {
//         ZonedDateTime expiredAt = ZonedDateTime.now().minusDays(1);

//         AgentSeatEntity seat = AgentSeatEntity.builder()
//                 .uid("seat-1")
//                 .orgUid("org-1")
//                 .assignedAgentUid("agent-1")
//                 .status(AgentSeatStatusEnum.OCCUPIED.name())
//                 .build();
//         AgentEntity agent = AgentEntity.builder().uid("agent-1").enabled(true).build();
//         MemberEntity member = MemberEntity.builder().uid("member-1").orgUid("org-1").build();

//         when(agentSeatRepository.findByAssignedAgentUidAndDeletedFalse("agent-1")).thenReturn(Optional.of(seat));
//         when(agentSeatRepository.save(any(AgentSeatEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(agentRepository.findByUid("agent-1")).thenReturn(Optional.of(agent));
//         when(agentRepository.save(any(AgentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(memberRestService.findByUid("member-1")).thenReturn(Optional.of(member));

//         Optional<AgentSeatEntity> updated = agentSeatDomainService.updateSeatExpireByAgentUid("agent-1", expiredAt);

//         assertThat(updated).isPresent();
//         assertThat(updated.get().getStatus()).isEqualTo(AgentSeatStatusEnum.EXPIRED.name());
//         assertThat(agent.getEnabled()).isFalse();
//         assertThat(agent.getForceLogout()).isTrue();
//         verify(memberRestService).removeUserFromOrg(any(MemberRequest.class));
//     }
// }