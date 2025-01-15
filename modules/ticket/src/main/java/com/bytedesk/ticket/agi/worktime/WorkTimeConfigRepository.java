package com.bytedesk.ticket.agi.worktime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface WorkTimeConfigRepository extends JpaRepository<WorkTimeConfig, Long> {
    
    @Query("SELECT c FROM WorkTimeConfig c WHERE c.holidays LIKE %?1%")
    List<WorkTimeConfig> findByHolidayDate(String date);
    
    @Query("SELECT c FROM WorkTimeConfig c WHERE c.specialWorkdays LIKE %?1%")
    List<WorkTimeConfig> findBySpecialWorkday(String date);
} 