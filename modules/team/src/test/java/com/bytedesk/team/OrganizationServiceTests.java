/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 10:27:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-02 10:42:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bytedesk.core.auth.AuthService;
import com.bytedesk.team.organization.OrganizationRepository;
import com.bytedesk.team.organization.OrganizationService;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTests {

    @Mock
    private AuthService authService;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    public void setUp() {
        // 初始化测试环境
    }

    @Test
    public void testQueryMyOrgs() {
        // Arrange
        // PageParam pageParam = new PageParam().setPage(0).setSize(20);
        // User mockUser = new User();
        // Page<Organization> mockPage = new Page<>(Collections.singletonList(new
        // Organization()), 1, 10, 100);

        // when(authService.getCurrentUser()).thenReturn(mockUser);
        // when(organizationRepository.findByUser(any(User.class),
        // any(Pageable.class))).thenReturn(mockPage);

        // // Act
        // Page<Organization> result = organizationService.queryMyOrgs(pageParam);

        // // Assert
        // assertNotNull(result);
        // assertEquals(1, result.getTotalPages());
        // assertEquals(10, result.getTotalElements());
        // assertEquals(1, result.getNumberOfElements());
    }

}
