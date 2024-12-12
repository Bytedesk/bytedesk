package com.bytedesk.ticket.worktime;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_ticket_worktime_config")
@EqualsAndHashCode(callSuper = true)
public class WorkTimeConfig extends BaseEntity {

    @Column(nullable = false)
    private String timezone;
    
    @Column(name = "work_days")
    private String workDays;  // 1,2,3,4,5 表示周一到周五
    
    @Column(name = "work_hours")
    private String workHours;  // 09:00-18:00
    
    @Column(name = "lunch_break")
    private String lunchBreak;  // 12:00-13:00
    
    @Column(name = "holidays")
    private String holidays;  // 2024-01-01,2024-02-10
    
    @Column(name = "special_workdays")
    private String specialWorkdays;  // 2024-02-04 补班
} 