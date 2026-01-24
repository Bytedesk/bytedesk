/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
@Slf4j
@RequiredArgsConstructor
public class CustomerExcelListener extends AnalysisEventListener<Map<Integer, String>> {

    private final CustomerRepository customerRepository;

    private final UidUtils uidUtils;

    private final String orgUid;

    private static final int BATCH_COUNT = 200;

    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private Integer nicknameIndex;
    private Integer emailIndex;
    private Integer mobileIndex;
    private Integer descriptionIndex;
    private Integer extraIndex;
    private Integer notesIndex;

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (headMap == null || headMap.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
            Integer index = entry.getKey();
            String header = normalizeHeader(entry.getValue());
            if (!StringUtils.hasText(header)) {
                continue;
            }

            if (nicknameIndex == null && isNicknameHeader(header)) {
                nicknameIndex = index;
                continue;
            }
            if (emailIndex == null && isEmailHeader(header)) {
                emailIndex = index;
                continue;
            }
            if (mobileIndex == null && isMobileHeader(header)) {
                mobileIndex = index;
                continue;
            }
            if (descriptionIndex == null && isDescriptionHeader(header)) {
                descriptionIndex = index;
                continue;
            }
            if (extraIndex == null && isExtraHeader(header)) {
                extraIndex = index;
                continue;
            }
            if (notesIndex == null && isNotesHeader(header)) {
                notesIndex = index;
                continue;
            }
        }

        log.info("CustomerExcelListener headMap: nicknameIndex={}, emailIndex={}, mobileIndex={}, descriptionIndex={}, extraIndex={}, notesIndex={} headMap={}",
                nicknameIndex, emailIndex, mobileIndex, descriptionIndex, extraIndex, notesIndex, headMap);
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        if (data == null || data.isEmpty()) {
            return;
        }
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveBatch();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveBatch();
        log.info("CustomerExcelListener: all data analysed and saved");
    }

    private void saveBatch() {
        if (cachedDataList.isEmpty()) {
            return;
        }

        List<CustomerEntity> entities = ListUtils.newArrayListWithExpectedSize(cachedDataList.size());
        for (Map<Integer, String> row : cachedDataList) {
            if (row == null || row.isEmpty()) {
                continue;
            }

            // 兜底规则：
            // - 不含 uid 列时：昵称/邮箱/手机号/描述/附加信息/备注 对应 0..5
            // - 含 uid 列时（导出文件）：uid 在 0，则其它列整体 +1
            String nickname = getCell(row, nicknameIndex, 0, 1);
            String email = getCell(row, emailIndex, 1, 2);
            String mobile = normalizeMobile(getCell(row, mobileIndex, 2, 3));
            String description = getCell(row, descriptionIndex, 3, 4);
            String extra = getCell(row, extraIndex, 4, 5);
            String notes = getCell(row, notesIndex, 5, 6);

            // 至少需要有一个可识别字段
            if (!StringUtils.hasText(nickname) && !StringUtils.hasText(mobile) && !StringUtils.hasText(email)) {
                continue;
            }

            // 兜底：极少数场景会把表头当作数据行
            if (isProbablyHeaderRow(nickname, email, mobile)) {
                continue;
            }

            CustomerEntity entity = new CustomerEntity();
            entity.setUid(uidUtils.getUid());
            entity.setOrgUid(orgUid);
            entity.setNickname(nickname);
            entity.setEmail(email);
            entity.setMobile(mobile);
            entity.setDescription(StringUtils.hasText(description) ? description : I18Consts.I18N_DESCRIPTION);
            entity.setExtra(StringUtils.hasText(extra) ? extra : BytedeskConsts.EMPTY_JSON_STRING);
            entity.setNotes(notes);
            entities.add(entity);
        }

        if (!entities.isEmpty()) {
            customerRepository.saveAll(entities);
            log.info("CustomerExcelListener: saved {} customers", entities.size());
        }
    }

    private String getCell(Map<Integer, String> row, Integer mappedIndex, int fallbackIndex, int fallbackIndex2) {
        String value = null;
        if (mappedIndex != null) {
            value = row.get(mappedIndex);
        }
        if (!StringUtils.hasText(value)) {
            value = row.get(fallbackIndex);
        }
        if (!StringUtils.hasText(value)) {
            value = row.get(fallbackIndex2);
        }
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeHeader(String header) {
        if (!StringUtils.hasText(header)) {
            return null;
        }
        return header.trim().replace(" ", "");
    }

    private boolean isNicknameHeader(String header) {
        return header.contains("昵称") || header.contains("姓名") || header.contains("客户名") || header.contains("客户昵称");
    }

    private boolean isEmailHeader(String header) {
        String lower = header.toLowerCase(Locale.ROOT);
        return header.contains("邮箱") || lower.contains("email");
    }

    private boolean isMobileHeader(String header) {
        String lower = header.toLowerCase(Locale.ROOT);
        return header.contains("手机号") || header.contains("手机号码") || header.contains("手机") || header.contains("电话") || header.contains("联系方式")
                || lower.contains("mobile") || lower.contains("phone");
    }

    private boolean isDescriptionHeader(String header) {
        return header.contains("描述") || header.contains("说明") || header.contains("简介");
    }

    private boolean isExtraHeader(String header) {
        String lower = header.toLowerCase(Locale.ROOT);
        return header.contains("附加信息") || header.contains("扩展") || header.contains("额外") || lower.contains("extra");
    }

    private boolean isNotesHeader(String header) {
        String lower = header.toLowerCase(Locale.ROOT);
        return header.contains("备注") || header.contains("注释") || lower.contains("notes");
    }

    private boolean isProbablyHeaderRow(String nickname, String email, String mobile) {
        return "昵称".equals(nickname)
                || "姓名".equals(nickname)
                || "邮箱".equals(email)
                || "手机号".equals(mobile)
                || "手机".equals(mobile);
    }

    private String normalizeMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return null;
        }
        String trimmed = mobile.trim();
        // Excel 数字单元格可能变成科学计数法，如 1.8888888003E10
        if (trimmed.contains("E") || trimmed.contains("e")) {
            try {
                return new BigDecimal(trimmed).toPlainString();
            } catch (Exception ignore) {
                return trimmed;
            }
        }
        // Excel 可能把整数显示成 18888888003.0
        if (trimmed.endsWith(".0")) {
            return trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }
}
