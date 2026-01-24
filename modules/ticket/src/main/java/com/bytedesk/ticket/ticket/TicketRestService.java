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
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.service.form.FormEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateDepartmentEvent;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsEntity;
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

    private final MessageRepository messageRepository;

    private final UploadRestService uploadRestService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CategoryRestService categoryRestService;

    private final TicketSettingsRestService ticketSettingsRestService;

    
    // @Cacheable(value = "ticket", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TicketEntity> findByUid(String uid) {
        return ticketRepository.findByUid(uid);
    }

    @Override
    public Page<TicketResponse> queryByOrg(TicketRequest request) {
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

    @Override
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

    @Override
    public TicketResponse queryByUid(TicketRequest request) {
        Optional<TicketEntity> ticketOptional = findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new NotFoundException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();
        return convertToResponse(ticket);
    }

    /**
     * 按工单会话 uid 查询工单（threadUid 与 ticket 一对一）。
     */
    public TicketResponse queryByThreadUid(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getOrgUid(), "organization uid required");
        Assert.hasText(request.getThreadUid(), "thread uid required");

        Optional<TicketEntity> ticketOptional = ticketRepository
                .findFirstByOrgUidAndThreadUidOrderByCreatedAtDesc(request.getOrgUid(), request.getThreadUid());
        if (ticketOptional.isEmpty()) {
            throw new NotFoundException("ticket not found");
        }

        return convertToResponse(ticketOptional.get());
    }

    /**
     * 按工单会话 topic 查询工单（可能多条）。
     */
    public Page<TicketResponse> queryByThreadTopic(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getOrgUid(), "organization uid required");
        Assert.hasText(request.getThreadTopic(), "thread topic required");

        Pageable pageable = request.getPageable();
        Page<TicketEntity> page = ticketRepository.findByOrgUidAndThreadTopic(request.getOrgUid(),
                request.getThreadTopic(), pageable);
        return page.map(this::convertToResponse);
    }

    /**
     * 按 visitorThreadUid 查询工单（可能多条，因为多个工单可绑定同一个访客会话）。
     */
    public Page<TicketResponse> queryByVisitorThreadUid(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getOrgUid(), "organization uid required");
        Assert.hasText(request.getVisitorThreadUid(), "visitor thread uid required");

        Pageable pageable = request.getPageable();
        Page<TicketEntity> page = ticketRepository.findByOrgUidAndVisitorThreadUid(
                request.getOrgUid(), request.getVisitorThreadUid(), pageable);
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
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getOrgUid(), "organization uid required");
        Assert.hasText(request.getReporterJson(), "reporter info required");
        // 创建工单...
        TicketEntity ticket = modelMapper.map(request, TicketEntity.class);
        ticket.setType(TicketTypeEnum.getNameFromValue(request.getType()));
        ticket.setUid(uidUtils.getUid());
        // 工单处理人/办理人：仅在确实传入 uid 时写入
        boolean hasAssignee = false;
        try {
            hasAssignee = StringUtils.hasText(request.getAssigneeJson())
                    && request.getAssignee() != null
                    && StringUtils.hasText(request.getAssignee().getUid());
        } catch (Exception ignore) {
            hasAssignee = false;
        }
        ticket.setAssignee(hasAssignee ? request.getAssigneeJson() : BytedeskConsts.EMPTY_JSON_STRING);

        // 工单创建人
        ticket.setReporter(request.getReporterJson());

        // 状态：创建时根据是否指定 assignee 进行初始化
        ticket.setStatus(hasAssignee ? TicketStatusEnum.ASSIGNED.name() : TicketStatusEnum.NEW.name());
        if (!StringUtils.hasText(ticket.getUserUid())) {
            String reporterUid = resolveReporterUid(request);
            if (StringUtils.hasText(reporterUid)) {
                ticket.setUserUid(reporterUid);
            }
        }
        // 应用工单设置
        TicketSettingsEntity settings = applyTicketSettings(ticket, request);
        ensureTicketNumber(ticket, request, settings);
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

        // 创建工单会话
        ThreadEntity thread = createTicketThread(savedTicket);
        if (thread != null) {
            savedTicket.setThreadTopic(thread.getTopic());
            savedTicket.setThreadUid(thread.getUid());
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
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getUid(), "ticket uid required");
        Assert.hasText(request.getOrgUid(), "organization uid required");
        Assert.hasText(request.getReporterJson(), "reporter info required");

        Optional<TicketEntity> ticketOptional = findByUid(request.getUid());
        if (ticketOptional.isEmpty()) {
            throw new NotFoundException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();

        // 预先记录旧值，避免后续 setXXX 覆盖导致事件判断失效
        final String oldAssigneeUid = (ticket.getAssignee() != null) ? ticket.getAssignee().getUid() : null;
        final String oldDepartmentUid = ticket.getDepartmentUid();

        // 更新基本信息
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(request.getStatus());
        ticket.setEmail(request.getEmail());
        ticket.setPhone(request.getPhone());
        ticket.setWechat(request.getWechat());
        ticket.setContactName(request.getContactName());

        // 更新关联信息
        ticket.setAssignee(request.getAssigneeJson());
        ticket.setReporter(request.getReporterJson());
        ticket.setDepartmentUid(request.getDepartmentUid());
        ticket.setVisitorThreadUid(request.getVisitorThreadUid());

        // 处理附件更新
        if (request.getUploadUids() != null) {
            ticket = updateAttachments(ticket, request.getUploadUids());
        }

        // 关联工单会话，如果不存在则创建，兼容旧数据
        // ThreadEntity thread = createTicketThread(ticket);
        // if (thread != null) {
        // ticket.setThreadTopic(thread.getTopic());
        // ticket.setThreadUid(thread.getUid());
        // }

        // 保存更新后的工单
        ticket = ticketRepository.save(ticket);
        if (ticket == null) {
            throw new RuntimeException("update ticket failed");
        }

        // 发布事件，判断assignee是否被修改
        String newAssigneeUid = request.getAssigneeUid();
        if (!StringUtils.hasText(newAssigneeUid) && request.getAssignee() != null) {
            newAssigneeUid = request.getAssignee().getUid();
        }
        if (StringUtils.hasText(newAssigneeUid) && StringUtils.hasText(ticket.getAssigneeString())) {
            if (oldAssigneeUid != null && !oldAssigneeUid.equals(newAssigneeUid)) {
                TicketUpdateAssigneeEvent ticketUpdateAssigneeEvent = new TicketUpdateAssigneeEvent(ticket,
                        oldAssigneeUid, newAssigneeUid);
                applicationEventPublisher.publishEvent(ticketUpdateAssigneeEvent);
            }
        }

        // 发布事件，判断departmentUid是否被修改
        if (StringUtils.hasText(request.getDepartmentUid())) {
            if (oldDepartmentUid != null && !oldDepartmentUid.equals(request.getDepartmentUid())) {
                TicketUpdateDepartmentEvent TicketUpdateDepartmentEvent = new TicketUpdateDepartmentEvent(ticket,
                        oldDepartmentUid, request.getDepartmentUid());
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

    /**
     * 创建/获取工单会话。
     * <p>
     * 尝试绑定已有会话；否则走 topic 规则查找/创建。
     */
    public ThreadEntity createTicketThread(TicketEntity ticket) {
        TicketTypeEnum ticketType = TicketTypeEnum.fromValue(ticket.getType());

        // thread.user 赋值规则：
        // - 外部工单：使用提交工单人信息(ticket.reporter)
        // - 内部工单：同样使用 ticket.reporter（会话内的消息可通过 MessageExtra.visibility 区分可见范围）
        String reporterUserJson = ticket.getReporterString();
        if (!StringUtils.hasText(reporterUserJson)) {
            reporterUserJson = BytedeskConsts.EMPTY_JSON_STRING;
        }

        // 优先用前端传入的 threadUid 绑定已有会话
        if (StringUtils.hasText(ticket.getThreadUid())) {
            Optional<ThreadEntity> existingByUid = threadRestService.findByUid(ticket.getThreadUid());
            if (existingByUid.isPresent()) {
                return existingByUid.get();
            }
        }

        // internal(部门) -> external(工作组) -> org兜底
        final String ticketUid = ticket.getUid();
        String topic;
        if (StringUtils.hasText(ticket.getDepartmentUid())) {
            topic = TopicUtils.formatOrgDepartmentTicketThreadTopic(ticket.getDepartmentUid(), ticketUid);
        } else if (StringUtils.hasText(ticket.getWorkgroupUid())) {
            topic = TopicUtils.formatOrgWorkgroupTicketThreadTopic(ticket.getWorkgroupUid(), ticketUid);
        } else if (StringUtils.hasText(ticket.getOrgUid())) {
            topic = TopicUtils.formatOrgTicketThreadTopic(ticket.getOrgUid(), ticketUid);
        } else {
            throw new IllegalArgumentException("ticket thread topic requires departmentUid/workgroupUid/orgUid");
        }

        // 创建工单会话
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ticketType == TicketTypeEnum.INTERNAL
                        ? ThreadTypeEnum.TICKET_INTERNAL.name()
                        : ThreadTypeEnum.TICKET_EXTERNAL.name())
                .status(ThreadProcessStatusEnum.NEW.name())
                .topic(topic)
                // .hide(TicketTypeEnum.INTERNAL == ticketType) // 暂时不隐藏
                .user(reporterUserJson)
                // .userUid(owner.getUid())
                // .owner(owner)
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
        deleteByUid(request.getUid());
    }

    @Override
    public TicketResponse convertToResponse(TicketEntity entity) {
        TicketResponse response = TicketConvertUtils.convertToResponse(entity);
        if (StringUtils.hasText(entity.getThreadUid())) {
            long visitorUnreadCount = messageRepository.countVisitorUnreadByThreadUid(entity.getThreadUid());
            response.setVisitorUnreadCount(Math.toIntExact(visitorUnreadCount));
        } else {
            response.setVisitorUnreadCount(0);
        }
        return response;
    }

    public void initTicketCategory(String orgUid) {
        log.info("initTicketCategory", orgUid);
        List<CategoryTypeEnum> ticketCategoryTypes = List.of(CategoryTypeEnum.TICKET_INTERNAL,
                CategoryTypeEnum.TICKET_EXTERNAL);
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
        return TicketSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketEntity> executePageQuery(Specification<TicketEntity> specification, Pageable pageable) {
        return ticketRepository.findAll(specification, pageable);
    }

    public TicketStatusCountResponse countStatus(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        Assert.hasText(request.getOrgUid(), "organization uid required");

        String orgUid = request.getOrgUid();
        String workgroupUid = request.getWorkgroupUid();
        String departmentUid = request.getDepartmentUid();

        String processingStatus = TicketStatusEnum.PROCESSING.name();
        String pendingStatus = TicketStatusEnum.PENDING.name();
        String reviewStatus = TicketStatusEnum.RESOLVED.name();

        long processing;
        long pending;
        long review;

        if (StringUtils.hasText(workgroupUid)) {
            processing = ticketRepository.countByOrgUidAndWorkgroupUidAndStatusAndDeletedFalse(orgUid, workgroupUid,
                    processingStatus);
            pending = ticketRepository.countByOrgUidAndWorkgroupUidAndStatusAndDeletedFalse(orgUid, workgroupUid,
                    pendingStatus);
            review = ticketRepository.countByOrgUidAndWorkgroupUidAndStatusAndDeletedFalse(orgUid, workgroupUid,
                    reviewStatus);
        } else if (StringUtils.hasText(departmentUid)) {
            processing = ticketRepository.countByOrgUidAndDepartmentUidAndStatusAndDeletedFalse(orgUid, departmentUid,
                    processingStatus);
            pending = ticketRepository.countByOrgUidAndDepartmentUidAndStatusAndDeletedFalse(orgUid, departmentUid,
                    pendingStatus);
            review = ticketRepository.countByOrgUidAndDepartmentUidAndStatusAndDeletedFalse(orgUid, departmentUid,
                    reviewStatus);
        } else {
            processing = ticketRepository.countByOrgUidAndStatusAndDeletedFalse(orgUid, processingStatus);
            pending = ticketRepository.countByOrgUidAndStatusAndDeletedFalse(orgUid, pendingStatus);
            review = ticketRepository.countByOrgUidAndStatusAndDeletedFalse(orgUid, reviewStatus);
        }

        return TicketStatusCountResponse.builder()
                .processing(processing)
                .pending(pending)
                .review(review)
                .build();
    }

    /**
     * 确保工单已分配唯一的 ticketNumber。
     * <p>
     * 该方法会直接对入参 {@code ticket} 进行赋值：当生成到可用号码后，调用
     * {@code ticket.setTicketNumber(...)} 并返回。
     * <p>
     * 生成规则：
     * <ul>
     * <li>组织范围：优先使用 {@code request.orgUid}，为空则回退到
     * {@code BytedeskConsts.DEFAULT_ORGANIZATION_UID}</li>
     * <li>前缀：优先使用 {@code settings.basicSettings.numberPrefix}（trim + upper），否则默认
     * {@code TK}</li>
     * <li>长度：优先使用 {@code settings.basicSettings.numberLength}（需大于前缀长度），否则默认前缀长度 +
     * 8；
     * 数字部分长度最终限制在 [4, 32]</li>
     * <li>唯一性：最多尝试 5 次（prefix + 数字段），若仍冲突则使用 fallback（prefix + 完整 uid）再尝试 3 次</li>
     * </ul>
     *
     * @param ticket   需要写入 ticketNumber 的工单实体
     * @param request  工单请求（用于 orgUid 等上下文）
     * @param settings 工单设置（用于号码前缀/长度配置）
     * @throws IllegalStateException 当多次尝试后仍无法分配到唯一号码时抛出
     */
    private void ensureTicketNumber(TicketEntity ticket, TicketRequest request, TicketSettingsEntity settings) {
        Assert.notNull(ticket, "ticket required");
        Assert.notNull(request, "ticket request required");
        Assert.notNull(settings, "ticket settings required");

        String orgUid = request.getOrgUid();
        String scopedOrgUid = StringUtils.hasText(orgUid) ? orgUid : BytedeskConsts.DEFAULT_ORGANIZATION_UID;

        TicketBasicSettingsEntity basicSettings = settings.getBasicSettings();

        String prefix = "TK";
        if (basicSettings != null && StringUtils.hasText(basicSettings.getNumberPrefix())) {
            prefix = basicSettings.getNumberPrefix().trim().toUpperCase();
        }

        int prefixLength = StringUtils.hasText(prefix) ? prefix.length() : 0;
        Integer configuredLength = basicSettings != null ? basicSettings.getNumberLength() : null;
        int totalLength = (configuredLength != null && configuredLength > prefixLength)
                ? configuredLength
                : prefixLength + 8;
        int numericLength = totalLength - prefixLength;
        numericLength = Math.max(numericLength, 4);
        numericLength = Math.min(numericLength, 32);

        for (int i = 0; i < 5; i++) {
            String raw = uidUtils.getUid();
            String numericPart;
            if (numericLength <= 0) {
                numericPart = raw;
            } else if (raw.length() > numericLength) {
                numericPart = raw.substring(raw.length() - numericLength);
            } else {
                StringBuilder builder = new StringBuilder();
                for (int j = raw.length(); j < numericLength; j++) {
                    builder.append('0');
                }
                builder.append(raw);
                numericPart = builder.toString();
            }

            String candidate = prefix + numericPart;
            if (!ticketRepository.existsByOrgUidAndTicketNumber(scopedOrgUid, candidate)) {
                ticket.setTicketNumber(candidate);
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            String candidate = prefix + uidUtils.getUid();
            if (!ticketRepository.existsByOrgUidAndTicketNumber(scopedOrgUid, candidate)) {
                ticket.setTicketNumber(candidate);
                return;
            }
        }

        throw new IllegalStateException("Unable to allocate unique ticket number for org " + scopedOrgUid);
    }

    private TicketSettingsEntity applyTicketSettings(TicketEntity ticket, TicketRequest request) {
        Assert.notNull(ticket, "ticket required");
        Assert.notNull(request, "ticket request required");

        TicketSettingsEntity settings = ticketSettingsRestService.findByUid(request.getTicketSettingsUid())
                .orElseThrow(() -> new NotFoundException(
                        "ticket settings not found: " + request.getTicketSettingsUid()));
        ticket.setTicketSettingsUid(request.getTicketSettingsUid());

        // 优先使用前端明确传入的流程 UID（便于草稿流程/自定义流程创建工单）
        if (StringUtils.hasText(request.getProcessEntityUid())) {
            ticket.setProcessEntityUid(request.getProcessEntityUid());
        } else {
            ProcessEntity process = settings.getProcess();
            if (process != null) {
                // processEntityUid 同时作为 Flowable 的 processDefinitionKey
                ticket.setProcessEntityUid(process.getUid());
            }
        }

        FormEntity form = settings.getForm();
        if (form != null) {
            ticket.setFormEntityUid(form.getUid());
        }

        return settings;
    }

    private String resolveReporterUid(TicketRequest request) {
        Assert.notNull(request, "ticket request required");
        if (StringUtils.hasText(request.getReporterUid())) {
            return request.getReporterUid();
        }
        UserProtobuf reporter = request.getReporter();
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            return reporter.getUid();
        }
        return null;
    }

}