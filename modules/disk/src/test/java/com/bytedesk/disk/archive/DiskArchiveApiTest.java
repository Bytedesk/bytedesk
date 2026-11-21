package com.bytedesk.disk.archive;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.disk.audit.DiskLogRepository;
import jakarta.persistence.EntityManagerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.bytedesk.disk.archive.DiskArchivePermissions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(DiskArchiveApiTest.Config.class)
@WebAppConfiguration
class DiskArchiveApiTest {
//     private static final Set<String> ALL_CONTRACT = Set.of(read("CONTRACT"), upload("CONTRACT"),
//             delete("CONTRACT"), manage("CONTRACT"));
    private static final Set<String> BOTH_MODULES = Set.of(read("CONTRACT"), upload("CONTRACT"),
            delete("CONTRACT"), manage("CONTRACT"), read("INVOICE"), upload("INVOICE"),
            delete("INVOICE"), manage("INVOICE"));
    private static final byte[] PDF = "%PDF-1.7\narchive test content".getBytes(java.nio.charset.StandardCharsets.UTF_8);
    private static final String API = "/api/v1/disk/archive";

    @Configuration
    @EnableWebMvc
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = {DiskArchiveFileRepository.class, DiskLogRepository.class})
    @Import({DiskArchiveService.class, DiskArchiveRestController.class, DiskArchiveAudit.class,
            com.bytedesk.core.config.JpaAuditingConfig.class})
    static class Config {
        @Bean DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:archive-api;DB_CLOSE_DELAY=-1", "sa", "");
        }
        @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource source) {
            var factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(source);
            factory.setPackagesToScan("com.bytedesk.disk.archive", "com.bytedesk.disk.audit");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop",
                    "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl"));
            return factory;
        }
        @Bean PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
            return new JpaTransactionManager(factory);
        }
        @Bean DiskArchiveAccess access() { return mock(DiskArchiveAccess.class, CALLS_REAL_METHODS); }
        @Bean DiskArchiveSettings settings() { return new DiskArchiveSettings(); }
        @Bean DiskArchiveStorage storage(DiskArchiveSettings settings) {
            var properties = mock(BytedeskProperties.class);
            when(properties.getUploadDir()).thenReturn(System.getProperty("java.io.tmpdir") + "/archive-public-test");
            return new DiskArchiveStorage(settings, properties, new MockEnvironment()
                    .withProperty("spring.servlet.multipart.max-file-size", "100MB")
                    .withProperty("spring.servlet.multipart.max-request-size", "100MB"));
        }
    }

    @TempDir Path privateDirectory;
    @Autowired WebApplicationContext context;
    @Autowired DiskArchiveAccess access;
    @Autowired DiskArchiveService service;
    @Autowired DiskArchiveSettings settings;
    @Autowired DiskArchiveFileRepository files;
    @Autowired DiskLogRepository logs;
    @org.springframework.test.context.bean.override.mockito.MockitoSpyBean DiskArchiveAudit audit;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        files.deleteAll();
        logs.deleteAll();
        reset(access);
        reset(audit);
        actor("owner", "org", BOTH_MODULES);
        doAnswer(call -> new DiskArchiveAccess.Actor(call.getArgument(0), call.getArgument(1),
                Set.of(read("CONTRACT"))))
                        .when(access).forUser(anyString(), anyString());
        settings.setPrivateDirectory(privateDirectory.toString());
        settings.setMaxFileSize(20L * 1024 * 1024);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private void actor(String uid, String org, Set<String> permissions) {
        doReturn(new DiskArchiveAccess.Actor(uid, org, permissions)).when(access).current();
    }

    private DiskArchiveRequest query(String module) {
        var request = new DiskArchiveRequest();
        request.setModule(module);
        return request;
    }

    private DiskArchiveRequest mutate(DiskArchiveResponse file) {
        var request = new DiskArchiveRequest();
        request.setUid(file.getUid());
        request.setVersion(file.getVersion());
        request.setModule(file.getModule());
        return request;
    }

    private DiskArchiveResponse uploadFile(String module, String requestId, String contractNumber) {
        var request = new DiskArchiveRequest();
        request.setModule(module);
        request.setUploadRequestId(requestId);
        request.setContractNumber(contractNumber);
        return service.upload(new MockMultipartFile("file", "contract.pdf", "application/pdf", PDF), request);
    }

    @Test
    void dispatcherWiresCapabilitiesQueryDetailAndMetadataUpdate() throws Exception {
        mvc.perform(get(API + "/capabilities").param("module", "CONTRACT")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.canRead").value(true))
                .andExpect(jsonPath("$.data.canUpload").value(true))
                .andExpect(jsonPath("$.data.storageConfigured").value(true))
                .andExpect(header().string("Cache-Control", "private, no-store"));
        var file = uploadFile("CONTRACT", "cap-1", "HT-001");
        mvc.perform(get(API + "/query").param("module", "CONTRACT")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].contractNumber").value("HT-001"));
        mvc.perform(get(API + "/query/uid").param("uid", file.getUid())).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storageKey").doesNotExist())
                .andExpect(jsonPath("$.data.actions").value(org.hamcrest.Matchers.hasItem("DOWNLOAD")));
        var update = mutate(file);
        update.setCounterparty("对方A");
        update.setOriginalName("renamed.pdf");
        mvc.perform(post(API + "/update").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(update))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.counterparty").value("对方A"))
                .andExpect(jsonPath("$.data.originalName").value("renamed.pdf"));
        mvc.perform(get(API + "/query").param("module", "CONTRACT").param("search", "对方a"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.totalElements").value(1));
        mvc.perform(get(API + "/query").param("module", "INVOICE")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
        mvc.perform(get(API + "/capabilities").param("module", "BAD")).andExpect(status().isBadRequest());
        mvc.perform(get(API + "/query").param("module", "BAD")).andExpect(status().isBadRequest());
    }

    @Test
    void modulesAreIndependentAndDuplicateNumbersCoexist() {
        var first = uploadFile("CONTRACT", "dup-1", "HT-SAME");
        var second = uploadFile("CONTRACT", "dup-2", "HT-SAME");
        assertNotEquals(first.getUid(), second.getUid());
        assertEquals(2, service.query(query("CONTRACT"), false).getTotalElements());
        var invoice = uploadFile("INVOICE", "inv-1", null);
        assertEquals("INVOICE", invoice.getModule());
        assertEquals(2, service.query(query("CONTRACT"), false).getTotalElements());
        assertEquals(1, service.query(query("INVOICE"), false).getTotalElements());
        uploadFile("INVOICE", "inv-2", "HT-SAME");
        assertEquals(2, service.query(query("INVOICE"), false).getTotalElements());
    }

    @Test
    void permissionsGateEveryModuleAction() throws Exception {
        actor("reader", "org", Set.of(read("CONTRACT")));
        mvc.perform(get(API + "/capabilities").param("module", "CONTRACT")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.canUpload").value(false));
        assertThrows(DiskArchiveException.class, () -> uploadFile("CONTRACT", "no-perm", null));
        mvc.perform(get(API + "/capabilities").param("module", "INVOICE")).andExpect(status().isForbidden());
        mvc.perform(get(API + "/query").param("module", "INVOICE")).andExpect(status().isForbidden());
        actor("uploader", "org", Set.of(read("CONTRACT"), upload("CONTRACT")));
        var file = uploadFile("CONTRACT", "own-1", null);
        mvc.perform(get(API + "/file/preview").param("uid", file.getUid())).andExpect(status().isOk())
                .andExpect(content().bytes(PDF))
                .andExpect(header().string("Content-Type", "application/pdf"));
        actor("stranger", "org", Set.of(read("CONTRACT"), upload("CONTRACT"), delete("CONTRACT")));
        var strangerUpdate = mutate(file);
        strangerUpdate.setRemark("not mine");
        mvc.perform(post(API + "/update").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(strangerUpdate))).andExpect(status().isForbidden());
        mvc.perform(post(API + "/delete").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(mutate(file)))).andExpect(status().isForbidden());
        actor("manager", "org", Set.of(read("CONTRACT"), manage("CONTRACT")));
        mvc.perform(post(API + "/delete").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(mutate(file)))).andExpect(status().isOk());
        actor("owner", "org", Set.of(read("CONTRACT")));
        mvc.perform(get(API + "/query/recycle").param("module", "CONTRACT")).andExpect(status().isForbidden());
        actor("owner", "org", Set.of(read("CONTRACT"), manage("CONTRACT")));
        mvc.perform(get(API + "/query/recycle").param("module", "CONTRACT")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));
        mvc.perform(get(API + "/file/download").param("uid", file.getUid())).andExpect(status().isNotFound());
    }

    @Test
    void deleteRestoreCycleKeepsBytesAndVersionConflictsRejected() throws Exception {
        var file = uploadFile("CONTRACT", "cycle-1", "HT-CY");
        mvc.perform(post(API + "/delete").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(mutate(file)))).andExpect(status().isOk());
        mvc.perform(get(API + "/file/download").param("uid", file.getUid())).andExpect(status().isNotFound());
        var deleted = files.findByUidAndOrgUid(file.getUid(), "org").orElseThrow();
        var stale = new DiskArchiveRequest();
        stale.setUid(deleted.getUid());
        stale.setVersion(deleted.getVersion() - 1);
        stale.setModule("CONTRACT");
        mvc.perform(post(API + "/restore").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(stale))).andExpect(status().isConflict());
        var restore = new DiskArchiveRequest();
        restore.setUid(deleted.getUid());
        restore.setVersion(deleted.getVersion());
        restore.setModule("CONTRACT");
        mvc.perform(post(API + "/restore").contentType("application/json")
                .content(com.alibaba.fastjson2.JSON.toJSONString(restore))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleted").value(false));
        mvc.perform(get(API + "/file/download").param("uid", file.getUid())).andExpect(status().isOk())
                .andExpect(content().bytes(PDF));
    }

    @Test
    void duplicateUploadsAreIdempotentAndFailedWritesAreReconciled() throws Exception {
        var one = uploadFile("CONTRACT", "same-request", "HT-IDEM");
        var two = uploadFile("CONTRACT", "same-request", "ignored");
        assertEquals(one.getUid(), two.getUid());
        assertEquals("HT-IDEM", two.getContractNumber());
        assertEquals(1, files.count());
        var request = new DiskArchiveRequest();
        request.setModule("CONTRACT");
        request.setUploadRequestId("bad-content");
        assertThrows(DiskArchiveException.class, () -> service.upload(
                new MockMultipartFile("file", "bad.pdf", "application/pdf", "not a pdf".getBytes()), request));
        var failed = service.upload(new MockMultipartFile("file", "bad.pdf", "application/pdf", PDF), request);
        assertEquals("FAILED", failed.getUploadState());
        assertEquals(2, files.count());
        try (var objects = Files.list(privateDirectory)) {
            assertEquals(1, objects.count());
        }
    }

    @Test
    void auditFailureAfterWriteRollsBackAndCleansUnpublishedObject() throws Exception {
        doThrow(new org.springframework.dao.DataAccessResourceFailureException("test audit failure"))
                .when(audit).success(any(), anyString(), eq("FILE_UPLOAD"));
        assertThrows(org.springframework.dao.DataAccessResourceFailureException.class,
                () -> uploadFile("CONTRACT", "fail-after-write", null));
        var file = files.findAll().getFirst();
        assertEquals("FAILED", file.getUploadState());
        assertTrue(file.isStorageCleared());
        assertFalse(Files.exists(privateDirectory.resolve(file.getStorageKey())));
    }
}
