/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 18:05:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
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
public class TicketSettingsRestService extends BaseRestServiceWithExport<TicketSettingsEntity, TicketSettingsRequest, TicketSettingsResponse, TicketSettingsExcel> {

    private final TicketSettingsRepository ticketSettingsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<TicketSettingsEntity> createSpecification(TicketSettingsRequest request) {
        return TicketSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketSettingsEntity> executePageQuery(Specification<TicketSettingsEntity> spec, Pageable pageable) {
        return ticketSettingsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "ticketSettings", key = "#uid", unless="#result==null")
    @Override
    public Optional<TicketSettingsEntity> findByUid(String uid) {
        return ticketSettingsRepository.findByUid(uid);
    }

    @Cacheable(value = "ticketSettings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TicketSettingsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return ticketSettingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return ticketSettingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TicketSettingsResponse create(TicketSettingsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TicketSettingsEntity> ticketSettings = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (ticketSettings.isPresent()) {
                return convertToResponse(ticketSettings.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TicketSettingsEntity entity = modelMapper.map(request, TicketSettingsEntity.class);
        entity.setLastModifiedUserUid(request.getUserUid());
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TicketSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ticketSettings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TicketSettingsResponse update(TicketSettingsRequest request) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TicketSettingsEntity entity = optional.get();
            modelMapper.map(request, entity);
            entity.setLastModifiedUserUid(request.getUserUid());
            //
            TicketSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ticketSettings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    /**
     * 根据 org + workgroup 获取设置，不存在时返回默认结构（不落库），供前端初始化。
     */
    public TicketSettingsResponse getOrDefaultByWorkgroup(String orgUid, String workgroupUid) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
        if (optional.isPresent()) {
            return convertToResponse(optional.get()).setInitialized(true);
        }
        // 构造默认 JSON 模板
        String defaultJson = "{" +
            "\"basic\":{\"numberPrefix\":\"TK\",\"numberLength\":8,\"defaultPriority\":\"medium\",\"validityDays\":30,\"autoCloseHours\":72,\"enableAutoClose\":true}," +
            "\"statusFlow\":{\"statuses\":[" +
                "{\\\"key\\\":\\\"new\\\",\\\"name\\\":\\\"新建\\\",\\\"color\\\":\\\"#1890ff\\\",\\\"description\\\":\\\"新创建的工单\\\",\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"assigned\\\",\\\"name\\\":\\\"已分配\\\",\\\"color\\\":\\\"#52c41a\\\",\\\"description\\\":\\\"已分配给处理人员\\\",\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"processing\\\",\\\"name\\\":\\\"处理中\\\",\\\"color\\\":\\\"#faad14\\\",\\\"description\\\":\\\"正在处理中\\\",\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"pending\\\",\\\"name\\\":\\\"待客户\\\",\\\"color\\\":\\\"#fa8c16\\\",\\\"description\\\":\\\"等待客户反馈\\\",\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"resolved\\\",\\\"name\\\":\\\"已解决\\\",\\\"color\\\":\\\"#52c41a\\\",\\\"description\\\":\\\"问题已解决\\\",\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"closed\\\",\\\"name\\\":\\\"已关闭\\\",\\\"color\\\":\\\"#8c8c8c\\\",\\\"description\\\":\\\"工单已关闭\\\",\\\"isActive\\\":true}]," +
            "\"transitions\":[" +
                "{\\\"from\\\":\\\"new\\\",\\\"to\\\":\\\"assigned\\\",\\\"roles\\\":[\\\"admin\\\",\\\"agent\\\"]}," +
                "{\\\"from\\\":\\\"assigned\\\",\\\"to\\\":\\\"processing\\\",\\\"roles\\\":[\\\"admin\\\",\\\"agent\\\"]}," +
                "{\\\"from\\\":\\\"processing\\\",\\\"to\\\":\\\"pending\\\",\\\"roles\\\":[\\\"admin\\\",\\\"agent\\\"]}," +
                "{\\\"from\\\":\\\"processing\\\",\\\"to\\\":\\\"resolved\\\",\\\"roles\\\":[\\\"admin\\\",\\\"agent\\\"]}," +
                "{\\\"from\\\":\\\"pending\\\",\\\"to\\\"processing\\\",\\\"roles\\\":[\\\"admin\\\",\\\"agent\\\"]}," +
                "{\\\"from\\\":\\\"resolved\\\",\\\"to\\\":\\\"closed\\\",\\\"roles\\\":[\\\"admin\\\",\\\"agent\\\",\\\"customer\\\"]}]," +
            "}," +
            "\"priorities\":[" +
                "{\\\"key\\\":\\\"low\\\",\\\"name\\\":\\\"低\\\",\\\"color\\\":\\\"#52c41a\\\",\\\"slaHours\\\":72,\\\"order\\\":1,\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"medium\\\",\\\"name\\\":\\\"中\\\",\\\"color\\\":\\\"#faad14\\\",\\\"slaHours\\\":48,\\\"order\\\":2,\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"high\\\",\\\"name\\\":\\\"高\\\",\\\"color\\\":\\\"#fa8c16\\\",\\\"slaHours\\\":24,\\\"order\\\":3,\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"urgent\\\",\\\"name\\\":\\\"紧急\\\",\\\"color\\\":\\\"#f5222d\\\",\\\"slaHours\\\":8,\\\"order\\\":4,\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"critical\\\",\\\"name\\\":\\\"严重\\\",\\\"color\\\":\\\"#722ed1\\\",\\\"slaHours\\\":4,\\\"order\\\":5,\\\"isActive\\\":true}]," +
            "\"assignment\":{\"autoAssign\":true,\"assignmentType\":\"round_robin\",\"workingHours\":{\"enabled\":true,\"startTime\":\"09:00\",\"endTime\":\"18:00\",\"workingDays\":[1,2,3,4,5]},\"maxConcurrentTickets\":10}," +
            "\"notifications\":{\"email\":{\"enabled\":true,\"events\":[\"created\",\"assigned\",\"resolved\",\"closed\"],\"templates\":{}},\"internal\":{\"enabled\":true,\"events\":[\"created\",\"assigned\",\"resolved\",\"closed\"]},\"webhook\":{\"enabled\":false,\"url\":\"\",\"events\":[]}}," +
            "\"customFields\":[" +
                "{\\\"key\\\":\\\"department\\\",\\\"name\\\":\\\"部门\\\",\\\"type\\\":\\\"select\\\",\\\"required\\\":true,\\\"options\\\":[\\\"技术部\\\",\\\"销售部\\\",\\\"客服部\\\",\\\"市场部\\\"],\\\"order\\\":1,\\\"isActive\\\":true}," +
                "{\\\"key\\\":\\\"contact_phone\\\",\\\"name\\\":\\\"联系电话\\\",\\\"type\\\":\\\"text\\\",\\\"required\\\":false,\\\"order\\\":2,\\\"isActive\\\":true}]" +
            "}";
        return TicketSettingsResponse.builder()
            .orgUid(orgUid)
            .workgroupUid(workgroupUid)
            .settingsJson(defaultJson)
            .initialized(false)
            .build();
    }

    /**
     * 保存/更新指定工作组的设置（按 orgUid+workgroupUid 幂等）。
     */
    @Transactional
    public TicketSettingsResponse saveByWorkgroup(TicketSettingsRequest request) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(request.getOrgUid(), request.getWorkgroupUid());
        if (optional.isPresent()) {
            TicketSettingsEntity entity = optional.get();
            entity.setSettingsJson(request.getSettingsJson());
            entity.setLastModifiedUserUid(request.getUserUid());
            entity.setInitialized(Boolean.TRUE);
            return convertToResponse(save(entity));
        } else {
            TicketSettingsEntity entity = modelMapper.map(request, TicketSettingsEntity.class);
            if (!StringUtils.hasText(entity.getUid())) {
                entity.setUid(uidUtils.getUid());
            }
            entity.setInitialized(Boolean.TRUE);
            entity.setLastModifiedUserUid(request.getUserUid());
            return convertToResponse(save(entity));
        }
    }

    @Override
    protected TicketSettingsEntity doSave(TicketSettingsEntity entity) {
        return ticketSettingsRepository.save(entity);
    }

    @Override
    public TicketSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TicketSettingsEntity entity) {
        try {
            Optional<TicketSettingsEntity> latest = ticketSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return ticketSettingsRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ticketSettingsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    @Override
    public void delete(TicketSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TicketSettingsResponse convertToResponse(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsResponse.class);
    }

    @Override
    public TicketSettingsExcel convertToExcel(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsExcel.class);
    }
    
    public void initTicketSettings(String orgUid) {
        // log.info("initThreadTicketSettings");
        // for (String ticketSettings : TicketSettingsInitData.getAllTicketSettingss()) {
        //     TicketSettingsRequest ticketSettingsRequest = TicketSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, ticketSettings))
        //             .name(ticketSettings)
        //             .order(0)
        //             .type(TicketSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(ticketSettingsRequest);
        // }
    }

    
    
}
