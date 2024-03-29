/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 09:13:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-02 10:12:12
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

import com.bytedesk.team.department.DepartmentController;
import com.bytedesk.team.department.DepartmentRequest;
import com.bytedesk.team.department.DepartmentService;
import com.bytedesk.core.utils.JsonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class DepartmentControllerTests {

    @InjectMocks
    private DepartmentController departmentController;

    @Mock
    private DepartmentService departmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    public void testCreateSuccess() {
        // Arrange
        DepartmentRequest departmentRequest = new DepartmentRequest();
        departmentRequest.setNickname("Test Department");
        departmentRequest.setParent_did(null);
        departmentRequest.setOrg_oid("f01f5444ecbc437cb5b8de7ca7dd023c");

        // Act
        ResponseEntity<JsonResult<?>> response = departmentController.create(departmentRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        JsonResult<?> result = response.getBody();
        assertEquals("创建成功", result.getMessage());
        assertEquals(200, result.getCode());
    }

    @SuppressWarnings("null")
    @Test
    public void testCreateFailure() {
        // Arrange
        DepartmentRequest departmentRequest = new DepartmentRequest();
        departmentRequest.setDid("testDid");
        departmentRequest.setParent_did(null);
        departmentRequest.setOrg_oid("testOrgOid");

        // Act
        ResponseEntity<JsonResult<?>> response = departmentController.create(departmentRequest);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        JsonResult<?> result = response.getBody();
        assertEquals("create dep failed", result.getMessage());
        assertEquals(-1, result.getCode());
        assertEquals(false, result.getData());
    }
}