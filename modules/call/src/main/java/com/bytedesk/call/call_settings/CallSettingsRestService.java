package com.bytedesk.call.call_settings;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.call.config.CallConstants;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CallSettingsRestService extends BaseRestServiceWithExport<CallSettingsEntity, CallSettingsRequest, CallSettingsResponse, CallSettingsExcel> {

    private static final String DEFAULT_HOLD_MEDIA_URL = CallConstants.DEFAULT_HOLD_MEDIA_URL;
    private static final String LEGACY_DEFAULT_HOLD_MEDIA_URL = CallConstants.LEGACY_DEFAULT_HOLD_MEDIA_URL;
    private static final String LEGACY_LOCAL_STREAM_DEFAULT_HOLD_MEDIA_URL = CallConstants.LEGACY_LOCAL_STREAM_DEFAULT_HOLD_MEDIA_URL;
    private static final String LEGACY_LOCAL_STREAM_HOLD_MEDIA_URL = CallConstants.LEGACY_LOCAL_STREAM_HOLD_MEDIA_URL;
    private static final String LEGACY_LOCAL_STREAM_HOLD_MEDIA_8000_URL = CallConstants.LEGACY_LOCAL_STREAM_HOLD_MEDIA_8000_URL;
    private static final String LEGACY_TONE_STREAM_HOLD_MEDIA_URL = CallConstants.LEGACY_TONE_STREAM_HOLD_MEDIA_URL;
    private static final String DEFAULT_CONSULT_EXTENSION_NUMBERS = CallConstants.DEFAULT_CONSULT_EXTENSION_NUMBERS;
    private static final String DEFAULT_TRANSFER_TARGET_NUMBERS = CallConstants.DEFAULT_TRANSFER_TARGET_NUMBERS;
    private static final String DEFAULT_CONFERENCE_TARGET_NUMBERS = CallConstants.DEFAULT_CONFERENCE_TARGET_NUMBERS;
    private static final String DEFAULT_IVR_TARGET_NUMBERS = CallConstants.DEFAULT_IVR_TARGET_NUMBERS;

    private final CallSettingsRepository callSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CallSettingsEntity> queryByOrgEntity(CallSettingsRequest request) {
        Specification<CallSettingsEntity> specs = CallSettingsSpecification.search(request, authService);
        return callSettingsRepository.findAll(specs, request.getPageable());
    }

    @Override
    public Page<CallSettingsResponse> queryByOrg(CallSettingsRequest request) {
        return queryByOrgEntity(request).map(this::convertToResponse);
    }

    @Override
    protected Specification<CallSettingsEntity> createSpecification(CallSettingsRequest request) {
        return CallSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<CallSettingsEntity> executePageQuery(Specification<CallSettingsEntity> spec, Pageable pageable) {
        return callSettingsRepository.findAll(spec, pageable);
    }

    public CallSettingsResponse queryByAgentUid(CallSettingsRequest request) {
        if (!StringUtils.hasText(request.getAgentUid())) {
            return null;
        }
        return callSettingsRepository.findByAgentUidAndDeletedFalse(request.getAgentUid())
            .map(this::convertToResponse)
            .orElse(null);
    }

    @Override
    public Optional<CallSettingsEntity> findByUid(String uid) {
        return callSettingsRepository.findByUid(uid);
    }

    public Optional<CallSettingsEntity> findByAgentUid(String agentUid) {
        if (!StringUtils.hasText(agentUid)) {
            return Optional.empty();
        }
        return callSettingsRepository.findByAgentUidAndDeletedFalse(agentUid.trim());
    }

    @Transactional
    public void disableByTargetForOrg(String target, String orgUid) {
        if (!StringUtils.hasText(target) || !StringUtils.hasText(orgUid)) {
            return;
        }

        String targetExtension = extractExtensionNo(target);
        if (!StringUtils.hasText(targetExtension)) {
            return;
        }

        List<CallSettingsEntity> settingsList = callSettingsRepository.findAllByOrgUidAndDeletedFalse(orgUid.trim());
        for (CallSettingsEntity settings : settingsList) {
            String settingsExtension = extractExtensionNo(settings.getTarget());
            if (targetExtension.equals(settingsExtension) && !Boolean.FALSE.equals(settings.getEnabled())) {
                settings.setEnabled(false);
                callSettingsRepository.save(settings);
            }
        }
    }

    @Transactional
    public void syncRegistrationStatus(String extensionNo, String registrationUser, String orgUid, String registrationStatus) {
        if (!StringUtils.hasText(orgUid)) {
            return;
        }

        Set<String> extensionCandidates = buildExtensionCandidates(extensionNo, registrationUser);
        if (extensionCandidates.isEmpty()) {
            return;
        }

        List<CallSettingsEntity> settingsList = callSettingsRepository.findAllByOrgUidAndDeletedFalse(orgUid.trim());
        for (CallSettingsEntity settings : settingsList) {
            String settingsExtension = extractExtensionNo(settings.getTarget());
            if (StringUtils.hasText(settingsExtension) && extensionCandidates.contains(settingsExtension)) {
                settings.setRegistrationStatus(normalizeNullable(registrationStatus));
                callSettingsRepository.save(settings);
            }
        }
    }

    @Transactional
    @Override
    public CallSettingsResponse create(CallSettingsRequest request) {
        if (!StringUtils.hasText(request.getAgentUid())) {
            throw new RuntimeException("agent uid is required");
        }

        Optional<CallSettingsEntity> existing = findByAgentUid(request.getAgentUid());
        if (existing.isPresent()) {
            throw new RuntimeException("该客服已存在呼叫配置，请直接编辑现有配置");
        }

        CallSettingsEntity entity = CallSettingsEntity.fromRequest(request, modelMapper);
        entity.setUid(uidUtils.getUid());
        applyAuditContext(entity, request);
        normalizeCallControlSettings(entity);

        CallSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create call settings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CallSettingsResponse update(CallSettingsRequest request) {
        if (StringUtils.hasText(request.getUid())) {
            Optional<CallSettingsEntity> existing = findByUid(request.getUid());
            if (existing.isPresent()) {
                CallSettingsEntity entity = existing.get();
                Long originalId = entity.getId();
                String originalUid = entity.getUid();
                String originalHoldMediaUrl = entity.getHoldMediaUrl();
                String originalConsultExtensionNumbers = entity.getConsultExtensionNumbers();
                String originalTransferTargetNumbers = entity.getTransferTargetNumbers();
                String originalConferenceTargetNumbers = entity.getConferenceTargetNumbers();
                String originalIvrTargetNumbers = entity.getIvrTargetNumbers();
                Boolean originalSignedIn = entity.getSignedIn();
                modelMapper.map(request, entity);
                entity.setId(originalId);
                entity.setUid(originalUid);
                if (request.getSignedIn() == null) {
                    entity.setSignedIn(originalSignedIn);
                }
                if (request.getHoldMediaUrl() == null) {
                    entity.setHoldMediaUrl(originalHoldMediaUrl);
                }
                if (request.getConsultExtensionNumbers() == null) {
                    entity.setConsultExtensionNumbers(originalConsultExtensionNumbers);
                }
                if (request.getTransferTargetNumbers() == null) {
                    entity.setTransferTargetNumbers(originalTransferTargetNumbers);
                }
                if (request.getConferenceTargetNumbers() == null) {
                    entity.setConferenceTargetNumbers(originalConferenceTargetNumbers);
                }
                if (request.getIvrTargetNumbers() == null) {
                    entity.setIvrTargetNumbers(originalIvrTargetNumbers);
                }
                normalizeCallControlSettings(entity);
                applyAuditContext(entity, request);
                CallSettingsEntity savedEntity = save(entity);
                if (savedEntity == null) {
                    throw new RuntimeException("Update call settings failed");
                }
                return convertToResponse(savedEntity);
            }
        }

        if (StringUtils.hasText(request.getAgentUid())) {
            Optional<CallSettingsEntity> existing = findByAgentUid(request.getAgentUid());
            if (existing.isPresent()) {
                CallSettingsEntity entity = existing.get();
                Long originalId = entity.getId();
                String originalUid = entity.getUid();
                String originalHoldMediaUrl = entity.getHoldMediaUrl();
                String originalConsultExtensionNumbers = entity.getConsultExtensionNumbers();
                String originalTransferTargetNumbers = entity.getTransferTargetNumbers();
                String originalConferenceTargetNumbers = entity.getConferenceTargetNumbers();
                String originalIvrTargetNumbers = entity.getIvrTargetNumbers();
                Boolean originalSignedIn = entity.getSignedIn();
                modelMapper.map(request, entity);
                entity.setId(originalId);
                entity.setUid(originalUid);
                if (request.getSignedIn() == null) {
                    entity.setSignedIn(originalSignedIn);
                }
                if (request.getHoldMediaUrl() == null) {
                    entity.setHoldMediaUrl(originalHoldMediaUrl);
                }
                if (request.getConsultExtensionNumbers() == null) {
                    entity.setConsultExtensionNumbers(originalConsultExtensionNumbers);
                }
                if (request.getTransferTargetNumbers() == null) {
                    entity.setTransferTargetNumbers(originalTransferTargetNumbers);
                }
                if (request.getConferenceTargetNumbers() == null) {
                    entity.setConferenceTargetNumbers(originalConferenceTargetNumbers);
                }
                if (request.getIvrTargetNumbers() == null) {
                    entity.setIvrTargetNumbers(originalIvrTargetNumbers);
                }
                normalizeCallControlSettings(entity);
                applyAuditContext(entity, request);
                CallSettingsEntity savedEntity = save(entity);
                if (savedEntity == null) {
                    throw new RuntimeException("Update call settings failed");
                }
                return convertToResponse(savedEntity);
            }
        }

        return create(request);
    }

    @Override
    protected CallSettingsEntity doSave(CallSettingsEntity entity) {
        return callSettingsRepository.save(entity);
    }

    @Transactional
    public CallSettingsResponse updateSignedIn(CallSettingsRequest request) {
        if (request.getSignedIn() == null) {
            throw new RuntimeException("signedIn is required");
        }

        Optional<CallSettingsEntity> existing = Optional.empty();
        if (StringUtils.hasText(request.getUid())) {
            existing = findByUid(request.getUid());
        }
        if (existing.isEmpty() && StringUtils.hasText(request.getAgentUid())) {
            existing = findByAgentUid(request.getAgentUid());
        }

        CallSettingsEntity entity = existing.orElseThrow(() -> new RuntimeException("Call settings not found"));
        entity.setSignedIn(Boolean.TRUE.equals(request.getSignedIn()));
        if (!Boolean.TRUE.equals(entity.getSignedIn())) {
            entity.setRegistrationStatus(null);
        }
        applyAuditContext(entity, request);
        CallSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Update call settings signed in state failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public CallSettingsEntity handleOptimisticLockingFailureException(
        ObjectOptimisticLockingFailureException e,
        CallSettingsEntity entity
    ) {
        try {
            Optional<CallSettingsEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallSettingsEntity latestEntity = latest.get();
                latestEntity.setAgentUid(entity.getAgentUid());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setSignedIn(entity.getSignedIn());
                latestEntity.setMobileOutboundEnabled(entity.getMobileOutboundEnabled());
                latestEntity.setMobilePhoneNumber(normalizeNullable(entity.getMobilePhoneNumber()));
                latestEntity.setNumber(entity.getNumber());
                latestEntity.setDisplayName(entity.getDisplayName());
                latestEntity.setTarget(entity.getTarget());
                latestEntity.setRegistrationStatus(entity.getRegistrationStatus());
                latestEntity.setHoldMediaUrl(normalizeHoldMediaUrl(entity.getHoldMediaUrl()));
                latestEntity.setConsultExtensionNumbers(normalizeString(entity.getConsultExtensionNumbers(), DEFAULT_CONSULT_EXTENSION_NUMBERS));
                latestEntity.setTransferTargetNumbers(normalizeString(entity.getTransferTargetNumbers(), DEFAULT_TRANSFER_TARGET_NUMBERS));
                latestEntity.setConferenceTargetNumbers(normalizeString(entity.getConferenceTargetNumbers(), DEFAULT_CONFERENCE_TARGET_NUMBERS));
                latestEntity.setIvrTargetNumbers(normalizeString(entity.getIvrTargetNumbers(), DEFAULT_IVR_TARGET_NUMBERS));
                latestEntity.setOrgUid(entity.getOrgUid());
                latestEntity.setUserUid(entity.getUserUid());
                return callSettingsRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Unable to resolve optimistic locking for call settings", ex);
            throw new RuntimeException("Unable to resolve optimistic locking for call settings", ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void delete(CallSettingsRequest request) {
        if (StringUtils.hasText(request.getUid())) {
            deleteByUid(request.getUid());
            return;
        }

        if (!StringUtils.hasText(request.getAgentUid())) {
            throw new RuntimeException("uid or agent uid is required");
        }

        CallSettingsEntity entity = findByAgentUid(request.getAgentUid())
            .orElseThrow(() -> new RuntimeException("Call settings not found"));
        entity.setDeleted(true);
        save(entity);
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        CallSettingsEntity entity = findByUid(uid)
            .orElseThrow(() -> new RuntimeException("Call settings not found"));
        entity.setDeleted(true);
        save(entity);
    }

    @Override
    public CallSettingsResponse convertToResponse(CallSettingsEntity entity) {
        CallSettingsResponse response = modelMapper.map(entity, CallSettingsResponse.class);
        response.setHoldMediaUrl(normalizeHoldMediaUrl(response.getHoldMediaUrl()));
        response.setConsultExtensionNumbers(normalizeString(response.getConsultExtensionNumbers(), DEFAULT_CONSULT_EXTENSION_NUMBERS));
        response.setTransferTargetNumbers(normalizeString(response.getTransferTargetNumbers(), DEFAULT_TRANSFER_TARGET_NUMBERS));
        response.setConferenceTargetNumbers(normalizeString(response.getConferenceTargetNumbers(), DEFAULT_CONFERENCE_TARGET_NUMBERS));
        response.setIvrTargetNumbers(normalizeString(response.getIvrTargetNumbers(), DEFAULT_IVR_TARGET_NUMBERS));
        return response;
    }

    @Override
    public CallSettingsExcel convertToExcel(CallSettingsEntity entity) {
        return modelMapper.map(entity, CallSettingsExcel.class);
    }

    private void applyAuditContext(CallSettingsEntity entity, CallSettingsRequest request) {
        if (StringUtils.hasText(request.getAgentUid())) {
            entity.setAgentUid(request.getAgentUid().trim());
        }
        if (StringUtils.hasText(request.getOrgUid())) {
            entity.setOrgUid(request.getOrgUid());
        }

        UserEntity user = authService.getUser();
        if (user != null && StringUtils.hasText(user.getUid())) {
            entity.setUserUid(user.getUid());
        } else if (StringUtils.hasText(request.getUserUid())) {
            entity.setUserUid(request.getUserUid());
        }
    }

    private String normalizeHoldMediaUrl(String holdMediaUrl) {
        if (!StringUtils.hasText(holdMediaUrl)) {
            return DEFAULT_HOLD_MEDIA_URL;
        }

        String normalizedValue = holdMediaUrl.trim();
        if (LEGACY_DEFAULT_HOLD_MEDIA_URL.equalsIgnoreCase(normalizedValue)
                || LEGACY_LOCAL_STREAM_DEFAULT_HOLD_MEDIA_URL.equalsIgnoreCase(normalizedValue)
                || LEGACY_LOCAL_STREAM_HOLD_MEDIA_URL.equalsIgnoreCase(normalizedValue)
                || LEGACY_LOCAL_STREAM_HOLD_MEDIA_8000_URL.equalsIgnoreCase(normalizedValue)
                || LEGACY_TONE_STREAM_HOLD_MEDIA_URL.equalsIgnoreCase(normalizedValue)) {
            return DEFAULT_HOLD_MEDIA_URL;
        }

        return normalizedValue;
    }

    private void normalizeCallControlSettings(CallSettingsEntity entity) {
        entity.setSignedIn(Boolean.TRUE.equals(entity.getSignedIn()));
        entity.setMobileOutboundEnabled(Boolean.TRUE.equals(entity.getMobileOutboundEnabled()));
        entity.setMobilePhoneNumber(normalizeNullable(entity.getMobilePhoneNumber()));
        entity.setHoldMediaUrl(normalizeHoldMediaUrl(entity.getHoldMediaUrl()));
        entity.setConsultExtensionNumbers(normalizeString(entity.getConsultExtensionNumbers(), DEFAULT_CONSULT_EXTENSION_NUMBERS));
        entity.setTransferTargetNumbers(normalizeString(entity.getTransferTargetNumbers(), DEFAULT_TRANSFER_TARGET_NUMBERS));
        entity.setConferenceTargetNumbers(normalizeString(entity.getConferenceTargetNumbers(), DEFAULT_CONFERENCE_TARGET_NUMBERS));
        entity.setIvrTargetNumbers(normalizeString(entity.getIvrTargetNumbers(), DEFAULT_IVR_TARGET_NUMBERS));
    }

    private String normalizeString(String value, String defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return value.trim();
    }

    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private Set<String> buildExtensionCandidates(String extensionNo, String registrationUser) {
        Set<String> candidates = new LinkedHashSet<>();
        addExtensionCandidate(candidates, extensionNo);
        addExtensionCandidate(candidates, registrationUser);
        addExtensionCandidate(candidates, extractExtensionNo(registrationUser));
        return candidates;
    }

    private void addExtensionCandidate(Set<String> candidates, String value) {
        if (StringUtils.hasText(value)) {
            candidates.add(value.trim());
        }
    }

    private String extractExtensionNo(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.regionMatches(true, 0, "sip:", 0, 4)) {
            normalized = normalized.substring(4);
        }
        int atIndex = normalized.indexOf('@');
        if (atIndex > 0) {
            normalized = normalized.substring(0, atIndex);
        }
        return normalized;
    }
}
