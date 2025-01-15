package com.bytedesk.ticket.agi.worktime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;

/**
 * 工作时间管理服务接口
 * 提供工作时间配置管理、工作时间计算等功能
 */
public interface WorkTimeService {
    
    /**
     * 创建工作时间配置
     * @param config 工作时间配置
     * @return 创建的配置实体
     * @throws IllegalArgumentException 配置参数无效时抛出
     */
    WorkTimeConfig createConfig(WorkTimeConfig config);
    
    /**
     * 更新工作时间配置
     * @param configId 配置ID
     * @param config 新的配置
     * @return 更新后的配置实体
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 配置参数无效时抛出
     */
    WorkTimeConfig updateConfig(Long configId, WorkTimeConfig config);
    
    /**
     * 获取工作时间配置
     * @param configId 配置ID
     * @return 配置实体
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     */
    WorkTimeConfig getConfig(Long configId);
    
    /**
     * 获取所有工作时间配置
     * @return 配置列表
     */
    List<WorkTimeConfig> getAllConfigs();
    
    /**
     * 删除工作时间配置
     * @param configId 配置ID
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     */
    void deleteConfig(Long configId);
    
    /**
     * 检查当前是否在工作时间内
     * @param configId 配置ID
     * @return true如果在工作时间内
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     */
    boolean isWorkingHours(Long configId);
    
    /**
     * 检查指定时间是否在工作时间内
     * @param configId 配置ID
     * @param dateTime 要检查的时间
     * @return true如果在工作时间内
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     */
    boolean isWorkingHours(Long configId, LocalDateTime dateTime);
    
    /**
     * 获取下一个工作日开始时间
     * @param configId 配置ID
     * @param fromTime 起始时间
     * @return 下一个工作日开始时间
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     */
    LocalDateTime getNextWorkingDay(Long configId, LocalDateTime fromTime);
    
    /**
     * 计算工作时间
     * @param configId 配置ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 工作时间（分钟）
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 开始时间晚于结束时间时抛出
     */
    long calculateWorkingMinutes(Long configId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定日期的工作时间范围
     * @param configId 配置ID
     * @param date 日期
     * @return 工作时间范围，如果不是工作日返回null
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     */
    WorkingHours getWorkingHours(Long configId, LocalDateTime date);
    
    /**
     * 添加节假日
     * @param configId 配置ID
     * @param dates 节假日日期列表 (格式: yyyy-MM-dd)
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 日期格式无效时抛出
     */
    void addHolidays(Long configId, List<String> dates);
    
    /**
     * 添加特殊工作日
     * @param configId 配置ID
     * @param dates 特殊工作日日期列表 (格式: yyyy-MM-dd)
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 日期格式无效时抛出
     */
    void addSpecialWorkdays(Long configId, List<String> dates);
    
    /**
     * 更新工作时间
     * @param configId 配置ID
     * @param startTime 开始时间 (格式: HH:mm)
     * @param endTime 结束时间 (格式: HH:mm)
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 时间格式无效时抛出
     */
    void updateWorkHours(Long configId, String startTime, String endTime);
    
    /**
     * 更新午休时间
     * @param configId 配置ID
     * @param startTime 开始时间 (格式: HH:mm)
     * @param endTime 结束时间 (格式: HH:mm)
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 时间格式无效时抛出
     */
    void updateLunchBreak(Long configId, String startTime, String endTime);
    
    /**
     * 更新工作日
     * @param configId 配置ID
     * @param workDays 工作日列表 (1-7, 1表示周一)
     * @throws WorkTimeConfigNotFoundException 配置不存在时抛出
     * @throws IllegalArgumentException 工作日格式无效时抛出
     */
    void updateWorkDays(Long configId, List<Integer> workDays);
}

/**
 * 工作时间范围
 */
@Data
class WorkingHours {
    private LocalTime startTime;      // 工作开始时间
    private LocalTime endTime;        // 工作结束时间
    private LocalTime lunchBreakStart; // 午休开始时间
    private LocalTime lunchBreakEnd;   // 午休结束时间
    
    /**
     * 检查指定时间是否在工作时间范围内
     * @param time 要检查的时间
     * @return true如果在工作时间内
     */
    public boolean isWithinWorkingHours(LocalTime time) {
        if (time.isBefore(startTime) || time.isAfter(endTime)) {
            return false;
        }
        
        if (lunchBreakStart != null && lunchBreakEnd != null) {
            if (time.isAfter(lunchBreakStart) && time.isBefore(lunchBreakEnd)) {
                return false;
            }
        }
        
        return true;
    }
} 