/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 13:28:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 10:45:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.lionsoul.ip2region.xdb.Searcher;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.DateUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md
 */
@Slf4j
@Service
@AllArgsConstructor
public class IpService {

    private final IpRepository ipRepository;

    private final ModelMapper modelMapper;

    private final Searcher searcher;

    private final UidUtils uidUtils;

    /**
     * location: "国家|区域|省份|城市|ISP"
     * location: "中国|0|湖北省|武汉市|联通"
     * 
     * @param ip
     * @return
     */
    public String getIpLocation(String ip) {
        // java.lang.Exception: invalid ip address `[0:0:0:0:0:0:0:1]` // replace
        // localhost with 127.0.0.1
        // 首先验证IP格式是否正确
        if (!IpUtils.isValidIp(ip)) {
            log.error("Invalid IP address format: " + ip);
            return "0|0|0|内网IP|内网IP";
        }
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("failed to search(%s): %s\n", ip, e);
        }
        return null;
    }

    public String getIpLocation(HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        return getIpLocation(ip);
    }

    // 封禁IP, TODO: cache区分org
    @CachePut(value = "ip", key = "#ipRequest.ip")
    public IpResponse blockIp(IpRequest ipRequest) {
        //
        Optional<IpEntity> ipOptional = findByOrgUid(ipRequest.getOrgUid());
        if (ipOptional.isPresent()) {
            // 更新
            if (StringUtils.hasText(ipRequest.getIp())) {
                ipOptional.get().getIps().add(ipRequest.getIp());
            }
            ipOptional.get().setIpRangeStart(ipRequest.getIpRangeStart());
            ipOptional.get().setIpRangeEnd(ipRequest.getIpRangeEnd());
            ipOptional.get().setType(IpTypeEnum.fromValue(ipRequest.getType()).name());
            ipOptional.get().setUntilDate(DateUtils.formatStringToDateTime(ipRequest.getUntilDate()));
            ipOptional.get().setReason(ipRequest.getReason());

            IpEntity savedIp = save(ipOptional.get());
            if (savedIp == null) {
                throw new RuntimeException("failed to update ip");
            }

            return convertToResponse(ipOptional.get());
        }
        //
        IpEntity ip = modelMapper.map(ipRequest, IpEntity.class);
        ip.setUid(uidUtils.getCacheSerialUid());
        if (StringUtils.hasText(ipRequest.getIp())) {
            ip.getIps().add(ipRequest.getIp());
        }
        ip.setUntilDate(DateUtils.formatStringToDateTime(ipRequest.getUntilDate()));
        //
        IpEntity savedIp = save(ip);
        if (savedIp == null) {
            throw new RuntimeException("failed to save ip");
        }
        //
        return convertToResponse(ip);
    }

    public IpResponse unblockIp(IpRequest ipRequest) {

        Optional<IpEntity> ipOptional = findByOrgUid(ipRequest.getOrgUid());
        if (ipOptional.isPresent()) {
            ipOptional.get().getIps().remove(ipRequest.getIp());

            IpEntity savedIp = save(ipOptional.get());
            if (savedIp == null) {
                throw new RuntimeException("failed to unblock ip");
            }

            return convertToResponse(ipOptional.get());
        }

        throw new RuntimeException("failed to unblock ip");
    }

    public Boolean isBlocked(HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        return isBlocked(ip, "");
    }

    // TODO: cache区分org
    @Cacheable(value = "ip", key = "#ip-#orgUid")
    public Boolean isBlocked(String ip, String orgUid) {
        // TODO: 暂时不验证
        return false;
    }

    // 将IPv4地址转换为长整型
    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        ByteBuffer buffer = ByteBuffer.wrap(octets);
        return buffer.getInt() & 0xFFFFFFFFL; // Ensure positive value
    }

    // 检查IP是否在指定的范围内
    public static boolean isIpInRange(InetAddress ip, InetAddress rangeStart, InetAddress rangeEnd) {
        long ipValue = ipToLong(ip);
        long rangeStartValue = ipToLong(rangeStart);
        long rangeEndValue = ipToLong(rangeEnd);

        return ipValue >= rangeStartValue && ipValue <= rangeEndValue;
    }

    public static boolean testIsIpInRange(String ip) {

        InetAddress ipToCheck;
        try {
            ipToCheck = InetAddress.getByName(ip);
            InetAddress rangeStart = InetAddress.getByName("192.168.1.1");
            InetAddress rangeEnd = InetAddress.getByName("192.168.1.254");

            if (isIpInRange(ipToCheck, rangeStart, rangeEnd)) {
                log.info("The IP is within the specified range.");
                return true;
            } else {
                log.info("The IP is not within the specified range.");
                return false;
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    // TODO: 昵称国际化：英语、中文、繁体、日文
    public String createVisitorNickname(HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        String location = getIpLocation(ip);
        // uidUtils.getCacheSerialUid(); // TODO: 修改昵称后缀数字为从1~递增
        String randomId = uidUtils.getUid(); //"[" + ip + "]"; 

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        // 0|0|0|内网IP|内网IP
        String[] locals = location.split("\\|");
        log.info("ip {} location {} locals {}", ip, location, (Object[]) locals); // Cast to Object[] to confirm the
        // non-varargs invocation
        if (locals.length > 2) {
            if (locals[2].equals("0")) {
                return "Local" + randomId;
            }
            return locals[2] + randomId;
        }

        return "Visitor";
    }

    Optional<IpEntity> findByUid(String uid) {
        return ipRepository.findByUid(uid);
    }

    Optional<IpEntity> findByOrgUid(String orgUid) {
        return ipRepository.findFirstByOrgUid(orgUid);
    }

    private IpEntity save(IpEntity ip) {
        try {
            return ipRepository.save(ip);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public IpResponse convertToResponse(IpEntity ip) {
        return modelMapper.map(ip, IpResponse.class);
    }

}
