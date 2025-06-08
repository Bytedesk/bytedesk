package com.bytedesk.freeswitch.controller;

import com.bytedesk.core.rbac.annotation.CurrentUser;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.freeswitch.dto.*;
import com.bytedesk.freeswitch.model.FreeSwitchGatewayEntity;
import com.bytedesk.freeswitch.service.FreeSwitchGatewayService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FreeSwitch网关管理REST API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch/gateways")
@AllArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true")
public class FreeSwitchGatewayController {

    private final FreeSwitchGatewayService gatewayService;
    private final FreeSwitchDtoMapper dtoMapper;

    /**
     * 创建网关
     */
    @PostMapping
    public ResponseEntity<JsonResult<FreeSwitchGatewayDto>> createGateway(
            @Valid @RequestBody CreateFreeSwitchGatewayRequest request,
            @CurrentUser UserEntity currentUser) {
        
        try {
            FreeSwitchGatewayEntity created = gatewayService.createGateway(
                    request.getName(),
                    request.getProfile(),
                    request.getProxy(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getPort(),
                    request.getRegister(),
                    request.getExpireSeconds()
            );
            
            FreeSwitchGatewayDto dto = dtoMapper.toGatewayDto(created);
            return ResponseEntity.ok(JsonResult.success(dto, "网关创建成功"));
        } catch (Exception e) {
            log.error("创建FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取网关列表
     */
    @GetMapping
    public ResponseEntity<JsonResult<Page<FreeSwitchGatewayDto>>> getGateways(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String profile,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean enabled) {
        
        try {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<FreeSwitchGatewayEntity> entities;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                entities = gatewayService.searchGateways(keyword, pageable);
            } else {
                entities = gatewayService.searchGateways("", pageable);
            }
            
            Page<FreeSwitchGatewayDto> dtos = entities.map(dtoMapper::toGatewayDto);
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取FreeSwitch网关列表失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取网关详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchGatewayDto>> getGatewayById(@PathVariable Long id) {
        try {
            Optional<FreeSwitchGatewayEntity> entity = gatewayService.findById(id);
            if (entity.isPresent()) {
                FreeSwitchGatewayDto dto = dtoMapper.toGatewayDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch网关详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据名称获取网关详情
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<JsonResult<FreeSwitchGatewayDto>> getGatewayByName(@PathVariable String name) {
        try {
            Optional<FreeSwitchGatewayEntity> entity = gatewayService.findByName(name);
            if (entity.isPresent()) {
                FreeSwitchGatewayDto dto = dtoMapper.toGatewayDto(entity.get());
                return ResponseEntity.ok(JsonResult.success(dto));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取FreeSwitch网关详情失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新网关信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<JsonResult<FreeSwitchGatewayDto>> updateGateway(
            @PathVariable Long id,
            @Valid @RequestBody CreateFreeSwitchGatewayRequest request,
            @CurrentUser UserEntity currentUser) {
        
        try {
            FreeSwitchGatewayEntity updated = gatewayService.updateGateway(
                    id,
                    request.getName(),
                    request.getProfile(),
                    request.getProxy(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getPort(),
                    request.getRegister(),
                    request.getExpireSeconds(),
                    request.getEnabled()
            );
            
            FreeSwitchGatewayDto dto = dtoMapper.toGatewayDto(updated);
            return ResponseEntity.ok(JsonResult.success(dto, "网关更新成功"));
        } catch (Exception e) {
            log.error("更新FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 删除网关
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResult<Void>> deleteGateway(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            gatewayService.deleteGateway(id);
            return ResponseEntity.ok(JsonResult.success("网关删除成功"));
        } catch (Exception e) {
            log.error("删除FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 启用/禁用网关
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<JsonResult<Void>> toggleGatewayStatus(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            gatewayService.toggleGatewayStatus(id);
            return ResponseEntity.ok(JsonResult.success("网关状态切换成功"));
        } catch (Exception e) {
            log.error("切换FreeSwitch网关状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 测试网关连接
     */
    @PostMapping("/{id}/test")
    public ResponseEntity<JsonResult<Boolean>> testGatewayConnection(@PathVariable Long id) {
        try {
            boolean connected = gatewayService.testGatewayConnection(id);
            return ResponseEntity.ok(JsonResult.success(connected, 
                    connected ? "网关连接正常" : "网关连接失败"));
        } catch (Exception e) {
            log.error("测试FreeSwitch网关连接失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 重新注册网关
     */
    @PostMapping("/{id}/reregister")
    public ResponseEntity<JsonResult<Void>> reregisterGateway(
            @PathVariable Long id,
            @CurrentUser UserEntity currentUser) {
        
        try {
            gatewayService.reregisterGateway(id);
            return ResponseEntity.ok(JsonResult.success("网关重新注册请求已发送"));
        } catch (Exception e) {
            log.error("重新注册FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新网关状态
     */
    @PatchMapping("/{name}/status")
    public ResponseEntity<JsonResult<Void>> updateGatewayStatus(
            @PathVariable String name,
            @RequestParam String status) {
        
        try {
            gatewayService.updateGatewayStatus(name, status);
            return ResponseEntity.ok(JsonResult.success("网关状态更新成功"));
        } catch (Exception e) {
            log.error("更新FreeSwitch网关状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 更新网关注册状态
     */
    @PatchMapping("/{name}/registration")
    public ResponseEntity<JsonResult<Void>> updateRegistrationStatus(
            @PathVariable String name,
            @RequestParam Boolean registered) {
        
        try {
            gatewayService.updateRegistrationStatus(name, registered);
            return ResponseEntity.ok(JsonResult.success("网关注册状态更新成功"));
        } catch (Exception e) {
            log.error("更新FreeSwitch网关注册状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取启用的网关列表
     */
    @GetMapping("/enabled")
    public ResponseEntity<JsonResult<List<FreeSwitchGatewayDto>>> getEnabledGateways() {
        try {
            List<FreeSwitchGatewayEntity> entities = gatewayService.getEnabledGateways();
            List<FreeSwitchGatewayDto> dtos = entities.stream()
                    .map(dtoMapper::toGatewayDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取启用的FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据配置文件获取网关列表
     */
    @GetMapping("/profile/{profile}")
    public ResponseEntity<JsonResult<List<FreeSwitchGatewayDto>>> getGatewaysByProfile(@PathVariable String profile) {
        try {
            List<FreeSwitchGatewayEntity> entities = gatewayService.findByProfile(profile);
            List<FreeSwitchGatewayDto> dtos = entities.stream()
                    .map(dtoMapper::toGatewayDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("根据配置文件获取FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 根据状态获取网关列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<JsonResult<List<FreeSwitchGatewayDto>>> getGatewaysByStatus(@PathVariable String status) {
        try {
            List<FreeSwitchGatewayEntity> entities = gatewayService.findByStatus(status);
            List<FreeSwitchGatewayDto> dtos = entities.stream()
                    .map(dtoMapper::toGatewayDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("根据状态获取FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取网关统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<JsonResult<FreeSwitchGatewayService.GatewayStatistics>> getStatistics() {
        try {
            FreeSwitchGatewayService.GatewayStatistics statistics = gatewayService.getStatistics();
            return ResponseEntity.ok(JsonResult.success(statistics));
        } catch (Exception e) {
            log.error("获取FreeSwitch网关统计失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 获取需要注册的网关列表
     */
    @GetMapping("/needs-registration")
    public ResponseEntity<JsonResult<List<FreeSwitchGatewayDto>>> getGatewaysNeedingRegistration() {
        try {
            List<FreeSwitchGatewayEntity> entities = gatewayService.getGatewaysNeedingRegistration();
            List<FreeSwitchGatewayDto> dtos = entities.stream()
                    .map(dtoMapper::toGatewayDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos));
        } catch (Exception e) {
            log.error("获取需要注册的FreeSwitch网关失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 检查网关健康状态
     */
    @GetMapping("/health-check")
    public ResponseEntity<JsonResult<List<FreeSwitchGatewayDto>>> checkGatewayHealth() {
        try {
            List<FreeSwitchGatewayEntity> entities = gatewayService.checkGatewayHealth();
            List<FreeSwitchGatewayDto> dtos = entities.stream()
                    .map(dtoMapper::toGatewayDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(JsonResult.success(dtos, 
                    dtos.isEmpty() ? "所有网关状态正常" : "发现" + dtos.size() + "个可能有问题的网关"));
        } catch (Exception e) {
            log.error("检查FreeSwitch网关健康状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }

    /**
     * 批量更新网关状态
     */
    @PatchMapping("/batch-update-status")
    public ResponseEntity<JsonResult<Void>> batchUpdateGatewayStatus(
            @RequestParam List<String> gatewayNames,
            @RequestParam String status,
            @CurrentUser UserEntity currentUser) {
        
        try {
            gatewayService.batchUpdateGatewayStatus(gatewayNames, status);
            return ResponseEntity.ok(JsonResult.success("批量更新网关状态成功"));
        } catch (Exception e) {
            log.error("批量更新FreeSwitch网关状态失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }
}
