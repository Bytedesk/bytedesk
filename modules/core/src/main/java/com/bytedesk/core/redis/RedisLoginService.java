package com.bytedesk.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.RedisConsts;

import java.util.concurrent.TimeUnit;

// @Slf4j
@Service
@AllArgsConstructor
public class RedisLoginService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * cache validate code for 15min
     * 缓存验证码, 缓存时间15分钟
     *
     * @param keyNum email/mobile
     * @param code   code
     */
    public void cacheValidateCode(String keyNum, @NonNull String code) {
        String key = RedisConsts.VALIDATE_CODE + keyNum;
        stringRedisTemplate.opsForValue().set(key, code, 60 * 15, TimeUnit.SECONDS);
    }

    /**
     * validate code
     * 验证验证码是否正确
     *
     * @param keyNum email/mobile
     * @param code   code
     * @return boolean
     */
    public boolean validateCode(String keyNum, String code) {
        String key = RedisConsts.VALIDATE_CODE + keyNum;
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        if (code.equals(cacheCode)) {
            return true;
        }
        return false;
    }

    /**
     * check validate code
     * 是否缓存有验证码
     * 
     * @param keyNum
     * @return
     */
    public boolean hasValidateCode(String keyNum) {
        String key = RedisConsts.VALIDATE_CODE + keyNum;
        String cacheCode = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasLength(cacheCode)) {
            return false;
        }
        return true;
    }

    /**
     * cache ip for 60min
     * 缓存注册ip, 缓存时间60分钟
     * 不允许同一个ip短时间内重复注册
     *
     * @param ip ip
     */
    public void cacheRegisterIP(@NonNull String ip) {
        String key = RedisConsts.REGISTE_IP + ip;
        stringRedisTemplate.opsForValue().set(key, ip, 60 * 60, TimeUnit.SECONDS);
    }

    /**
     * check ip
     * 是否缓存有ip
     * 
     * @param ip
     * @return
     */
    public boolean hasRegisterIP(String ip) {
        String key = RedisConsts.REGISTE_IP + ip;
        String cacheIp = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasLength(cacheIp)) {
            return false;
        }
        return true;
    }

    /**
     * cache visitor ip
     * 限制访客端
     * 缓存注册ip, 缓存时间5分钟
     * 不允许同一个ip短时间内重复注册
     *
     * @param ip ip
     */
    public void cacheVisitorRegisterIP(@NonNull String ip) {
        String key = RedisConsts.REGISTE_IP_VISITOR + ip;
        stringRedisTemplate.opsForValue().set(key, ip, 60 * 5, TimeUnit.SECONDS);
    }

    /**
     * check visitor ip
     * 限制访客端
     * 是否缓存有ip
     * 
     * @param ip
     * @return
     */
    public boolean hasVisitorRegisterIP(String ip) {
        String key = RedisConsts.REGISTE_IP_VISITOR + ip;
        String cacheIp = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasLength(cacheIp)) {
            return false;
        }
        return true;
    }

}
