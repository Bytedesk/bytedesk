package com.bytedesk.team;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.Mockito.when;

// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.modelmapper.ModelMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;

// import com.bytedesk.core.auth.AuthService;
// import com.bytedesk.core.rbac.user.User;
// import com.bytedesk.team.department.Department;
// import com.bytedesk.team.department.DepartmentRepository;
// import com.bytedesk.team.department.DepartmentRequest;
// import com.bytedesk.team.department.DepartmentService;
// import com.bytedesk.team.organization.Organization;
// import com.bytedesk.team.organization.OrganizationService;
// import com.bytedesk.core.utils.Utils;

// @SpringBootTest
// @ActiveProfiles("test")
// public class DepartmentServiceTests {

//     @Autowired
//     private DepartmentService departmentService;

//     @MockBean
//     private ModelMapper modelMapper;

//     @MockBean
//     private AuthService authService;

//     @MockBean
//     private OrganizationService organizationService;

//     @MockBean
//     private DepartmentRepository departmentRepository;

//     private DepartmentRequest departmentRequest;
//     private User user;
//     private Organization organization;

//     @BeforeEach
//     public void setUp() {
//         departmentRequest = new DepartmentRequest();
//         departmentRequest.setDescription("Test department");
//         departmentRequest.setParent_did("parentDid");
//         departmentRequest.setOrgOid("f01f5444ecbc437cb5b8de7ca7dd023c");
//         departmentRequest.setId(1L);
//         departmentRequest.setDid(Utils.getUid());
//         departmentRequest.setNickname("Test Department");
//         departmentRequest.setAvatar("testAvatar");

//         user = new User();
//         user.setUsername("testUser");

//         organization = new Organization();

//         when(authService.getCurrentUser()).thenReturn(user);
//         when(organizationService.findByOid(departmentRequest.getOrgOid())).thenReturn(Optional.of(organization));
//         when(modelMapper.map(departmentRequest, Department.class)).thenAnswer(invocation -> {
//             Department dept = new Department();
//             dept.setDid(departmentRequest.getDid());
//             dept.setOrganization(organization);
//             // dept.setUser(user);
//             return dept;
//         });
//     }

//     @Test
//     public void testCreateDepartmentWithValidRequest() {
//         // Act
//         Department createdDepartment = departmentService.create(departmentRequest);

//         // Assert
//         assertNotNull(createdDepartment);
//         assertEquals(departmentRequest.getDid(), createdDepartment.getDid());
//         assertEquals(organization, createdDepartment.getOrganization());
//         // assertEquals(user, createdDepartment.getUser());

//         // Verify that the repository's save method is called
//         // verify(departmentRepository).save(any(Department.class));
//     }

//     @Test
//     public void testCreateDepartmentWithNonExistingOrgOid() {
//         // Arrange
//         when(organizationService.findByOid(departmentRequest.getOrgOid())).thenReturn(Optional.empty());

//         // Act & Assert
//         // Department createdDepartment = departmentService.create(departmentRequest);
//         // assertNull(createdDepartment);
//     }
// }