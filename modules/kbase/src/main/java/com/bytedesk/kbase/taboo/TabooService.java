/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:35:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-01 11:03:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TabooService extends BaseService<Taboo, TabooRequest, TabooResponse> {

    private final TabooRepository tabooRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<TabooResponse> queryByOrg(TabooRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<Taboo> specification = TabooSpecification.search(request);
        
        Page<Taboo> page = tabooRepository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TabooResponse> queryByUser(TabooRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Taboo> findByUid(String uid) {
        return tabooRepository.findByUid(uid);
    }

    @Override
    public TabooResponse create(TabooRequest request) {
        
        Taboo taboo = modelMapper.map(request, Taboo.class);
        taboo.setUid(uidUtils.getCacheSerialUid());

        Taboo savedTaboo = save(taboo);
        if (savedTaboo == null) {
            throw new RuntimeException("create taboo failed");
        }
        return convertToResponse(savedTaboo);
    }

    @Override
    public TabooResponse update(TabooRequest request) {
        
        Optional<Taboo> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            Taboo taboo = optional.get();
            taboo.setContent(request.getContent());
            // 
            Taboo savedTaboo = save(taboo);
            if (savedTaboo == null) {
                throw new RuntimeException("create taboo failed");
            }
            return convertToResponse(savedTaboo);
        } else {
            throw new RuntimeException("update taboo failed");
        }
    }

    @Override
    public Taboo save(Taboo entity) {
        try {
            return tabooRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void save(List<Taboo> entities) {
        tabooRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Taboo> optional = findByUid(uid);
        if (optional.isPresent()) {
            Taboo taboo = optional.get();
            taboo.setDeleted(true);
            save(taboo);
        }
    }

    @Override
    public void delete(Taboo entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Taboo entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TabooResponse convertToResponse(Taboo entity) {
        return modelMapper.map(entity, TabooResponse.class);
    }

    public TabooExcel convertToExcel(TabooResponse response) {
        return modelMapper.map(response, TabooExcel.class);
    }

    public Taboo convertExcelToTaboo(TabooExcel excel, String categoryUid, String kbUid, String orgUid) {
        // return modelMapper.map(excel, Taboo.class);
        Taboo taboo = Taboo.builder().build();
        taboo.setUid(uidUtils.getCacheSerialUid());
        taboo.setContent(excel.getContent());
        // 
        taboo.setCategoryUid(categoryUid);
        taboo.setKbUid(kbUid);
        taboo.setOrgUid(orgUid);

        return taboo;
    }
    
}
