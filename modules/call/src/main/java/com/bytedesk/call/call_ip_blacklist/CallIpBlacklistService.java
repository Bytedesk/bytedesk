package com.bytedesk.call.call_ip_blacklist;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.call.esl_event.EslEventEntity;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallIpBlacklistService {

    private static final Pattern IPV4_WITH_PORT = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+$");

    private final CallIpBlacklistRepository callIpBlacklistRepository;
    private final UidUtils uidUtils;

    @Transactional
    public CallIpBlacklistEntity blacklistSourceIp(EslEventEntity eslEvent) {
        String sourceIp = normalizeIp(eslEvent.getSourceIp());
        if (!StringUtils.hasText(sourceIp)) {
            throw new IllegalArgumentException("sourceIp is empty");
        }

        String orgUid = normalize(eslEvent.getOrgUid());
        return findExisting(orgUid, sourceIp)
            .orElseGet(() -> saveNew(eslEvent, sourceIp, orgUid));
    }

    @Transactional
    public CallIpBlacklistEntity blacklistSourceIp(String orgUid,
            String sourceIp,
            String eventName,
            String callerNumber,
            String sourceEslEventUid,
            String reason) {
        String normalizedIp = normalizeIp(sourceIp);
        if (!StringUtils.hasText(normalizedIp)) {
            throw new IllegalArgumentException("sourceIp is empty");
        }

        String normalizedOrgUid = normalize(orgUid);
        return findExisting(normalizedOrgUid, normalizedIp)
            .orElseGet(() -> saveNew(normalizedOrgUid, normalizedIp, eventName, callerNumber, sourceEslEventUid, reason));
    }

    public boolean isBlacklisted(String orgUid, String sourceIp) {
        String normalizedOrgUid = normalize(orgUid);
        String normalizedIp = normalizeIp(sourceIp);
        if (!StringUtils.hasText(normalizedIp)) {
            return false;
        }
        if (StringUtils.hasText(normalizedOrgUid)
                && !callIpBlacklistRepository.findAllByOrgUidAndIpAddressAndDeletedFalseOrderByIdAsc(normalizedOrgUid, normalizedIp).isEmpty()) {
            return true;
        }
        return !callIpBlacklistRepository.findAllByIpAddressAndDeletedFalseOrderByIdAsc(normalizedIp).isEmpty();
    }

    public List<String> findBlacklistedIps(Collection<String> orgUids) {
        if (orgUids == null || orgUids.isEmpty()) {
            return Collections.emptyList();
        }
        return callIpBlacklistRepository.findAllByOrgUidInAndDeletedFalse(orgUids).stream()
            .map(CallIpBlacklistEntity::getIpAddress)
            .filter(StringUtils::hasText)
            .map(this::normalizeIp)
            .filter(StringUtils::hasText)
            .distinct()
            .sorted()
            .toList();
    }

    public String normalizeIpAddress(String value) {
        return normalizeIp(value);
    }

    private Optional<CallIpBlacklistEntity> findExisting(String orgUid, String sourceIp) {
        if (StringUtils.hasText(orgUid)) {
            return callIpBlacklistRepository.findAllByOrgUidAndIpAddressAndDeletedFalseOrderByIdAsc(orgUid, sourceIp)
                    .stream()
                    .findFirst();
        }
        return callIpBlacklistRepository.findAllByIpAddressAndDeletedFalseOrderByIdAsc(sourceIp)
                .stream()
                .findFirst();
    }

    private CallIpBlacklistEntity saveNew(EslEventEntity eslEvent, String sourceIp, String orgUid) {
        return callIpBlacklistRepository.save(CallIpBlacklistEntity.builder()
            .uid(uidUtils.getUid())
            .orgUid(orgUid)
            .userUid(eslEvent.getUserUid())
            .level(StringUtils.hasText(eslEvent.getLevel()) ? eslEvent.getLevel() : LevelEnum.ORGANIZATION.name())
            .ipAddress(sourceIp)
            .sourceEslEventUid(eslEvent.getUid())
            .eventName(eslEvent.getEventName())
            .callerNumber(eslEvent.getCallerNumber())
            .reason(buildReason(eslEvent))
            .build());
    }

            private CallIpBlacklistEntity saveNew(String orgUid,
                String sourceIp,
                String eventName,
                String callerNumber,
                String sourceEslEventUid,
                String reason) {
            return callIpBlacklistRepository.save(CallIpBlacklistEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .level(StringUtils.hasText(orgUid) ? LevelEnum.ORGANIZATION.name() : LevelEnum.PLATFORM.name())
                .ipAddress(sourceIp)
                .sourceEslEventUid(normalize(sourceEslEventUid))
                .eventName(normalize(eventName))
                .callerNumber(normalize(callerNumber))
                .reason(buildReason(reason, eventName, callerNumber))
                .build());
            }

    private String buildReason(EslEventEntity eslEvent) {
        String eventName = normalize(eslEvent.getEventName());
        String callerNumber = normalize(eslEvent.getCallerNumber());
        StringBuilder builder = new StringBuilder("Blocked from ESL event");
        if (StringUtils.hasText(eventName)) {
            builder.append(' ').append(eventName);
        }
        if (StringUtils.hasText(callerNumber)) {
            builder.append(" caller=").append(callerNumber);
        }
        return builder.toString();
    }

    private String buildReason(String reason, String eventName, String callerNumber) {
        if (StringUtils.hasText(reason)) {
            return reason.trim();
        }
        String normalizedEventName = normalize(eventName);
        String normalizedCallerNumber = normalize(callerNumber);
        StringBuilder builder = new StringBuilder("Blocked from ESL event");
        if (StringUtils.hasText(normalizedEventName)) {
            builder.append(' ').append(normalizedEventName);
        }
        if (StringUtils.hasText(normalizedCallerNumber)) {
            builder.append(" caller=").append(normalizedCallerNumber);
        }
        return builder.toString();
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeIp(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.startsWith("[") && normalized.contains("]")) {
            normalized = normalized.substring(1, normalized.indexOf(']'));
        }
        if (IPV4_WITH_PORT.matcher(normalized).matches()) {
            normalized = normalized.substring(0, normalized.lastIndexOf(':'));
        }
        return normalized.toLowerCase(Locale.ROOT);
    }
}