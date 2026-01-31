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
package com.bytedesk.crm.product;

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
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ProductRestService extends BaseRestServiceWithExport<ProductEntity, ProductRequest, ProductResponse, ProductExcel> {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ProductEntity> queryByOrgEntity(ProductRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ProductEntity> specs = ProductSpecification.search(request, authService);
        return productRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ProductResponse> queryByOrg(ProductRequest request) {
        Page<ProductEntity> productPage = queryByOrgEntity(request);
        return productPage.map(this::convertToResponse);
    }

    @Override
    public Page<ProductResponse> queryByUser(ProductRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "product", key = "#uid", unless="#result==null")
    @Override
    public Optional<ProductEntity> findByUid(String uid) {
        return productRepository.findByUid(uid);
    }

    @Cacheable(value = "product", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ProductEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return productRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return productRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ProductResponse create(ProductRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ProductResponse createSystemProduct(ProductRequest request) {
        return createInternal(request, true);
    }

    private ProductResponse createInternal(ProductRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ProductEntity> product = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (product.isPresent()) {
                return convertToResponse(product.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ProductPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ProductEntity entity = modelMapper.map(request, ProductEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ProductEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create product failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ProductResponse update(ProductRequest request) {
        Optional<ProductEntity> optional = productRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ProductEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ProductPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ProductEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update product failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Product not found");
        }
    }

    @Override
    protected ProductEntity doSave(ProductEntity entity) {
        return productRepository.save(entity);
    }

    @Override
    public ProductEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ProductEntity entity) {
        try {
            Optional<ProductEntity> latest = productRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ProductEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return productRepository.save(latestEntity);
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
        Optional<ProductEntity> optional = productRepository.findByUid(uid);
        if (optional.isPresent()) {
            ProductEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ProductPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // productRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Product not found");
        }
    }

    @Override
    public void delete(ProductRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ProductResponse convertToResponse(ProductEntity entity) {
        return modelMapper.map(entity, ProductResponse.class);
    }

    @Override
    public ProductExcel convertToExcel(ProductEntity entity) {
        return modelMapper.map(entity, ProductExcel.class);
    }

    @Override
    protected Specification<ProductEntity> createSpecification(ProductRequest request) {
        return ProductSpecification.search(request, authService);
    }

    @Override
    protected Page<ProductEntity> executePageQuery(Specification<ProductEntity> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }
    
    public void initProducts(String orgUid) {
        // log.info("initProductProduct");
        // for (String product : ProductInitData.getAllProducts()) {
        //     ProductRequest productRequest = ProductRequest.builder()
        //             .uid(Utils.formatUid(orgUid, product))
        //             .name(product)
        //             .order(0)
        //             .type(ProductTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemProduct(productRequest);
        // }
    }

    
    
}
