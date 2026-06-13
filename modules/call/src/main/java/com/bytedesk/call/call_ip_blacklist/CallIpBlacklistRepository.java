package com.bytedesk.call.call_ip_blacklist;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CallIpBlacklistRepository extends JpaRepository<CallIpBlacklistEntity, Long>, JpaSpecificationExecutor<CallIpBlacklistEntity> {

    Optional<CallIpBlacklistEntity> findByUid(String uid);

    Optional<CallIpBlacklistEntity> findByOrgUidAndIpAddressAndDeletedFalse(String orgUid, String ipAddress);

    Optional<CallIpBlacklistEntity> findByIpAddressAndDeletedFalse(String ipAddress);

    List<CallIpBlacklistEntity> findAllByOrgUidInAndDeletedFalse(Collection<String> orgUids);
}