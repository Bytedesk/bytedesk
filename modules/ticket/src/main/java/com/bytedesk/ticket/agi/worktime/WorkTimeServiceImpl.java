package com.bytedesk.ticket.agi.worktime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.agi.worktime.exception.WorkTimeConfigNotFoundException;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.*;

@Slf4j
@Service
public class WorkTimeServiceImpl implements WorkTimeService {

    @Autowired
    private WorkTimeConfigRepository configRepository;

    @Override
    @Transactional
    public WorkTimeConfig createConfig(WorkTimeConfig config) {
        validateConfig(config);
        return configRepository.save(config);
    }

    @Override
    @Transactional
    public WorkTimeConfig updateConfig(Long configId, WorkTimeConfig config) {
        WorkTimeConfig existingConfig = getConfig(configId);
        validateConfig(config);
        
        existingConfig.setTimezone(config.getTimezone());
        existingConfig.setWorkDays(config.getWorkDays());
        existingConfig.setWorkHours(config.getWorkHours());
        existingConfig.setLunchBreak(config.getLunchBreak());
        existingConfig.setHolidays(config.getHolidays());
        existingConfig.setSpecialWorkdays(config.getSpecialWorkdays());
        
        return configRepository.save(existingConfig);
    }

    @Override
    public WorkTimeConfig getConfig(Long configId) {
        return configRepository.findById(configId)
            .orElseThrow(() -> new WorkTimeConfigNotFoundException(configId));
    }

    @Override
    public List<WorkTimeConfig> getAllConfigs() {
        return configRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteConfig(Long configId) {
        configRepository.deleteById(configId);
    }

    @Override
    public boolean isWorkingHours(Long configId) {
        return isWorkingHours(configId, LocalDateTime.now());
    }

    @Override
    public boolean isWorkingHours(Long configId, LocalDateTime dateTime) {
        WorkTimeConfig config = getConfig(configId);
        
        // 检查是否是工作日
        if (!isWorkDay(config, dateTime)) {
            return false;
        }
        
        // 获取工作时间范围
        WorkingHours workingHours = getWorkingHours(configId, dateTime);
        if (workingHours == null) {
            return false;
        }
        
        LocalTime time = dateTime.toLocalTime();
        
        // 检查是否在工作时间范围内
        if (time.isBefore(workingHours.getStartTime()) || time.isAfter(workingHours.getEndTime())) {
            return false;
        }
        
        // 检查是否在午休时间
        if (workingHours.getLunchBreakStart() != null && workingHours.getLunchBreakEnd() != null) {
            if (time.isAfter(workingHours.getLunchBreakStart()) && 
                time.isBefore(workingHours.getLunchBreakEnd())) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public LocalDateTime getNextWorkingDay(Long configId, LocalDateTime fromTime) {
        WorkTimeConfig config = getConfig(configId);
        LocalDateTime nextDay = fromTime.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        
        while (!isWorkDay(config, nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        
        WorkingHours workingHours = getWorkingHours(configId, nextDay);
        return nextDay.with(workingHours.getStartTime());
    }

    @Override
    public long calculateWorkingMinutes(Long configId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        // WorkTimeConfig config = getConfig(configId);
        long minutes = 0;
        LocalDateTime current = startTime;
        
        while (!current.isAfter(endTime)) {
            if (isWorkingHours(configId, current)) {
                minutes++;
            }
            current = current.plusMinutes(1);
        }
        
        return minutes;
    }

    @Override
    public WorkingHours getWorkingHours(Long configId, LocalDateTime date) {
        WorkTimeConfig config = getConfig(configId);
        
        if (!isWorkDay(config, date)) {
            return null;
        }
        
        WorkingHours hours = new WorkingHours();
        
        // 解析工作时间
        String[] workHours = config.getWorkHours().split("-");
        hours.setStartTime(parseTime(workHours[0]));
        hours.setEndTime(parseTime(workHours[1]));
        
        // 解析午休时间
        if (config.getLunchBreak() != null) {
            String[] lunchHours = config.getLunchBreak().split("-");
            hours.setLunchBreakStart(parseTime(lunchHours[0]));
            hours.setLunchBreakEnd(parseTime(lunchHours[1]));
        }
        
        return hours;
    }

    private void validateConfig(WorkTimeConfig config) {
        Objects.requireNonNull(config.getTimezone(), "Timezone is required");
        Objects.requireNonNull(config.getWorkDays(), "Work days is required");
        Objects.requireNonNull(config.getWorkHours(), "Work hours is required");
        
        // 验证时区
        try {
            ZoneId.of(config.getTimezone());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone: " + config.getTimezone());
        }
        
        // 验证工作日格式
        if (!config.getWorkDays().matches("^[1-7](,[1-7])*$")) {
            throw new IllegalArgumentException("Invalid work days format");
        }
        
        // 验证工作时间格式
        validateTimeRange(config.getWorkHours(), "work hours");
        if (config.getLunchBreak() != null) {
            validateTimeRange(config.getLunchBreak(), "lunch break");
        }
    }

    private void validateTimeRange(String timeRange, String field) {
        if (!timeRange.matches("^([01]?[0-9]|2[0-3])[0-5][0-9]-([01]?[0-9]|2[0-3])[0-5][0-9]$")) {
            throw new IllegalArgumentException("Invalid " + field + " format");
        }
    }

    private boolean isWorkDay(WorkTimeConfig config, LocalDateTime date) {
        // 检查是否是节假日
        if (isHoliday(config, date)) {
            return false;
        }
        
        // 检查是否是特殊工作日
        if (isSpecialWorkday(config, date)) {
            return true;
        }
        
        // 检查是否是常规工作日
        String dayOfWeek = String.valueOf(date.getDayOfWeek().getValue());
        return config.getWorkDays().contains(dayOfWeek);
    }

    private boolean isHoliday(WorkTimeConfig config, LocalDateTime date) {
        if (config.getHolidays() == null) {
            return false;
        }
        return Arrays.asList(config.getHolidays().split(","))
            .contains(date.toLocalDate().toString());
    }

    private boolean isSpecialWorkday(WorkTimeConfig config, LocalDateTime date) {
        if (config.getSpecialWorkdays() == null) {
            return false;
        }
        return Arrays.asList(config.getSpecialWorkdays().split(","))
            .contains(date.toLocalDate().toString());
    }

    private LocalTime parseTime(String time) {
        return LocalTime.parse(String.format("%04d", Integer.parseInt(time)), 
            java.time.format.DateTimeFormatter.ofPattern("HHmm"));
    }

    @Override
    public void addHolidays(Long configId, List<String> dates) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addHolidays'");
    }

    @Override
    public void addSpecialWorkdays(Long configId, List<String> dates) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addSpecialWorkdays'");
    }

    @Override
    public void updateWorkHours(Long configId, String startTime, String endTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateWorkHours'");
    }

    @Override
    public void updateLunchBreak(Long configId, String startTime, String endTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateLunchBreak'");
    }

    @Override
    public void updateWorkDays(Long configId, List<Integer> workDays) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateWorkDays'");
    }
} 