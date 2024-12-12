package com.bytedesk.ticket.worktime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class WorkTimeConfigRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkTimeConfigRepository repository;

    private WorkTimeConfig testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new WorkTimeConfig();
        testConfig.setTimezone("Asia/Shanghai");
        testConfig.setWorkDays("1,2,3,4,5");
        testConfig.setWorkHours("0900-1800");
        testConfig.setLunchBreak("1200-1300");
        testConfig.setHolidays("2024-01-01,2024-02-10");
        testConfig.setSpecialWorkdays("2024-02-04");
        
        // 清除数据并保存测试配置
        entityManager.clear();
        testConfig = entityManager.persistAndFlush(testConfig);
    }

    @Test
    void findById_ExistingConfig_ShouldReturnConfig() {
        Optional<WorkTimeConfig> found = repository.findById(testConfig.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getTimezone()).isEqualTo("Asia/Shanghai");
        assertThat(found.get().getWorkDays()).isEqualTo("1,2,3,4,5");
    }

    @Test
    void findById_NonExistingConfig_ShouldReturnEmpty() {
        Optional<WorkTimeConfig> found = repository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void save_NewConfig_ShouldPersist() {
        WorkTimeConfig newConfig = new WorkTimeConfig();
        newConfig.setTimezone("America/New_York");
        newConfig.setWorkDays("1,2,3,4,5");
        newConfig.setWorkHours("0900-1700");
        
        WorkTimeConfig saved = repository.save(newConfig);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(entityManager.find(WorkTimeConfig.class, saved.getId())).isNotNull();
    }

    @Test
    void update_ExistingConfig_ShouldUpdate() {
        testConfig.setWorkHours("1000-1900");
        // WorkTimeConfig updated = repository.save(testConfig);
        
        WorkTimeConfig found = entityManager.find(WorkTimeConfig.class, testConfig.getId());
        assertThat(found.getWorkHours()).isEqualTo("1000-1900");
    }

    @Test
    void delete_ExistingConfig_ShouldRemove() {
        repository.delete(testConfig);
        
        WorkTimeConfig found = entityManager.find(WorkTimeConfig.class, testConfig.getId());
        assertThat(found).isNull();
    }

    @Test
    void findByHolidayDate_ExistingDate_ShouldReturnConfigs() {
        List<WorkTimeConfig> configs = repository.findByHolidayDate("2024-01-01");
        
        assertThat(configs).isNotEmpty();
        assertThat(configs.get(0).getHolidays()).contains("2024-01-01");
    }

    @Test
    void findByHolidayDate_NonExistingDate_ShouldReturnEmpty() {
        List<WorkTimeConfig> configs = repository.findByHolidayDate("2024-03-01");
        assertThat(configs).isEmpty();
    }

    @Test
    void findBySpecialWorkday_ExistingDate_ShouldReturnConfigs() {
        List<WorkTimeConfig> configs = repository.findBySpecialWorkday("2024-02-04");
        
        assertThat(configs).isNotEmpty();
        assertThat(configs.get(0).getSpecialWorkdays()).contains("2024-02-04");
    }

    @Test
    void findAll_ShouldReturnAllConfigs() {
        // 添加另一个配置
        WorkTimeConfig anotherConfig = new WorkTimeConfig();
        anotherConfig.setTimezone("Europe/London");
        anotherConfig.setWorkDays("1,2,3,4,5");
        anotherConfig.setWorkHours("0900-1700");
        entityManager.persistAndFlush(anotherConfig);
        
        List<WorkTimeConfig> configs = repository.findAll();
        
        assertThat(configs).hasSize(2);
    }

    @Test
    void save_ConfigWithLongHolidays_ShouldPersist() {
        // 测试长假期列表
        StringBuilder holidays = new StringBuilder();
        for (int i = 1; i <= 31; i++) {
            holidays.append(String.format("2024-01-%02d", i)).append(",");
        }
        testConfig.setHolidays(holidays.toString().substring(0, holidays.length() - 1));
        
        WorkTimeConfig saved = repository.save(testConfig);
        
        WorkTimeConfig found = entityManager.find(WorkTimeConfig.class, saved.getId());
        assertThat(found.getHolidays().split(",")).hasSize(31);
    }

    @Test
    void save_ConfigWithNullOptionalFields_ShouldPersist() {
        WorkTimeConfig minimalConfig = new WorkTimeConfig();
        minimalConfig.setTimezone("UTC");
        minimalConfig.setWorkDays("1,2,3,4,5");
        minimalConfig.setWorkHours("0900-1700");
        // 不设置可选字段
        
        WorkTimeConfig saved = repository.save(minimalConfig);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLunchBreak()).isNull();
        assertThat(saved.getHolidays()).isNull();
        assertThat(saved.getSpecialWorkdays()).isNull();
    }
} 