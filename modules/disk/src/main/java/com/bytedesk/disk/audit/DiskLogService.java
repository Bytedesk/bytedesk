package com.bytedesk.disk.audit;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 网盘关键操作留痕服务：一期仅记录授权变更与彻底删除，不对外提供查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiskLogService {

    private final DiskLogRepository diskLogRepository;

    private final AuthService authService;

    private final UidUtils uidUtils;

    @Transactional
    public void record(String orgUid, String spaceUid, String nodeUid, String action, String result, String detail) {
        try {
            UserEntity user = authService.getCurrentUser();
            DiskLogEntity entity = DiskLogEntity.builder().build();
            entity.setUid(uidUtils.getUid());
            entity.setOrgUid(orgUid);
            entity.setUserUid(user != null ? user.getUid() : null);
            entity.setSpaceUid(spaceUid);
            entity.setNodeUid(nodeUid);
            entity.setOperatorUid(user != null ? user.getUid() : null);
            entity.setAction(action);
            entity.setResult(result);
            entity.setDetail(detail != null ? detail : BytedeskConsts.EMPTY_JSON_STRING);
            diskLogRepository.save(entity);
        } catch (Exception e) {
            // 留痕失败不阻断业务，但记录错误日志
            log.error("disk audit log failed: action={}, nodeUid={}, error={}", action, nodeUid, e.getMessage());
        }
    }

    public void logPermissionChange(String orgUid, String spaceUid, String folderUid, String action) {
        record(orgUid, spaceUid, folderUid, action, "SUCCESS", null);
    }

    public void logPermanentDelete(String orgUid, String spaceUid, String nodeUid, String detail) {
        record(orgUid, spaceUid, nodeUid, "DELETE_PERMANENT", "SUCCESS", detail);
    }
}
