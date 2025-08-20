/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 13:18:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.shopping;

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
import com.bytedesk.core.base.BaseRestServiceWithExcelImproved;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ShoppingRestService extends BaseRestServiceWithExcelImproved<ShoppingEntity, ShoppingRequest, ShoppingResponse, ShoppingExcel> {

    private final ShoppingRepository shoppingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<ShoppingEntity> createSpecification(ShoppingRequest request) {
        return ShoppingSpecification.search(request);
    }

    @Override
    protected Page<ShoppingEntity> executePageQuery(Specification<ShoppingEntity> spec, Pageable pageable) {
        return shoppingRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "shopping", key = "#uid", unless="#result==null")
    @Override
    public Optional<ShoppingEntity> findByUid(String uid) {
        return shoppingRepository.findByUid(uid);
    }

    @Cacheable(value = "shopping", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ShoppingEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return shoppingRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return shoppingRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ShoppingResponse create(ShoppingRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ShoppingEntity> shopping = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (shopping.isPresent()) {
                return convertToResponse(shopping.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        ShoppingEntity entity = modelMapper.map(request, ShoppingEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ShoppingEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create shopping failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ShoppingResponse update(ShoppingRequest request) {
        Optional<ShoppingEntity> optional = shoppingRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ShoppingEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ShoppingEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update shopping failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Shopping not found");
        }
    }

    @Override
    protected ShoppingEntity doSave(ShoppingEntity entity) {
        return shoppingRepository.save(entity);
    }

    @Override
    public ShoppingEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ShoppingEntity entity) {
        try {
            Optional<ShoppingEntity> latest = shoppingRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ShoppingEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return shoppingRepository.save(latestEntity);
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
        Optional<ShoppingEntity> optional = shoppingRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // shoppingRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Shopping not found");
        }
    }

    @Override
    public void delete(ShoppingRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ShoppingResponse convertToResponse(ShoppingEntity entity) {
        return modelMapper.map(entity, ShoppingResponse.class);
    }

    @Override
    public ShoppingExcel convertToExcel(ShoppingEntity entity) {
        return modelMapper.map(entity, ShoppingExcel.class);
    }
    
    public void initShoppings(String orgUid) {
        // log.info("initThreadShopping");
        // for (String shopping : ShoppingInitData.getAllShoppings()) {
        //     ShoppingRequest shoppingRequest = ShoppingRequest.builder()
        //             .uid(Utils.formatUid(orgUid, shopping))
        //             .name(shopping)
        //             .order(0)
        //             .type(ShoppingTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(shoppingRequest);
        // }
    }
    
}
