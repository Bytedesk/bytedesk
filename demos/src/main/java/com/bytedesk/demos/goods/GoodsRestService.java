/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.demos.goods;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class GoodsRestService extends BaseRestServiceWithExport<GoodsEntity, GoodsRequest, GoodsResponse, GoodsExcel> {

    private final GoodsRepository goodsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<GoodsEntity> createSpecification(GoodsRequest request) {
        return GoodsSpecification.search(request, authService);
    }

    @Override
    protected Page<GoodsEntity> executePageQuery(Specification<GoodsEntity> spec, Pageable pageable) {
        return goodsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "goods", key = "#uid", unless="#result==null")
    @Override
    public Optional<GoodsEntity> findByUid(String uid) {
        return goodsRepository.findByUid(uid);
    }

    @Cacheable(value = "goods", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<GoodsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return goodsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return goodsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public GoodsResponse create(GoodsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<GoodsEntity> goods = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (goods.isPresent()) {
                return convertToResponse(goods.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        GoodsEntity entity = modelMapper.map(request, GoodsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        GoodsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create goods failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public GoodsResponse update(GoodsRequest request) {
        Optional<GoodsEntity> optional = goodsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            GoodsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            GoodsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update goods failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Goods not found");
        }
    }

    @Override
    protected GoodsEntity doSave(GoodsEntity entity) {
        return goodsRepository.save(entity);
    }

    @Override
    public GoodsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, GoodsEntity entity) {
        try {
            Optional<GoodsEntity> latest = goodsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                GoodsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return goodsRepository.save(latestEntity);
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
        Optional<GoodsEntity> optional = goodsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // goodsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Goods not found");
        }
    }

    @Override
    public void delete(GoodsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public GoodsResponse convertToResponse(GoodsEntity entity) {
        return modelMapper.map(entity, GoodsResponse.class);
    }

    @Override
    public GoodsExcel convertToExcel(GoodsEntity entity) {
        return modelMapper.map(entity, GoodsExcel.class);
    }
    
    public void initGoodss(String orgUid) {
        // log.info("initThreadGoods");
        for (String goods : GoodsInitData.getAllGoodss()) {
            GoodsRequest goodsRequest = GoodsRequest.builder()
                    .uid(Utils.formatUid(orgUid, goods))
                    .name(goods)
                    .order(0)
                    .type(GoodsTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(goodsRequest);
        }
    }

    
    
}
