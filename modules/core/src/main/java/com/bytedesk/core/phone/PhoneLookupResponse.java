/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-24
 * @Description: External phone number lookup response
 */
package com.bytedesk.core.phone;

public record PhoneLookupResponse(
        String number,
        String province,
        String city,
        String zipCode,
        String areaCode,
        String isp,
        String ispCnName
) {

    public static PhoneLookupResponse from(PhoneNumberInfo info) {
        return new PhoneLookupResponse(
                info.getNumber(),
                info.getAttribution() != null ? info.getAttribution().getProvince() : null,
                info.getAttribution() != null ? info.getAttribution().getCity() : null,
                info.getAttribution() != null ? info.getAttribution().getZipCode() : null,
                info.getAttribution() != null ? info.getAttribution().getAreaCode() : null,
                info.getIsp() != null ? info.getIsp().name() : null,
                info.getIsp() != null ? info.getIsp().getCnName() : null
        );
    }

    /**
     * 号码未在数据库中查到时的默认响应（如固话/座机号段）
     */
    public static PhoneLookupResponse notFound(String number) {
        return new PhoneLookupResponse(
                number, null, null, null, null,
                PhoneISPEnum.UNKNOWN.name(),
                PhoneISPEnum.UNKNOWN.getCnName()
        );
    }
}
