package com.bytedesk.call.call_ip_blacklist;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CallIpBlacklistRestService extends BaseRestServiceWithExport<CallIpBlacklistEntity, CallIpBlacklistRequest, CallIpBlacklistResponse, CallIpBlacklistExcel> {

    private final CallIpBlacklistRepository callIpBlacklistRepository;

    private final CallIpBlacklistService callIpBlacklistService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<CallIpBlacklistEntity> queryByOrgEntity(CallIpBlacklistRequest request) {
        Specification<CallIpBlacklistEntity> specs = CallIpBlacklistSpecification.search(request, authService);
        return callIpBlacklistRepository.findAll(specs, request.getPageable());
    }

    @Override
    public Page<CallIpBlacklistResponse> queryByOrg(CallIpBlacklistRequest request) {
        return queryByOrgEntity(request).map(this::convertToResponse);
    }

    @Override
    protected Specification<CallIpBlacklistEntity> createSpecification(CallIpBlacklistRequest request) {
        return CallIpBlacklistSpecification.search(request, authService);
    }

    @Override
    protected Page<CallIpBlacklistEntity> executePageQuery(Specification<CallIpBlacklistEntity> spec, Pageable pageable) {
        return callIpBlacklistRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<CallIpBlacklistEntity> findByUid(String uid) {
        return callIpBlacklistRepository.findByUid(uid);
    }

    @Transactional
    @Override
    public CallIpBlacklistResponse create(CallIpBlacklistRequest request) {
        String normalizedIp = requireNormalizedIp(request.getIpAddress());
        String orgUid = normalize(request.getOrgUid());

        Optional<CallIpBlacklistEntity> existing = findExisting(orgUid, normalizedIp);
        if (existing.isPresent()) {
            return convertToResponse(existing.get());
        }

        CallIpBlacklistEntity entity = CallIpBlacklistEntity.fromRequest(request, modelMapper);
        entity.setUid(uidUtils.getUid());
        entity.setIpAddress(normalizedIp);
        applyAuditContext(entity, request);

        CallIpBlacklistEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create call ip blacklist failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CallIpBlacklistResponse update(CallIpBlacklistRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            return create(request);
        }

        CallIpBlacklistEntity entity = findByUid(request.getUid())
            .orElseThrow(() -> new RuntimeException("Call ip blacklist not found"));

        String normalizedIp = requireNormalizedIp(request.getIpAddress());
        String orgUid = StringUtils.hasText(request.getOrgUid()) ? normalize(request.getOrgUid()) : normalize(entity.getOrgUid());

        Optional<CallIpBlacklistEntity> duplicate = findExisting(orgUid, normalizedIp);
        if (duplicate.isPresent() && !duplicate.get().getUid().equals(entity.getUid())) {
            throw new RuntimeException("IP blacklist entry already exists");
        }

        Long originalId = entity.getId();
        String originalUid = entity.getUid();
        modelMapper.map(request, entity);
        entity.setId(originalId);
        entity.setUid(originalUid);
        entity.setIpAddress(normalizedIp);
        applyAuditContext(entity, request);

        CallIpBlacklistEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Update call ip blacklist failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    protected CallIpBlacklistEntity doSave(CallIpBlacklistEntity entity) {
        return callIpBlacklistRepository.save(entity);
    }

    @Override
    public CallIpBlacklistEntity handleOptimisticLockingFailureException(
        ObjectOptimisticLockingFailureException e,
        CallIpBlacklistEntity entity
    ) {
        try {
            Optional<CallIpBlacklistEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                CallIpBlacklistEntity latestEntity = latest.get();
                latestEntity.setIpAddress(entity.getIpAddress());
                latestEntity.setSourceEslEventUid(entity.getSourceEslEventUid());
                latestEntity.setEventName(entity.getEventName());
                latestEntity.setCallerNumber(entity.getCallerNumber());
                latestEntity.setReason(entity.getReason());
                latestEntity.setOrgUid(entity.getOrgUid());
                latestEntity.setUserUid(entity.getUserUid());
                latestEntity.setLevel(entity.getLevel());
                return callIpBlacklistRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Unable to resolve optimistic locking for call ip blacklist", ex);
            throw new RuntimeException("Unable to resolve optimistic locking for call ip blacklist", ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void delete(CallIpBlacklistRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException("uid is required");
        }
        deleteByUid(request.getUid());
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        CallIpBlacklistEntity entity = findByUid(uid)
            .orElseThrow(() -> new RuntimeException("Call ip blacklist not found"));
        entity.setDeleted(true);
        save(entity);
    }

    @Override
    public CallIpBlacklistResponse convertToResponse(CallIpBlacklistEntity entity) {
        return modelMapper.map(entity, CallIpBlacklistResponse.class);
    }

    @Override
    public CallIpBlacklistExcel convertToExcel(CallIpBlacklistEntity entity) {
        return modelMapper.map(entity, CallIpBlacklistExcel.class);
    }

    private void applyAuditContext(CallIpBlacklistEntity entity, CallIpBlacklistRequest request) {
        if (StringUtils.hasText(request.getOrgUid())) {
            entity.setOrgUid(normalize(request.getOrgUid()));
        }

        if (StringUtils.hasText(request.getSourceEslEventUid())) {
            entity.setSourceEslEventUid(request.getSourceEslEventUid().trim());
        }

        if (StringUtils.hasText(request.getEventName())) {
            entity.setEventName(request.getEventName().trim());
        }

        if (StringUtils.hasText(request.getCallerNumber())) {
            entity.setCallerNumber(request.getCallerNumber().trim());
        }

        if (StringUtils.hasText(request.getReason())) {
            entity.setReason(request.getReason().trim());
        }

        UserEntity user = authService.getUser();
        if (user != null && StringUtils.hasText(user.getUid())) {
            entity.setUserUid(user.getUid());
        } else if (StringUtils.hasText(request.getUserUid())) {
            entity.setUserUid(request.getUserUid());
        }
    }

    private Optional<CallIpBlacklistEntity> findExisting(String orgUid, String normalizedIp) {
        if (!StringUtils.hasText(normalizedIp)) {
            return Optional.empty();
        }

        if (StringUtils.hasText(orgUid)) {
            return callIpBlacklistRepository.findByOrgUidAndIpAddressAndDeletedFalse(orgUid, normalizedIp);
        }

        return callIpBlacklistRepository.findByIpAddressAndDeletedFalse(normalizedIp);
    }

    private String requireNormalizedIp(String ipAddress) {
        String normalizedIp = callIpBlacklistService.normalizeIpAddress(ipAddress);
        if (!StringUtils.hasText(normalizedIp)) {
            throw new RuntimeException("ip address is required");
        }
        return normalizedIp;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}