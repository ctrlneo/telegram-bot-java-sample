package com.demo.telegram.bot.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IpUtil {

    /**
     * 获取客户端真实IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多级反向代理时，取第一个IP
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 检查IP是否在指定的IP段内
     */
    private static boolean isIpInRange(String ip, String cidr) {
        if (!cidr.contains("/")) {
            // 单个IP地址比较
            return ip.equals(cidr);
        }

        String[] parts = cidr.split("/");
        String network = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        long ipLong = ipToLong(ip);
        long networkLong = ipToLong(network);
        long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;

        return (ipLong & mask) == (networkLong & mask);
    }

    /**
     * 将IP地址转换为长整型
     */
    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result += Long.parseLong(parts[i]) << (8 * (3 - i));
        }
        return result & 0xFFFFFFFFL;
    }


    /**
     * 检查IP是否在允许的范围内
     */
    public static boolean isIpAllowed(String ip, List<String> allowedIps) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        for (String allowedIp : allowedIps) {
            if (isIpInRange(ip, allowedIp)) {
                return true;
            }
        }

        return false;
    }
}
