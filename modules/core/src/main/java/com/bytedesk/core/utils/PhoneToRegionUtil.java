/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 11:29:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-04 12:37:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.util.Locale;

import com.alibaba.fastjson2.JSONObject;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/google/libphonenumber/
 * https://www.baeldung.com/java-libphonenumber
 * 基于google的libphonenumber将手机号转成地区及供应商信息
 */
@Slf4j
@UtilityClass
public class PhoneToRegionUtil {

    // 手机号工具
    private final static PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
    // 运营商
    private final static PhoneNumberToCarrierMapper CARRIER_MAPPER = PhoneNumberToCarrierMapper.getInstance();
    // 地址
    private final static PhoneNumberOfflineGeocoder GEO_CODER = PhoneNumberOfflineGeocoder.getInstance();

    /**
     * 验证当前手机号是否有效
     * @param phone 手机号
     * @return 校验结果
     */
    public static boolean isValidNumber(String phone){
        return PHONE_NUMBER_UTIL.isValidNumber(getPhoneNumber(phone));
    }

    /**
     * 获取手机号运营商
     * @param phone 手机号
     * @return 运营商
     */
    public static String getPhoneCarrier(String phone){
        return isValidNumber(phone) ?  CARRIER_MAPPER.getNameForNumber(getPhoneNumber(phone), Locale.CHINA) : "";
    }

    /**
     * 获取手机号归属地
     * @param phone 手机号
     * @return 归属地
     */
    public static String getPhoneRegin(String phone){
        return isValidNumber(phone) ? GEO_CODER.getDescriptionForNumber(getPhoneNumber(phone),Locale.CHINESE) : "";
    }

    /**
     * 生成PhoneNumber
     * @param phone 手机号
     * @return PhoneNumber
     */
    private static Phonenumber.PhoneNumber getPhoneNumber(String phone){
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(86);
        phoneNumber.setNationalNumber(Long.parseLong(phone));
        return phoneNumber;
    }

    public String formatPhoneNumber(String phoneNumber, String regionCode) {  
        try {  
            PhoneNumber number = PHONE_NUMBER_UTIL.parse(phoneNumber, regionCode);  
            return PHONE_NUMBER_UTIL.format(number, PhoneNumberFormat.E164); // E.164 格式  
        } catch (NumberParseException e) {  
            System.err.println("NumberParseException was thrown: " + e.toString());  
            return null;  
        }  
    }  
    
     /**
     * Get the phone number's attribution information: carrier, region
     * 
     * @param phone Phone number
     * @return Attribution information
     */
    public static JSONObject getPhoneAffiliationInfo(String phone){
        JSONObject affiliation = new JSONObject();
        affiliation.put("phone",phone);
        affiliation.put("carrier",getPhoneCarrier(phone));
        affiliation.put("region",getPhoneRegin(phone));
        return affiliation;
    }

}
