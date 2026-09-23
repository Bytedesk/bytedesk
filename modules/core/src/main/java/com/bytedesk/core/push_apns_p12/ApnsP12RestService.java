/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_apns_p12;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ApnsP12RestService extends BaseRestServiceWithExport<ApnsP12Entity, ApnsP12Request, ApnsP12Response, ApnsP12Excel> {

    private final ApnsP12Repository apnsP12Repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final OrganizationRepository organizationRepository;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ApnsP12Entity> queryByOrgEntity(ApnsP12Request request) {
        Pageable pageable = request.getPageable();
        Specification<ApnsP12Entity> specs = ApnsP12Specification.search(request, authService);
        return apnsP12Repository.findAll(specs, pageable);
    }

    @Override
    public Page<ApnsP12Response> queryByOrg(ApnsP12Request request) {
        Page<ApnsP12Entity> apns_p12Page = queryByOrgEntity(request);
        return apns_p12Page.map(this::convertToResponse);
    }

    @Override
    public Page<ApnsP12Response> queryByUser(ApnsP12Request request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "apns_p12", key = "#uid", unless="#result==null")
    @Override
    public Optional<ApnsP12Entity> findByUid(String uid) {
        return apnsP12Repository.findByUid(uid);
    }

    @Cacheable(value = "apns_p12", key = "#bundleId + '_' + #orgUid + '_' + #sandbox", unless="#result==null")
    public Optional<ApnsP12Entity> findByBundleIdAndOrgUidAndSandbox(String bundleId, String orgUid, Boolean sandbox) {
        return apnsP12Repository.findByBundleIdAndOrgUidAndSandboxAndDeletedFalse(bundleId, orgUid, sandbox);
    }

    public Boolean existsByUid(String uid) {
        return apnsP12Repository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ApnsP12Response create(ApnsP12Request request) {
        return createInternal(request, false);
    }

    @Transactional
    public ApnsP12Response createSystemApnsP12(ApnsP12Request request) {
        return createInternal(request, true);
    }

    private ApnsP12Response createInternal(ApnsP12Request request, boolean skipPermissionCheck) {
        // 判断是否已经存在：系统初始化（skipPermissionCheck=true）幂等静默返回已有记录；
        // 用户创建时抛出明确提示，避免前端用户误解以为创建失败
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            if (skipPermissionCheck) {
                return convertToResponse(findByUid(request.getUid()).get());
            }
            throw new RuntimeException(I18Consts.I18N_DATA_ALREADY_EXISTS);
        }
        // 检查 bundleId + orgUid + sandbox 是否已经存在
        if (StringUtils.hasText(request.getBundleId()) && StringUtils.hasText(request.getOrgUid())) {
            Boolean sandbox = request.getSandbox() != null ? request.getSandbox() : Boolean.FALSE;
            Optional<ApnsP12Entity> apns_p12 = findByBundleIdAndOrgUidAndSandbox(request.getBundleId(), request.getOrgUid(), sandbox);
            if (apns_p12.isPresent()) {
                if (skipPermissionCheck) {
                    return convertToResponse(apns_p12.get());
                }
                // 同一组织下相同 Bundle ID + 环境的证书已存在，明确提示而不是静默返回旧记录
                throw new RuntimeException(I18Consts.withArgs(I18Consts.I18N_APNS_P12_EXISTS, request.getBundleId()));
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ApnsP12Permissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        ApnsP12Entity entity = modelMapper.map(request, ApnsP12Entity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 解析 p12 证书内容，自动刷新到期时间
        refreshExpiresAt(entity);
        // 
        ApnsP12Entity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ApnsP12Response update(ApnsP12Request request) {
        Optional<ApnsP12Entity> optional = apnsP12Repository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ApnsP12Entity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ApnsP12Permissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            // 解析 p12 证书内容，自动刷新到期时间
            refreshExpiresAt(entity);
            //
            ApnsP12Entity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected ApnsP12Entity doSave(ApnsP12Entity entity) {
        return apnsP12Repository.save(entity);
    }

    /**
     * 解析 p12 证书内容，自动刷新证书到期时间。
     * p12 为 PKCS#12 格式，使用证书密码加载后读取首个 X509 证书的 notAfter 即为到期时间；
     * 解析失败（密码错误、文件不可达等）时置空并记录警告，不阻断保存流程。
     */
    private void refreshExpiresAt(ApnsP12Entity entity) {
        if (!StringUtils.hasText(entity.getP12Url())) {
            entity.setExpiresAt(null);
            return;
        }
        try (InputStream inputStream = openP12InputStream(entity.getP12Url())) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] password = entity.getP12Password() == null ? new char[0]
                    : entity.getP12Password().toCharArray();
            keyStore.load(inputStream, password);
            var aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = keyStore.getCertificate(alias);
                if (certificate instanceof X509Certificate x509Cert) {
                    entity.setExpiresAt(x509Cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()));
                    log.info("Parsed APNS p12 certificate expiry, uid={}, bundleId={}, expiresAt={}",
                            entity.getUid(), entity.getBundleId(), x509Cert.getNotAfter());
                    return;
                }
            }
            log.warn("No X509 certificate found in p12, uid={}, p12Url={}", entity.getUid(), entity.getP12Url());
            entity.setExpiresAt(null);
        } catch (Exception e) {
            log.warn("Failed to parse APNS p12 certificate expiry, uid={}, p12Url={}, error={}",
                    entity.getUid(), entity.getP12Url(), e.getMessage());
            entity.setExpiresAt(null);
        }
    }

    private InputStream openP12InputStream(String p12Url) throws Exception {
        // 与 ApnsPushService.openP12InputStream 保持一致：支持 http/https/file URL 与本地路径
        if (p12Url.startsWith("http://") || p12Url.startsWith("https://") || p12Url.startsWith("file:")) {
            return URI.create(p12Url).toURL().openStream();
        }
        return Files.newInputStream(Path.of(p12Url));
    }

    @Override
    public ApnsP12Entity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ApnsP12Entity entity) {
        try {
            Optional<ApnsP12Entity> latest = apnsP12Repository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ApnsP12Entity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setBundleId(entity.getBundleId());
                latestEntity.setP12Url(entity.getP12Url());
                latestEntity.setP12Password(entity.getP12Password());
                latestEntity.setSandbox(entity.getSandbox());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setExpiresAt(entity.getExpiresAt());
                return apnsP12Repository.save(latestEntity);
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
        Optional<ApnsP12Entity> optional = apnsP12Repository.findByUid(uid);
        if (optional.isPresent()) {
            ApnsP12Entity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ApnsP12Permissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
            // apns_p12Repository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(ApnsP12Request request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ApnsP12Response convertToResponse(ApnsP12Entity entity) {
        ApnsP12Response response = modelMapper.map(entity, ApnsP12Response.class);

        if (StringUtils.hasText(entity.getOrgUid())) {
            organizationRepository.findByUid(entity.getOrgUid()).ifPresent(org -> response.setOrgName(org.getName()));
        }

        return response;
    }

    @Override
    public ApnsP12Excel convertToExcel(ApnsP12Entity entity) {
        return modelMapper.map(entity, ApnsP12Excel.class);
    }

    @Override
    protected Specification<ApnsP12Entity> createSpecification(ApnsP12Request request) {
        return ApnsP12Specification.search(request, authService);
    }

    @Override
    protected Page<ApnsP12Entity> executePageQuery(Specification<ApnsP12Entity> spec, Pageable pageable) {
        return apnsP12Repository.findAll(spec, pageable);
    }
    
    public void initApnsP12s(String orgUid) {
        // log.info("initApnsP12ApnsP12");
        // for (String apns_p12 : ApnsP12InitData.getAllApnsP12s()) {
        //     ApnsP12Request apns_p12Request = ApnsP12Request.builder()
        //             .uid(Utils.formatUid(orgUid, apns_p12))
        //             .name(apns_p12)
        //             .bundleId("com.example.app")
        //             .p12Url("https://example.com/certificates/app.p12")
        //             .sandbox(Boolean.FALSE)
        //             .enabled(Boolean.TRUE)
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemApnsP12(apns_p12Request);
        // }
    }

    
    
}
