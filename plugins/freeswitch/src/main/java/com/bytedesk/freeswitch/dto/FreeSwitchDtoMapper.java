package com.bytedesk.freeswitch.dto;

import com.bytedesk.freeswitch.model.FreeSwitchUserEntity;
import com.bytedesk.freeswitch.model.FreeSwitchCdrEntity;
import com.bytedesk.freeswitch.model.FreeSwitchConferenceEntity;
import com.bytedesk.freeswitch.model.FreeSwitchGatewayEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * FreeSwitch DTO转换工具类
 */
@Component
public class FreeSwitchDtoMapper {

    /**
     * 用户实体转DTO
     */
    public FreeSwitchUserDto toUserDto(FreeSwitchUserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        FreeSwitchUserDto dto = new FreeSwitchUserDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * CDR实体转DTO
     */
    public FreeSwitchCdrDto toCdrDto(FreeSwitchCdrEntity entity) {
        if (entity == null) {
            return null;
        }
        
        FreeSwitchCdrDto dto = new FreeSwitchCdrDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * 会议室实体转DTO
     */
    public FreeSwitchConferenceDto toConferenceDto(FreeSwitchConferenceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        FreeSwitchConferenceDto dto = new FreeSwitchConferenceDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * 网关实体转DTO
     */
    public FreeSwitchGatewayDto toGatewayDto(FreeSwitchGatewayEntity entity) {
        if (entity == null) {
            return null;
        }
        
        FreeSwitchGatewayDto dto = new FreeSwitchGatewayDto();
        BeanUtils.copyProperties(entity, dto);
        // 不复制密码字段
        dto.setPassword(null);
        return dto;
    }

    /**
     * 创建用户请求转实体
     */
    public FreeSwitchUserEntity toUserEntity(CreateFreeSwitchUserRequest request) {
        if (request == null) {
            return null;
        }
        
        FreeSwitchUserEntity entity = new FreeSwitchUserEntity();
        BeanUtils.copyProperties(request, entity);
        return entity;
    }

    /**
     * 创建会议室请求转实体
     */
    public FreeSwitchConferenceEntity toConferenceEntity(CreateFreeSwitchConferenceRequest request) {
        if (request == null) {
            return null;
        }
        
        FreeSwitchConferenceEntity entity = new FreeSwitchConferenceEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setCurrentMembers(0); // 初始成员数为0
        return entity;
    }

    /**
     * 创建网关请求转实体
     */
    public FreeSwitchGatewayEntity toGatewayEntity(CreateFreeSwitchGatewayRequest request) {
        if (request == null) {
            return null;
        }
        
        FreeSwitchGatewayEntity entity = new FreeSwitchGatewayEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setStatus("CREATED"); // 初始状态
        return entity;
    }
}
