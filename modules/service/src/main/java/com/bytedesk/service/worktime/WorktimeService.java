/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:46:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:21:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.DateUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WorktimeService extends BaseService<WorktimeEntity, WorktimeRequest, WorktimeResponse> {

    private final WorktimeRepository worktimeRepository;

    private final UidUtils uidUtils;

    // private final ModelMapper modelMapper;

    @Override
    public Page<WorktimeResponse> queryByOrg(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<WorktimeResponse> queryByUser(WorktimeRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<WorktimeEntity> findByUid(String uid) {
        return worktimeRepository.findByUid(uid);
    }

    @Override
    public WorktimeResponse create(WorktimeRequest request) {

        // Worktime worktime = modelMapper.map(request, Worktime.class);
        WorktimeEntity worktime = WorktimeEntity.builder()
        .startTime(DateUtils.formatStringToTime(request.getStartTime()))
        .endTime(DateUtils.formatStringToTime(request.getEndTime()))
        .build();
        worktime.setUid(uidUtils.getCacheSerialUid());

        return convertToResponse(save(worktime));
    }

    @Override
    public WorktimeResponse update(WorktimeRequest request) {

        Optional<WorktimeEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WorktimeEntity worktime = optional.get();
            // modelMapper.map(request, worktime);
            worktime.setStartTime(DateUtils.formatStringToTime(request.getStartTime()));
            worktime.setEndTime(DateUtils.formatStringToTime(request.getEndTime()));
            // 
            return convertToResponse(save(worktime));
        } else {
            throw new RuntimeException("Worktime not found");
        }
    }

    @Override
    public WorktimeEntity save(WorktimeEntity entity) {
        return worktimeRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WorktimeEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            WorktimeEntity worktime = optional.get();
            worktime.setDeleted(true);
            // worktimeRepository.delete(worktime);
            save(worktime);
        }
    }

    @Override
    public void delete(WorktimeRequest entity) {
        // worktimeRepository.delete(entity);
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorktimeEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public WorktimeResponse convertToResponse(WorktimeEntity entity) {
        // return modelMapper.map(entity, WorktimeResponse.class);
        WorktimeResponse worktimeResponse = WorktimeResponse.builder()
                // .uid(entity.getUid())
                .startTime(DateUtils.formatTimeToString(entity.getStartTime()))
                .endTime(DateUtils.formatTimeToString(entity.getEndTime()))
                .build();
        worktimeResponse.setUid(entity.getUid());
        return worktimeResponse;
    }

    public String createDefault() {

        WorktimeRequest worktimeRequest = WorktimeRequest.builder()
                .startTime("00:00:00")
                .endTime("23:59:59")
                .build();

        WorktimeResponse worktimeResponse = create(worktimeRequest);

        return worktimeResponse.getUid();
    }

}
