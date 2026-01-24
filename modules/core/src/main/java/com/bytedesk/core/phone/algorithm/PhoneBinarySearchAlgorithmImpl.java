package com.bytedesk.core.phone.algorithm;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.bytedesk.core.phone.PhoneAttribution;
import com.bytedesk.core.phone.PhoneISPEnum;
import com.bytedesk.core.phone.PhoneNumberInfo;

/**
 * 二分查找算子
 *
 * @author xq.h
 * 2019/10/19 00:12
 **/
@Slf4j
public class PhoneBinarySearchAlgorithmImpl implements PhoneLookupAlgorithm {
    private ByteBuffer originalByteBuffer;
    private int indicesStartOffset;
    private int indicesEndOffset;

    @Override
    public void loadData(byte[] data) {
        originalByteBuffer = ByteBuffer.wrap(data)
                .asReadOnlyBuffer()
                .order(ByteOrder.LITTLE_ENDIAN);
        // advance position by 4 bytes (dataVersion not used)
        originalByteBuffer.getInt();
        indicesStartOffset = originalByteBuffer.getInt(4);
        indicesEndOffset = originalByteBuffer.capacity();
    }

    /**
     * 对齐
     */
    private int alignPosition(int pos) {
        int remain = (pos - indicesStartOffset) % 9;
        if (pos - indicesStartOffset < 9) {
            return pos - remain;
        } else if (remain != 0) {
            return pos + 9 - remain;
        } else {
            return pos;
        }
    }

    private boolean isInvalidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            log.debug("phone number is null");
            return true;
        }
        int phoneNumberLength = phoneNumber.length();
        if (phoneNumberLength < 7 || phoneNumberLength > 11) {
            log.debug("phone number {} is not acceptable, length invalid, length should be 11 or 7(for left 7 numbers), actual: {}",
                    phoneNumber, phoneNumberLength);
            return true;
        }
        return false;
    }

    @Override
    public Optional<PhoneNumberInfo> lookup(String phoneNumber) {
        log.trace("try to resolve attribution of phone number: {}", phoneNumber);
        ByteBuffer byteBuffer = originalByteBuffer.asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
        if (isInvalidPhoneNumber(phoneNumber)) {
            return Optional.empty();
        }
        int attributionIdentity;
        try {
            attributionIdentity = Integer.parseInt(phoneNumber.substring(0, 7));
        } catch (NumberFormatException e) {
            log.debug("phone number {} is invalid, is it numeric?", phoneNumber);
            return Optional.empty();
        }
        int left = indicesStartOffset;
        int right = indicesEndOffset;
        int mid = (left + right) / 2;
        mid = alignPosition(mid);
        while (mid >= left && mid <= right) {
            if (mid == right) {
                return Optional.empty();
            }
            int compare = compare(mid, attributionIdentity, byteBuffer);
            if (compare == 0) {
                return extract(phoneNumber, mid, byteBuffer);
            } else if (mid == left) {
                return Optional.empty();
            } else if (compare > 0) {
                int tempMid = (mid + left) / 2;
                right = mid;
                mid = alignPosition(tempMid);
            } else {
                int tempMid = (mid + right) / 2;
                left = mid;
                mid = alignPosition(tempMid);
            }
        }
        return Optional.empty();
    }

    private Optional<PhoneNumberInfo> extract(String phoneNumber, int indexStart, ByteBuffer byteBuffer) {
        byteBuffer.position(indexStart);
        // skip prefix
        byteBuffer.getInt();
        int infoStartIndex = byteBuffer.getInt();
        byte ispMark = byteBuffer.get();
        PhoneISPEnum isp = PhoneISPEnum.of(ispMark).orElse(PhoneISPEnum.UNKNOWN);

        byte[] bytes = new byte[determineInfoLength(infoStartIndex, byteBuffer)];
        byteBuffer.get(bytes);
        String oriString = new String(bytes, StandardCharsets.UTF_8);
        PhoneAttribution attribution = parse(oriString);

        return Optional.of(new PhoneNumberInfo(phoneNumber, attribution, isp));
    }

    private int determineInfoLength(int infoStartIndex, ByteBuffer byteBuffer) {
        byteBuffer.position(infoStartIndex);
        //noinspection StatementWithEmptyBody
        while ((byteBuffer.get()) != 0) {
            // just to find index of next '\0'
        }
        int infoEnd = byteBuffer.position() - 1;
        byteBuffer.position(infoStartIndex); //reset to info start index
        return infoEnd - infoStartIndex;
    }

    private PhoneAttribution parse(String ori) {
        String[] split = ori.split("\\|");
        if (split.length < 4) {
            throw new IllegalStateException("content format error");
        }
        return PhoneAttribution.builder()
                .province(split[0])
                .city(split[1])
                .zipCode(split[2])
                .areaCode(split[3])
                .build();
    }

    private int compare(int position, int key, ByteBuffer byteBuffer) {
        byteBuffer.position(position);
        int phonePrefix = byteBuffer.getInt();
        return Integer.compare(phonePrefix, key);
    }
}
