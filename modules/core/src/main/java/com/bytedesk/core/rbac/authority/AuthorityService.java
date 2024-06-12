/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-11 17:47:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.uid.UidUtils;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Authority create(AuthorityRequest authorityRequest) {
        //
        Authority authority = modelMapper.map(authorityRequest, Authority.class);
        authority.setUid(uidUtils.getCacheSerialUid());

        return save(authority);
    }

    @Cacheable(value = "authority", key = "#uid", unless = "#result == null")
    public Optional<Authority> findByUid(String uid) {
        return authorityRepository.findByUid(uid);
    }

    @Cacheable(value = "authority", key = "#value", unless = "#result == null")
    public Optional<Authority> findByValue(String value) {
        return authorityRepository.findByValue(value);
    }

    public Authority save(Authority authority) {
        return authorityRepository.save(authority);
    }

    public void initData() {

        if (authorityRepository.count() > 0) {
            return;
        }
        //
        String[] authorities = {
                TypeConsts.SUPER,
                TypeConsts.ADMIN,
                TypeConsts.HR,
                TypeConsts.ORG,
                TypeConsts.IT,
                TypeConsts.MONEY,
                TypeConsts.MARKETING,
                TypeConsts.SALES,
                TypeConsts.CUSTOMER_SERVICE,
        };

        for (String authority : authorities) {
            AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(authority)
                    .value(authority)
                    .description(authority)
                    .build();
            authRequest.setType(TypeConsts.TYPE_SYSTEM);
            //
            create(authRequest);
        }

    }

}
