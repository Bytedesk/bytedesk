package com.bytedesk.freeswitch.controller;

import com.bytedesk.core.rbac.annotation.CurrentUser;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.freeswitch.dto.FreeSwitchCdrDto;
import com.bytedesk.freeswitch.dto.FreeSwitchDtoMapper;
import com.bytedesk.freeswitch.model.FreeSwitchCdrEntity;
import com.bytedesk.freeswitch.service.FreeSwitchCdrService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FreeSwitch通话详单REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch/cdr")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchCdrController {

    private final FreeSwitchCdrService cdrService;
    private final FreeSwitchDtoMapper dtoMapper;

    /**
     * 获取通话详单列表
     */
    @GetMapping
    public ResponseEntity<JsonResult<Page<FreeSwitchCdrDto>>> getCdrList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String callerNumber,
            @RequestParam(required = false) String destinationNumber,
            @RequestParam(required = false) String direction_filter,
            @RequestParam(required = false) String hangupCause,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<FreeSwitchCdrEntity> entities;
            
            if (startTime != null && endTime != null) {
                entities = cdrService.findByTimeRange(startTime, endTime, pageable);
            } else if (callerNumber != null) {
                entities = cdrService.findByCallerNumber(callerNumber, pageable);
            } else if (destinationNumber != null) {
                entities = cdrService.findByDestinationNumber(destinationNumber, pageable);
            } else if (direction_filter != null) {
                entities = cdrService.findByDirection(direction_filter, pageable);
            } else if (hangupCause != null) {
                entities = cdrService.findByHangupCause(hangupCause, pageable);
            } else {
                entities = cdrService.findAll(pageable);
            }
            
            Page<FreeSwitchCdrDto> dtos = entities.map(dtoMapper::toCdrDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取FreeSwitch CDR列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取通话详单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchCdrDto>> getCdrById(@PathVariable Long id) {
        try {
            Optional<FreeSwitchCdrEntity> entity = cdrService.findById(id);
            if (entity.isPresent()) {
                FreeSwitchCdrDto dto = dtoMapper.toCdrDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch CDR详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据UUID获取通话详单详情
     */
    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<JsonResult<FreeSwitchCdrDto>> getCdrByUuid(@PathVariable String uuid) {
        try {
            Optional<FreeSwitchCdrEntity> entity = cdrService.findByUuid(uuid);
            if (entity.isPresent()) {
                FreeSwitchCdrDto dto = dtoMapper.toCdrDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch CDR详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取今日通话统计
     */
    @GetMapping("/statistics/today")
    public ResponseEntity<JsonResult<FreeSwitchCdrService.CallStatistics>> getTodayStatistics() {
        try {
            FreeSwitchCdrService.CallStatistics statistics = cdrService.getTodayStatistics();
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取今日通话统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取时间范围内的通话统计
     */
    @GetMapping("/statistics/range")
    public ResponseEntity<JsonResult<FreeSwitchCdrService.CallStatistics>> getStatisticsByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            FreeSwitchCdrService.CallStatistics statistics = cdrService.getStatisticsByTimeRange(startTime, endTime);
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取时间范围通话统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取用户通话统计
     */
    @GetMapping("/statistics/user/{callerNumber}")
    public ResponseEntity<JsonResult<FreeSwitchCdrService.CallStatistics>> getUserStatistics(
            @PathVariable String callerNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            FreeSwitchCdrService.CallStatistics statistics;
            if (startTime != null && endTime != null) {
                statistics = cdrService.getUserStatistics(callerNumber, startTime, endTime);
            } else {
                statistics = cdrService.getUserStatistics(callerNumber);
            }
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取用户通话统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取通话方向统计
     */
    @GetMapping("/statistics/direction")
    public ResponseEntity<JsonResult<List<Object[]>>> getCallDirectionStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            List<Object[]> statistics;
            if (startTime != null && endTime != null) {
                statistics = cdrService.getCallDirectionStatistics(startTime, endTime);
            } else {
                statistics = cdrService.getCallDirectionStatistics();
            }
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取通话方向统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取挂断原因统计
     */
    @GetMapping("/statistics/hangup-cause")
    public ResponseEntity<JsonResult<List<Object[]>>> getHangupCauseStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        try {
            List<Object[]> statistics;
            if (startTime != null && endTime != null) {
                statistics = cdrService.getHangupCauseStatistics(startTime, endTime);
            } else {
                statistics = cdrService.getHangupCauseStatistics();
            }
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取挂断原因统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取长通话记录
     */
    @GetMapping("/long-calls")
    public ResponseEntity<JsonResult<List<FreeSwitchCdrDto>>> getLongCalls(
            @RequestParam(defaultValue = "300") long minDuration) {
        
        try {
            List<FreeSwitchCdrEntity> entities = cdrService.findLongCalls(minDuration);
            List<FreeSwitchCdrDto> dtos = entities.stream()
                    .map(dtoMapper::toCdrDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取长通话记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取失败通话记录
     */
    @GetMapping("/failed-calls")
    public ResponseEntity<JsonResult<Page<FreeSwitchCdrDto>>> getFailedCalls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
            Page<FreeSwitchCdrEntity> entities = cdrService.findFailedCalls(pageable);
            Page<FreeSwitchCdrDto> dtos = entities.map(dtoMapper::toCdrDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取失败通话记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除过期的CDR记录
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<JsonResult<Integer>> cleanupExpiredCdr(
            @RequestParam(defaultValue = "90") int retentionDays,
            @CurrentUser UserEntity currentUser) {
        
        try {
            int deletedCount = cdrService.cleanupExpiredRecords(retentionDays);
            return ResponseEntity.ok(JsonResult.success(deletedCount, 
                    String.format("已清理%d条过期CDR记录", deletedCount)));
        } catch (Exception e) {
            log.error("清理过期CDR记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 搜索通话记录
     */
    @GetMapping("/search")
    public ResponseEntity<JsonResult<Page<FreeSwitchCdrDto>>> searchCdr(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
            Page<FreeSwitchCdrEntity> entities = cdrService.searchCdr(keyword, pageable);
            Page<FreeSwitchCdrDto> dtos = entities.map(dtoMapper::toCdrDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("搜索CDR记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 导出CDR记录
     */
    @GetMapping("/export")
    public ResponseEntity<JsonResult<String>> exportCdr(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "csv") String format) {
        
        try {
            // 这里可以实现实际的导出逻辑
            // 返回导出文件的下载链接或直接返回数据
            
            String message = String.format("CDR记录导出请求已提交，格式: %s", format);
            if (startTime != null && endTime != null) {
                message += String.format("，时间范围: %s 至 %s", startTime, endTime);
            }
            
            return ResponseEntity.ok(JsonResult.success("", message));
        } catch (Exception e) {
            log.error("导出CDR记录失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }
}
