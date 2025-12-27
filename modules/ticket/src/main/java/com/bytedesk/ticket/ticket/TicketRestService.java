/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:53:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.service.form.FormEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateDepartmentEvent;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsResponse;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsResponse;
import com.bytedesk.ticket.utils.TicketConvertUtils;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketRestService
        extends BaseRestServiceWithExport<TicketEntity, TicketRequest, TicketResponse, TicketExcel> {

    private final TicketRepository ticketRepository;

    private final TicketAttachmentRepository attachmentRepository;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    private final UploadRestService uploadRestService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CategoryRestService categoryRestService;

    private final TicketSettingsRestService ticketSettingsRestService;

    @Cacheable(value = "ticket", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TicketEntity> findByUid(String uid) {
        return ticketRepository.findByUid(uid);
    }

    // query by user
    public Page<TicketResponse> queryByUser(TicketRequest request) {
        if (!StringUtils.hasText(request.getUserUid())) {
            String reporterUid = resolveReporterUid(request);
            if (StringUtils.hasText(reporterUid)) {
                request.setUserUid(reporterUid);
            }
        }
        Pageable pageable = request.getPageable();
        Specification<TicketEntity> spec = createSpecification(request);
        Page<TicketEntity> page = executePageQuery(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Transactional
    @Override
    public TicketResponse create(TicketRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public TicketResponse createVisitor(TicketRequest request) {
        return createInternal(request, true);
    }

    private TicketResponse createInternal(TicketRequest request, boolean skipLoginEnforce) {
        boolean platformTicketCenterRequest = ensurePlatformTicketRequestDefaults(request);
        // 创建工单...
        TicketEntity ticket = modelMapper.map(request, TicketEntity.class);
        ticket.setType(resolveTicketType(request.getType()));
        ticket.setUid(uidUtils.getUid());
        // 
        if (platformTicketCenterRequest) {
            ticket.setLevel(LevelEnum.PLATFORM.name());
            ticket.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        }
        if (!StringUtils.hasText(ticket.getUserUid())) {
            String reporterUid = resolveReporterUid(request);
            if (StringUtils.hasText(reporterUid)) {
                ticket.setUserUid(reporterUid);
            }
        }
        // 工单处理人
        ticket.setAssignee(request.getAssigneeJson());
        // 工单创建人
        ticket.setReporter(request.getReporterJson());
        //
        if (StringUtils.hasText(request.getAssigneeJson())
                && StringUtils.hasText(request.getAssignee().getUid())) {
            ticket.setStatus(TicketStatusEnum.ASSIGNED.name());
        } else {
            ticket.setStatus(TicketStatusEnum.NEW.name());
        }
        ticket.setReporter(request.getReporterJson());

        applyTicketSettings(ticket, request);

        if (!skipLoginEnforce) {
            enforceRequireLoginRule(ticket, request);
        }
        ensureTicketNumber(ticket, request);
        // 先保存工单
        TicketEntity savedTicket = save(ticket);
        // 保存附件
        Set<TicketAttachmentEntity> attachments = new HashSet<>();
        if (request.getUploadUids() != null) {
            for (String uploadUid : request.getUploadUids()) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
                    attachment.setOrgUid(savedTicket.getOrgUid());
                    attachment.setTicket(savedTicket);
                    attachment.setUpload(uploadOptional.get());
                    attachmentRepository.save(attachment);
                    //
                    attachments.add(attachment);
                }
            }
        }
        savedTicket.setAttachments(attachments);

        // 未绑定客服会话的情况下，创建工单客服会话
        if (!StringUtils.hasText(ticket.getThreadUid())) {
            // 如果创建工单的时候没有绑定会话，则创建会话
            if (!skipLoginEnforce) {
                ThreadEntity thread = createTicketThread(ticket);
                if (thread != null) {
                    ticket.setTopic(thread.getTopic());
                    ticket.setThreadUid(thread.getUid());
                }
            } else {
                log.debug("Skip creating ticket thread for anonymous visitor ticket: {}", ticket.getUid());
            }
        }

        // 保存工单
        savedTicket = save(savedTicket);
        if (savedTicket == null) {
            throw new RuntimeException("create ticket failed");
        }

        return convertToResponse(savedTicket);
    }

    @Transactional
    @Override
    public TicketResponse update(TicketRequest request) {
        Optional<TicketEntity> ticketOptional = findByUid(request.getUid());
        if (ticketOptional.isEmpty()) {
            throw new NotFoundException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();

        // 更新基本信息
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(request.getStatus());
        if (StringUtils.hasText(request.getType())) {
            ticket.setType(resolveTicketType(request.getType()));
        }

        // 更新工作组和处理人信息
        ticket.setAssignee(request.getAssigneeJson());
        ticket.setDepartmentUid(request.getDepartmentUid());
        // ticket = updateAssigneeAndWorkgroup(ticket, request);

        // 处理附件更新
        if (request.getUploadUids() != null) {
            ticket = updateAttachments(ticket, request.getUploadUids());
        }

        // 保存更新后的工单
        ticket = ticketRepository.save(ticket);
        if (ticket == null) {
            throw new RuntimeException("update ticket failed");
        }

        // 发布事件，判断assignee是否被修改
        if (StringUtils.hasText(request.getAssignee().getUid()) && StringUtils.hasText(ticket.getAssigneeString())) {
            String oldAssigneeUid = ticket.getAssignee().getUid();
            if (oldAssigneeUid != null && !oldAssigneeUid.equals(request.getAssignee().getUid())) {
                TicketUpdateAssigneeEvent ticketUpdateAssigneeEvent = new TicketUpdateAssigneeEvent(ticket,
                        oldAssigneeUid, request.getAssignee().getUid());
                applicationEventPublisher.publishEvent(ticketUpdateAssigneeEvent);
            }
        }

        // 发布事件，判断workgroupUid是否被修改
        if (StringUtils.hasText(request.getDepartmentUid())) {
            String oldWorkgroupUid = ticket.getDepartmentUid();
            if (oldWorkgroupUid != null && !oldWorkgroupUid.equals(request.getDepartmentUid())) {
                TicketUpdateDepartmentEvent TicketUpdateDepartmentEvent = new TicketUpdateDepartmentEvent(ticket,
                        oldWorkgroupUid, request.getDepartmentUid());
                applicationEventPublisher.publishEvent(TicketUpdateDepartmentEvent);
            }
        }

        return convertToResponse(ticket);
    }

    public TicketEntity updateAttachments(TicketEntity ticket, Set<String> uploadUids) {
        // 获取现有附件的 uploadUid 列表
        Set<String> existingUploadUids = ticket.getAttachments().stream()
                .map(attachment -> attachment.getUpload().getUid())
                .collect(Collectors.toSet());

        // 处理新增的附件
        for (String uploadUid : uploadUids) {
            if (!existingUploadUids.contains(uploadUid)) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
                    attachment.setTicket(ticket);
                    attachment.setUpload(uploadOptional.get());
                    attachmentRepository.save(attachment);
                    // 添加到工单的附件集合中
                    ticket.getAttachments().add(attachment);
                }
            }
        }

        // 处理需要删除的附件
        ticket.getAttachments().stream()
                .filter(attachment -> !uploadUids.contains(attachment.getUpload().getUid()))
                .forEach(attachment -> {
                    attachment.setDeleted(true);
                    attachmentRepository.save(attachment);
                });

        return ticket;
    }

    // 创建工单会话
    public ThreadEntity createTicketThread(TicketEntity ticket) {
        //
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        //
        if (ticket.getDepartmentUid() == null || ticket.getDepartmentUid().isEmpty()) {
            ticket.setDepartmentUid("all");
        }
        //
        String topic = TopicUtils.formatOrgDepartmentTicketThreadTopic(ticket.getDepartmentUid(), ticket.getUid());
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        String user = ConvertUtils.convertToUserProtobufString(owner);
        // 创建工单会话
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ThreadTypeEnum.TICKET.name())
                .status(ThreadProcessStatusEnum.NEW.name())
                .topic(topic)
                .hide(true) // 默认隐藏
                .user(user)
                // .agent(user) // 客服会话的创建者是客服
                .userUid(owner.getUid())
                .owner(owner)
                .channel(ticket.getChannel())
                .orgUid(ticket.getOrgUid())
                .build();
        //
        return threadRestService.save(thread);
    }

    @Override
    protected TicketEntity doSave(TicketEntity entity) {
        return ticketRepository.save(entity);
    }

    @Override
    public TicketEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketEntity entity) {
        try {
            Optional<TicketEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setTitle(entity.getTitle());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setPriority(entity.getPriority());
                latestEntity.setStatus(entity.getStatus());
                // 其他需要合并的字段
                return ticketRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void delete(TicketRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(uid);
        if (!ticketOptional.isPresent()) {
            throw new NotFoundException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();
        ticket.setDeleted(true);
        save(ticket);
    }
    
    public void deleteByVisitor(TicketRequest request) {
        if (request == null || !StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("ticket uid required");
        }
        Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(request.getUid());
        if (ticketOptional.isEmpty()) {
            throw new NotFoundException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();
        String reporterUid = resolveReporterUid(request);
        if (!StringUtils.hasText(reporterUid)) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        String ticketReporterUid = ticket.getUserUid();
        if (!StringUtils.hasText(ticketReporterUid) && StringUtils.hasText(ticket.getReporterString())) {
            UserProtobuf reporter = ticket.getReporter();
            if (reporter != null && StringUtils.hasText(reporter.getUid())) {
                ticketReporterUid = reporter.getUid();
            }
        }
        if (!StringUtils.hasText(ticketReporterUid) || !ticketReporterUid.equals(reporterUid)) {
            throw new NotFoundException("ticket not found");
        }
        ticket.setDeleted(true);
        save(ticket);
    }

    @Override
    public TicketResponse convertToResponse(TicketEntity entity) {
        return TicketConvertUtils.convertToResponse(entity);
    }

    public void initTicketCategory(String orgUid) {
        log.info("initTicketCategory", orgUid);
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        List<CategoryTypeEnum> ticketCategoryTypes = List.of(CategoryTypeEnum.TICKET_INTERNAL, CategoryTypeEnum.TICKET_EXTERNAL);
        for (String category : TicketCategories.getAllCategories()) {
            for (CategoryTypeEnum categoryType : ticketCategoryTypes) {
                CategoryRequest categoryRequest = CategoryRequest.builder()
                        .uid(Utils.formatUid(orgUid, categoryType.name() + "_" + category))
                        .name(category)
                        .order(0)
                        .type(categoryType.name())
                        .level(LevelEnum.ORGANIZATION.name())
                        .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                        .orgUid(orgUid)
                        .build();
                categoryRestService.create(categoryRequest);
            }
        }
    }

    @Override
    public TicketExcel convertToExcel(TicketEntity entity) {
        return modelMapper.map(entity, TicketExcel.class);
    }

    @Override
    protected Specification<TicketEntity> createSpecification(TicketRequest request) {
        ensurePlatformTicketRequestDefaults(request);
        return TicketSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketEntity> executePageQuery(Specification<TicketEntity> specification, Pageable pageable) {
        return ticketRepository.findAll(specification, pageable);
    }

    private void ensureTicketNumber(TicketEntity ticket, TicketRequest request) {
        if (ticket == null || StringUtils.hasText(ticket.getTicketNumber())) {
            return;
        }
        String orgUid = resolveOrgUid(ticket, request);
        String workgroupUid = resolveWorkgroupUid(ticket, request);
        ticket.setTicketNumber(generateTicketNumber(orgUid, workgroupUid, ticket.getType()));
    }

    private String resolveTicketType(String requestedType) {
        return TicketTypeEnum.fromValue(requestedType).name();
    }

    private String generateTicketNumber(String orgUid, String workgroupUid, String ticketType) {
        String scopedOrgUid = StringUtils.hasText(orgUid) ? orgUid : BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        TicketBasicSettingsResponse basicSettings = fetchBasicSettings(scopedOrgUid, workgroupUid, ticketType);
        String prefix = resolvePrefix(basicSettings);
        int numericLength = resolveNumericLength(prefix, basicSettings);
        for (int i = 0; i < 5; i++) {
            String candidate = prefix + buildNumericPart(numericLength);
            if (!ticketNumberExists(scopedOrgUid, candidate)) {
                return candidate;
            }
        }
        return generateFallbackTicketNumber(scopedOrgUid, prefix);
    }

    private TicketBasicSettingsResponse fetchBasicSettings(String orgUid, String workgroupUid, String ticketType) {
        if (!StringUtils.hasText(orgUid)) {
            return null;
        }
        String normalizedType = resolveTicketType(ticketType);
        if (TicketTypeEnum.EXTERNAL.name().equals(normalizedType) && !StringUtils.hasText(workgroupUid)) {
            return null;
        }
        try {
            TicketSettingsResponse settings = ticketSettingsRestService
                    .getOrDefaultByWorkgroup(orgUid, workgroupUid, normalizedType);
            if (settings == null) {
                return null;
            }
            return settings.getBasicSettings() != null
                    ? settings.getBasicSettings()
                    : settings.getDraftBasicSettings();
        } catch (Exception ex) {
            log.warn("Failed to load ticket settings for org {} workgroup {}: {}", orgUid, workgroupUid, ex.getMessage());
            return null;
        }
    }

    private String resolvePrefix(TicketBasicSettingsResponse basicSettings) {
        if (basicSettings != null && StringUtils.hasText(basicSettings.getNumberPrefix())) {
            return basicSettings.getNumberPrefix().trim().toUpperCase();
        }
        return "TK";
    }

    private int resolveNumericLength(String prefix, TicketBasicSettingsResponse basicSettings) {
        int prefixLength = StringUtils.hasText(prefix) ? prefix.length() : 0;
        Integer configuredLength = basicSettings != null ? basicSettings.getNumberLength() : null;
        int totalLength = (configuredLength != null && configuredLength > prefixLength)
                ? configuredLength
                : prefixLength + 8;
        int numericLength = totalLength - prefixLength;
        numericLength = Math.max(numericLength, 4);
        return Math.min(numericLength, 32);
    }

    private String buildNumericPart(int length) {
        String raw = uidUtils.getUid();
        if (length <= 0) {
            return raw;
        }
        if (raw.length() > length) {
            return raw.substring(raw.length() - length);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = raw.length(); i < length; i++) {
            builder.append('0');
        }
        builder.append(raw);
        return builder.toString();
    }

    private String resolveOrgUid(TicketEntity ticket, TicketRequest request) {
        if (ticket != null && StringUtils.hasText(ticket.getOrgUid())) {
            return ticket.getOrgUid();
        }
        if (request != null && StringUtils.hasText(request.getOrgUid())) {
            return request.getOrgUid();
        }
        UserEntity user = authService.getUser();
        if (user != null && StringUtils.hasText(user.getOrgUid())) {
            return user.getOrgUid();
        }
        return BytedeskConsts.DEFAULT_ORGANIZATION_UID;
    }

    private String resolveWorkgroupUid(TicketEntity ticket, TicketRequest request) {
        if (ticket != null && StringUtils.hasText(ticket.getWorkgroupUid())) {
            return ticket.getWorkgroupUid();
        }
        if (request != null && StringUtils.hasText(request.getWorkgroupUid())) {
            return request.getWorkgroupUid();
        }
        if (ticket != null && StringUtils.hasText(ticket.getDepartmentUid())) {
            return ticket.getDepartmentUid();
        }
        return BytedeskConsts.DEFAULT_WORKGROUP_UID;
    }

    private boolean ticketNumberExists(String orgUid, String candidate) {
        return ticketRepository.existsByOrgUidAndTicketNumber(orgUid, candidate);
    }

    private String generateFallbackTicketNumber(String orgUid, String prefix) {
        for (int i = 0; i < 3; i++) {
            String candidate = prefix + uidUtils.getUid();
            if (!ticketNumberExists(orgUid, candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Unable to allocate unique ticket number for org " + orgUid);
    }

    private void applyTicketSettings(TicketEntity ticket, TicketRequest request) {
        if (ticket == null) {
            return;
        }
        String orgUid = resolveOrgUid(ticket, request);
        String workgroupUid = resolveWorkgroupUid(ticket, request);
        TicketSettingsEntity settings = resolveTicketSettingsEntity(request, orgUid, workgroupUid, ticket.getType());
        if (settings == null) {
            ensureProcessDefinitionFallback(ticket);
            return;
        }
        ticket.setTicketSettingsUid(settings.getUid());

        ProcessEntity process = settings.getProcess();
        if (process != null) {
            // processEntityUid 同时作为 Flowable 的 processDefinitionKey
            ticket.setProcessEntityUid(process.getUid());
        }

        FormEntity form = settings.getForm();
        if (form != null) {
            ticket.setFormEntityUid(form.getUid());
            if (!StringUtils.hasText(ticket.getSchema())) {
                ticket.setSchema(form.getSchema());
            }
        }

        ensureProcessDefinitionFallback(ticket);
    }

    private TicketSettingsEntity resolveTicketSettingsEntity(TicketRequest request, String orgUid,
            String workgroupUid, String ticketType) {
        if (!StringUtils.hasText(orgUid)) {
            return null;
        }
        String normalizedType = resolveTicketType(ticketType);
        if (request != null && StringUtils.hasText(request.getTicketSettingsUid())) {
            return ticketSettingsRestService.findByUid(request.getTicketSettingsUid())
                    .orElseThrow(() -> new NotFoundException(
                            "ticket settings not found: " + request.getTicketSettingsUid()));
        }
        return ticketSettingsRestService.resolveEntityByWorkgroup(orgUid, workgroupUid, normalizedType);
    }

    private void ensureProcessDefinitionFallback(TicketEntity ticket) {
        if (ticket != null && !StringUtils.hasText(ticket.getProcessEntityUid())) {
            // 根据工单类型计算默认的 processEntityUid
            String defaultProcessUid = TicketTypeEnum.EXTERNAL.name().equals(ticket.getType())
                    ? Utils.formatUid(ticket.getOrgUid(), TicketConsts.TICKET_PROCESS_KEY + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX)
                    : Utils.formatUid(ticket.getOrgUid(), TicketConsts.TICKET_PROCESS_KEY);
            ticket.setProcessEntityUid(defaultProcessUid);
        }
    }

    private void enforceRequireLoginRule(TicketEntity ticket, TicketRequest request) {
        String orgUid = resolveOrgUid(ticket, request);
        String workgroupUid = resolveWorkgroupUid(ticket, request);
        TicketBasicSettingsResponse basicSettings = fetchBasicSettings(orgUid, workgroupUid, ticket.getType());
        if (basicSettings == null || !Boolean.TRUE.equals(basicSettings.getRequireLogin())) {
            return;
        }
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
    }

    private String resolveReporterUid(TicketRequest request) {
        if (request == null) {
            return null;
        }
        if (StringUtils.hasText(request.getReporterUid())) {
            return request.getReporterUid();
        }
        UserProtobuf reporter = request.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            return reporter.getUid();
        }
        return null;
    }

    private boolean ensurePlatformTicketRequestDefaults(TicketRequest request) {
        boolean platformTicketCenterRequest = isPlatformTicketCenterRequest(request);
        if (!platformTicketCenterRequest) {
            return false;
        }
        UserEntity currentUser = requireLoginUser();
        request.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        request.setLevel(LevelEnum.PLATFORM.name());
        if (!StringUtils.hasText(request.getUserUid())) {
            request.setUserUid(currentUser.getUid());
        }
        if (!StringUtils.hasText(request.getReporterUid())) {
            request.setReporterUid(currentUser.getUid());
        }
        if (request.getReporter() == null) {
            request.setReporter(ConvertUtils.convertToUserProtobuf(currentUser));
        }
        return true;
    }

    private boolean isPlatformTicketCenterRequest(TicketRequest request) {
        if (request == null) {
            return false;
        }
        boolean defaultOrg = StringUtils.hasText(request.getOrgUid())
                && BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(request.getOrgUid());
        boolean platformLevel = StringUtils.hasText(request.getLevel())
                && LevelEnum.PLATFORM.name().equalsIgnoreCase(request.getLevel());
        return defaultOrg && platformLevel;
    }

    private UserEntity requireLoginUser() {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        return user;
    }

}