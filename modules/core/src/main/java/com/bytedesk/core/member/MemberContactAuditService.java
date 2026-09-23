/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: T0-3 存量联系方式镜像盘点/修复服务（docs/plans/2026-09-21-user-member-contact-sync-plan.md）
 *   - audit：只读盘点 Member 与关联 User 的 mobile/email/country 不一致清单（dry-run）
 *   - fix：按管理员确认的 memberUid 清单精准修复，仅覆盖 User 侧非空值，记录 before/after 审计日志
 */
package com.bytedesk.core.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.CountryCodeUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MemberContactAuditService {

    private final MemberRepository memberRepository;

    private final AuthService authService;

    /**
     * 盘点全部有效成员与其关联 User 的联系方式差异（只读，不修改任何数据）。
     */
    public List<MemberContactAuditItem> audit() {
        List<MemberEntity> members = memberRepository.findAllActiveWithUser();
        List<MemberContactAuditItem> items = new ArrayList<>();
        for (MemberEntity member : members) {
            UserEntity user = member.getUser();
            if (user == null) {
                continue;
            }
            if (!isInconsistent(member, user)) {
                continue;
            }
            items.add(toAuditItem(member, user));
        }
        log.info("member contact audit: total active members = {}, inconsistent = {}",
                members.size(), items.size());
        return items;
    }

    /**
     * 按确认清单修复：仅当 User 侧值非空时覆盖 Member 侧对应字段；User 侧为空的字段跳过（不清空 Member）。
     * 每条记录输出 before/after 审计日志并携带操作人。
     */
    @Transactional
    public List<MemberContactFixResult> fix(List<String> memberUids) {
        List<MemberContactFixResult> results = new ArrayList<>();
        if (memberUids == null || memberUids.isEmpty()) {
            return results;
        }
        String operatorUid = currentOperatorUid();
        for (String memberUid : memberUids) {
            results.add(fixOne(memberUid, operatorUid));
        }
        return results;
    }

    private MemberContactFixResult fixOne(String memberUid, String operatorUid) {
        Optional<MemberEntity> optional = memberRepository.findByUid(memberUid);
        if (optional.isEmpty()) {
            return MemberContactFixResult.builder()
                    .memberUid(memberUid)
                    .fixed(false)
                    .message("member not found")
                    .build();
        }
        MemberEntity member = optional.get();
        UserEntity user = member.getUser();
        if (user == null) {
            return MemberContactFixResult.builder()
                    .memberUid(memberUid)
                    .orgUid(member.getOrgUid())
                    .fixed(false)
                    .message("member has no associated user")
                    .build();
        }
        if (!isInconsistent(member, user)) {
            return MemberContactFixResult.builder()
                    .memberUid(memberUid)
                    .orgUid(member.getOrgUid())
                    .userUid(user.getUid())
                    .fixed(false)
                    .message("already consistent")
                    .build();
        }

        String beforeMobile = member.getMobile();
        String beforeEmail = member.getEmail();
        String beforeCountry = member.getCountry();
        List<String> skipped = new ArrayList<>();

        if (StringUtils.hasText(user.getMobile())) {
            member.setMobile(user.getMobile().trim());
        } else {
            skipped.add("user mobile empty, member mobile kept");
        }
        if (StringUtils.hasText(user.getEmail())) {
            member.setEmail(user.getEmail().trim());
        } else {
            skipped.add("user email empty, member email kept");
        }
        member.setCountry(CountryCodeUtils.normalize(user.getCountry()));

        memberRepository.save(member);
        log.info("member contact fix: memberUid={}, orgUid={}, userUid={}, operator={}, mobile {} -> {}, email {} -> {}, country {} -> {}, skipped={}",
                member.getUid(), member.getOrgUid(), user.getUid(), operatorUid,
                beforeMobile, member.getMobile(),
                beforeEmail, member.getEmail(),
                beforeCountry, member.getCountry(),
                skipped);

        return MemberContactFixResult.builder()
                .memberUid(member.getUid())
                .orgUid(member.getOrgUid())
                .userUid(user.getUid())
                .fixed(true)
                .beforeMobile(beforeMobile)
                .afterMobile(member.getMobile())
                .beforeEmail(beforeEmail)
                .afterEmail(member.getEmail())
                .beforeCountry(beforeCountry)
                .afterCountry(member.getCountry())
                .message(skipped.isEmpty() ? "fixed" : String.join("; ", skipped))
                .build();
    }

    private boolean isInconsistent(MemberEntity member, UserEntity user) {
        boolean mobileMismatch = !Objects.equals(trimToNull(member.getMobile()), trimToNull(user.getMobile()));
        boolean emailMismatch = !equalsIgnoreCaseSafe(trimToNull(member.getEmail()), trimToNull(user.getEmail()));
        boolean countryMismatch = !Objects.equals(
                CountryCodeUtils.normalize(member.getCountry()),
                CountryCodeUtils.normalize(user.getCountry()));
        return mobileMismatch || emailMismatch || countryMismatch;
    }

    private MemberContactAuditItem toAuditItem(MemberEntity member, UserEntity user) {
        return MemberContactAuditItem.builder()
                .memberUid(member.getUid())
                .orgUid(member.getOrgUid())
                .userUid(user.getUid())
                .memberMobile(member.getMobile())
                .userMobile(user.getMobile())
                .memberEmail(member.getEmail())
                .userEmail(user.getEmail())
                .memberCountry(member.getCountry())
                .userCountry(user.getCountry())
                .build();
    }

    private String currentOperatorUid() {
        try {
            UserEntity operator = authService.getUser();
            return operator != null ? operator.getUid() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private static boolean equalsIgnoreCaseSafe(String left, String right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.equalsIgnoreCase(right);
    }
}
