package com.bytedesk.call.call_ip_blacklist;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CallIpBlacklistRepository extends JpaRepository<CallIpBlacklistEntity, Long>, JpaSpecificationExecutor<CallIpBlacklistEntity> {

    Optional<CallIpBlacklistEntity> findByUid(String uid);

    // 使用 List + 按 id 升序:同一 IP 可能被并发自动拉黑或被多个 org 分别拉黑而产生多行,
    // 单结果查询会抛 NonUniqueResultException,取最早一条即可
    List<CallIpBlacklistEntity> findAllByOrgUidAndIpAddressAndDeletedFalseOrderByIdAsc(String orgUid, String ipAddress);

    List<CallIpBlacklistEntity> findAllByIpAddressAndDeletedFalseOrderByIdAsc(String ipAddress);

    List<CallIpBlacklistEntity> findAllByOrgUidInAndDeletedFalse(Collection<String> orgUids);
}