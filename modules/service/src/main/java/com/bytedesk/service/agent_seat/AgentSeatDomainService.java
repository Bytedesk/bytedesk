package com.bytedesk.service.agent_seat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.bytedesk.core.member.MemberRequest;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.service.agent.AgentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentSeatDomainService {

    @Value("${bytedesk.service.agent-seat-enabled:false}")
    private boolean agentSeatEnabled;

    private final AgentSeatRepository agentSeatRepository;
    private final AgentRepository agentRepository;
    private final MemberRestService memberRestService;
    // private final UidUtils uidUtils;

    public boolean isSeatEnabled() {
        return agentSeatEnabled;
    }

    public boolean hasManagedSeats(String orgUid) {
        if (!isSeatEnabled()) {
            return false;
        }
        return agentSeatRepository.countByOrgUidAndDeletedFalse(orgUid) > 0;
    }

    public boolean hasAvailableSeat(String orgUid) {
        if (!isSeatEnabled()) {
            return false;
        }
        refreshSeatState(orgUid);
        return findAllocatableSeat(orgUid).isPresent();
    }

    @Transactional
    public Optional<AgentSeatEntity> findManagedSeatByAgentUid(String agentUid) {
        if (!StringUtils.hasText(agentUid) || !isSeatEnabled()) {
            return Optional.empty();
        }
        return agentSeatRepository.findByAssignedAgentUidAndDeletedFalse(agentUid)
                .map(seat -> agentSeatRepository.save(refreshSeatStatus(seat)));
    }

    @Transactional(readOnly = true)
    public Map<String, ZonedDateTime> findSeatExpireMapByAgentUids(Collection<String> agentUids) {
        Map<String, ZonedDateTime> expireMap = new HashMap<>();
        if (!isSeatEnabled() || agentUids == null || agentUids.isEmpty()) {
            return expireMap;
        }

        agentSeatRepository.findByAssignedAgentUidInAndDeletedFalse(agentUids).forEach(seat -> {
            if (StringUtils.hasText(seat.getAssignedAgentUid())) {
                expireMap.put(seat.getAssignedAgentUid(), seat.getExpireAt());
            }
        });
        return expireMap;
    }

    @Transactional
    public int countActiveSeatsForOrg(String orgUid) {
        if (!isSeatEnabled() || !StringUtils.hasText(orgUid)) {
            return 0;
        }
        List<AgentSeatEntity> seats = new ArrayList<>(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc(orgUid));
        seats.forEach(this::refreshSeatStatus);
        return (int) seats.stream()
                .filter(seat -> !seat.isDeleted())
                .filter(seat -> !isExpired(seat.getExpireAt()))
                .count();
    }

    @Transactional
    public Optional<AgentSeatEntity> assignSeatForAgent(String orgUid, String memberUid, String agentUid) {
        if (!isSeatEnabled()) {
            return Optional.empty();
        }
        refreshSeatState(orgUid);

        Optional<AgentSeatEntity> existingSeat = agentSeatRepository.findByAssignedAgentUidAndDeletedFalse(agentUid);
        if (existingSeat.isPresent()) {
            AgentSeatEntity seat = refreshSeatStatus(existingSeat.get());
            return Optional.of(agentSeatRepository.save(seat));
        }

        Optional<AgentSeatEntity> seatOptional = findAllocatableSeat(orgUid);
        if (seatOptional.isEmpty()) {
            if (hasManagedSeats(orgUid)) {
                throw new RuntimeException("No available agent seat");
            }
            return Optional.empty();
        }

        AgentSeatEntity seat = seatOptional.get();
        seat.setAssignedAgentUid(agentUid);
        seat.setAssignedMemberUid(memberUid);
        seat.setAssignedAt(ZonedDateTime.now());
        seat.setReleasedAt(null);
        seat.setStatus(AgentSeatStatusEnum.OCCUPIED.name());
        return Optional.of(agentSeatRepository.save(seat));
    }

    @Transactional
    public void releaseSeatForAgent(String agentUid) {
        if (!isSeatEnabled()) {
            return;
        }
        Optional<AgentSeatEntity> seatOptional = agentSeatRepository.findByAssignedAgentUidAndDeletedFalse(agentUid);
        if (seatOptional.isEmpty()) {
            return;
        }

        AgentSeatEntity seat = seatOptional.get();
        seat.setAssignedAgentUid(null);
        seat.setAssignedMemberUid(null);
        seat.setAssignedAt(null);
        seat.setReleasedAt(ZonedDateTime.now());
        seat.setStatus(resolveSeatStatusAfterRelease(seat));
        agentSeatRepository.save(seat);
    }

    @Transactional
    public Optional<AgentSeatEntity> updateSeatExpireByAgentUid(String agentUid, ZonedDateTime expireAt) {
        if (!isSeatEnabled()) {
            return Optional.empty();
        }
        Optional<AgentSeatEntity> seatOptional = agentSeatRepository.findByAssignedAgentUidAndDeletedFalse(agentUid);
        if (seatOptional.isEmpty()) {
            return Optional.empty();
        }

        AgentSeatEntity seat = seatOptional.get();
        seat.setExpireAt(expireAt);
        refreshSeatStatus(seat);
        AgentSeatEntity savedSeat = agentSeatRepository.save(seat);
        refreshSeatState(savedSeat.getOrgUid());
        return Optional.of(savedSeat);
    }

    @Transactional
    public List<AgentSeatEntity> synchronizeShopSeats(String orgUid, String shopName
            // Integer baseAgentSeats, Integer extraAgentSeats, Integer maxAgents, ZonedDateTime editionExpireAt,
            // ZonedDateTime extraSeatExpireAt
        ) {
        // List<AgentSeatEntity> existingSeats = new ArrayList<>(agentSeatRepository
        //     .findByOrgUidAndDeletedFalseOrderByCreatedAtAsc(orgUid));
        List<AgentSeatEntity> changedSeats = new ArrayList<>();

        // int expectedBaseSeatCount = safeCount(baseAgentSeats);
        // int expectedExtraSeatCount = safeCount(extraAgentSeats);
        // int renewableExtraSeatCount = resolveRenewableExtraSeatCount(expectedBaseSeatCount, expectedExtraSeatCount, maxAgents);

        // changedSeats.addAll(syncSeatsBySource(orgUid, shopName, existingSeats, AgentSeatSourceEnum.BASE,
        //     expectedBaseSeatCount, expectedBaseSeatCount, true, editionExpireAt));
        // changedSeats.addAll(syncSeatsBySource(orgUid, shopName, existingSeats, AgentSeatSourceEnum.EXTRA,
        //     expectedExtraSeatCount, renewableExtraSeatCount, false, extraSeatExpireAt));

        refreshSeatState(orgUid);

        return changedSeats;
    }

    @Transactional
    public void refreshSeatState(String orgUid) {
        if (!isSeatEnabled() || !StringUtils.hasText(orgUid)) {
            return;
        }

        List<AgentSeatEntity> seats = new ArrayList<>(agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc(orgUid));
        if (seats.isEmpty()) {
            return;
        }

        for (AgentSeatEntity seat : seats) {
            String previousStatus = seat.getStatus();
            boolean previousDeleted = seat.isDeleted();
            ZonedDateTime previousReleasedAt = seat.getReleasedAt();

            refreshSeatStatus(seat);

            if (!nullSafeEquals(previousStatus, seat.getStatus())
                    || previousDeleted != seat.isDeleted()
                    || !nullSafeEquals(previousReleasedAt, seat.getReleasedAt())) {
                agentSeatRepository.save(seat);
            }
        }

    }

    private boolean nullSafeEquals(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
    }

    // private List<AgentSeatEntity> syncSeatsBySource(String orgUid, String shopName,
    //         List<AgentSeatEntity> existingSeats, AgentSeatSourceEnum source, int expectedCount, int renewableCount,
    //         boolean baseSeat, ZonedDateTime expireAt) {
    //     List<AgentSeatEntity> sourceSeats = existingSeats.stream()
    //             .filter(seat -> source.name().equals(seat.getSource()))
    //             .sorted(Comparator.comparing(AgentSeatEntity::getCreatedAt,
    //                     Comparator.nullsLast(Comparator.naturalOrder())))
    //             .toList();
    //     List<AgentSeatEntity> changed = new ArrayList<>();

    //     List<AgentSeatEntity> removableSeats = sourceSeats.size() > expectedCount
    //         ? sourceSeats.stream()
    //             .sorted(Comparator.comparing(AgentSeatEntity::getCreatedAt,
    //                 Comparator.nullsLast(Comparator.reverseOrder())))
    //             .limit(sourceSeats.size() - expectedCount)
    //             .toList()
    //         : List.of();
    //     List<AgentSeatEntity> renewableSeats = sourceSeats.stream()
    //             .filter(seat -> !removableSeats.contains(seat))
    //             .sorted(Comparator.comparing(AgentSeatEntity::getCreatedAt,
    //                     Comparator.nullsLast(Comparator.naturalOrder())))
    //             .limit(Math.max(renewableCount, 0))
    //             .toList();

    //     for (AgentSeatEntity seat : sourceSeats) {
    //         if (removableSeats.contains(seat)) {
    //             continue;
    //         }
    //         seat.setBaseSeat(baseSeat);
    //         if (renewableSeats.contains(seat)) {
    //             seat.setExpireAt(expireAt);
    //         }
    //         refreshSeatStatus(seat);
    //         changed.add(agentSeatRepository.save(seat));
    //     }

    //     if (sourceSeats.size() < expectedCount) {
    //         int start = sourceSeats.size();
    //         for (int index = start; index < expectedCount; index++) {
    //             boolean renewable = index < Math.max(renewableCount, 0);
    //             ZonedDateTime seatExpireAt = renewable ? expireAt : resolveInactiveSeatExpireAt(expireAt);
    //             AgentSeatEntity seat = AgentSeatEntity.builder()
    //                     .uid(uidUtils.getUid())
    //                     .orgUid(orgUid)
    //                     .seatNo(buildSeatNo(orgUid, source, index + 1))
    //                     .source(source.name())
    //                     .status(resolveInitialSeatStatus(seatExpireAt))
    //                     .baseSeat(baseSeat)
    //                     .expireAt(seatExpireAt)
    //                     .build();
    //             changed.add(agentSeatRepository.save(seat));
    //         }
    //     }

    //     if (sourceSeats.size() > expectedCount) {
    //         for (AgentSeatEntity seat : removableSeats) {
    //             recycleSeat(seat, "Seat recycled by shop policy update");
    //             changed.add(agentSeatRepository.save(seat));
    //         }
    //     }

    //     return changed;
    // }

    private Optional<AgentSeatEntity> findAllocatableSeat(String orgUid) {
        return agentSeatRepository.findByOrgUidAndDeletedFalseOrderByCreatedAtAsc(orgUid).stream()
                .map(this::refreshSeatStatus)
                .filter(this::isAllocatable)
                .sorted(Comparator
                        .comparing(AgentSeatEntity::getBaseSeat, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AgentSeatEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .findFirst();
    }

    private boolean isAllocatable(AgentSeatEntity seat) {
        return !StringUtils.hasText(seat.getAssignedAgentUid())
                && AgentSeatStatusEnum.AVAILABLE.name().equals(seat.getStatus());
    }

    private AgentSeatEntity refreshSeatStatus(AgentSeatEntity seat) {
        if (isExpired(seat.getExpireAt())) {
            seat.setStatus(AgentSeatStatusEnum.EXPIRED.name());
            if (StringUtils.hasText(seat.getAssignedAgentUid())) {
                deactivateAssignedAgent(seat, "Seat expired");
            }
            removeAssignedMemberFromOrg(seat);
            return seat;
        }
        if (StringUtils.hasText(seat.getAssignedAgentUid())) {
            seat.setStatus(AgentSeatStatusEnum.OCCUPIED.name());
        } else if (!AgentSeatStatusEnum.RECYCLED.name().equals(seat.getStatus())) {
            seat.setStatus(AgentSeatStatusEnum.AVAILABLE.name());
        }
        return seat;
    }

    // private void recycleSeat(AgentSeatEntity seat, String reason) {
    //     seat.setDeleted(true);
    //     seat.setStatus(AgentSeatStatusEnum.RECYCLED.name());
    //     seat.setReleasedAt(ZonedDateTime.now());
    //     if (StringUtils.hasText(seat.getAssignedAgentUid())) {
    //         deactivateAssignedAgent(seat, reason);
    //     }
    //     removeAssignedMemberFromOrg(seat);
    // }

    private void deactivateAssignedAgent(AgentSeatEntity seat, String reason) {
        agentRepository.findByUid(seat.getAssignedAgentUid()).ifPresent(agent -> {
            agent.setEnabled(false);
            agent.setForceLogout(true);
            agent.setForceLogoutReason(reason);
            agent.setForceLogoutAt(ZonedDateTime.now());
            agentRepository.save(agent);
        });
    }

    private void removeAssignedMemberFromOrg(AgentSeatEntity seat) {
        if (!StringUtils.hasText(seat.getAssignedMemberUid())) {
            return;
        }

        memberRestService.findByUid(seat.getAssignedMemberUid())
                .filter(member -> !member.isDeleted())
                .ifPresent(member -> memberRestService.removeUserFromOrg(MemberRequest.builder()
                        .uid(member.getUid())
                        .orgUid(member.getOrgUid())
                        .build()));
    }

    private boolean isExpired(ZonedDateTime expireAt) {
        return expireAt != null && expireAt.isBefore(ZonedDateTime.now());
    }

    private String resolveSeatStatusAfterRelease(AgentSeatEntity seat) {
        return isExpired(seat.getExpireAt())
                ? AgentSeatStatusEnum.EXPIRED.name()
                : AgentSeatStatusEnum.AVAILABLE.name();
    }

    // private String resolveInitialSeatStatus(ZonedDateTime expireAt) {
    //     return isExpired(expireAt) ? AgentSeatStatusEnum.EXPIRED.name() : AgentSeatStatusEnum.AVAILABLE.name();
    // }

    // private int safeCount(Integer count) {
    //     return count == null ? 0 : Math.max(count, 0);
    // }

    // private int resolveRenewableExtraSeatCount(int expectedBaseSeatCount, int expectedExtraSeatCount, Integer maxAgents) {
    //     if (maxAgents == null) {
    //         return expectedExtraSeatCount;
    //     }
    //     int renewable = maxAgents - expectedBaseSeatCount;
    //     return Math.max(0, Math.min(expectedExtraSeatCount, renewable));
    // }

    // private ZonedDateTime resolveInactiveSeatExpireAt(ZonedDateTime expireAt) {
    //     ZonedDateTime expiredAt = ZonedDateTime.now().minusSeconds(1);
    //     if (expireAt == null) {
    //         return expiredAt;
    //     }
    //     return expireAt.isBefore(expiredAt) ? expireAt : expiredAt;
    // }

    // private String buildSeatNo(String orgUid, AgentSeatSourceEnum source, int index) {
    //     String prefix = StringUtils.hasText(orgUid) ? orgUid : "seat";
    //     return prefix + "-" + source.name().toLowerCase() + "-" + index;
    // }
}