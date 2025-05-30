package com.bytedesk.core.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class NetworkUtils {

    /**
     * 获取本地IP地址列表
     * 
     * @return List<String> IP地址列表
     */
    public static List<String> getLocalIPs() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                // 排除：虚拟网卡、loopback、未启用的网卡
                if (ni.isVirtual() || ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // 排除IPv6地址
                    if (addr.getHostAddress().contains(":")) {
                        continue;
                    }
                    // 排除loopback地址
                    if (!addr.isLoopbackAddress()) {
                        ipList.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            log.error("Failed to get local IP addresses", e);
        }
        return ipList;
    }

    /**
     * 获取第一个非本地回环IP地址
     * 
     * @return String IP地址
     */
    public static String getFirstNonLoopbackIP() {
        List<String> ips = getLocalIPs();
        return ips.isEmpty() ? "127.0.0.1" : ips.get(0);
    }
} 