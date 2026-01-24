/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-24
 * @Description: Phone number lookup service
 */
package com.bytedesk.core.phone;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class PhoneLookupService {

    private volatile PhoneNumberLookup lookup;

    public Optional<PhoneNumberInfo> lookup(String number) {
        return getLookup().lookup(number);
    }

    private PhoneNumberLookup getLookup() {
        PhoneNumberLookup local = lookup;
        if (local == null) {
            synchronized (this) {
                local = lookup;
                if (local == null) {
                    local = new PhoneNumberLookup();
                    lookup = local;
                }
            }
        }
        return local;
    }
}
