/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: T0-3 联系方式镜像盘点/修复服务单测（docs/plans/2026-09-21-user-member-contact-sync-plan.md）
 */
package com.bytedesk.core.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

class MemberContactAuditServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final AuthService authService = mock(AuthService.class);

    private MemberContactAuditService createService() {
        return new MemberContactAuditService(memberRepository, authService);
    }

    private MemberEntity member(String uid, String orgUid, UserEntity user,
            String mobile, String email, String country) {
        MemberEntity member = MemberEntity.builder()
                .uid(uid)
                .orgUid(orgUid)
                .mobile(mobile)
                .email(email)
                .country(country)
                .build();
        member.setUser(user);
        return member;
    }

    @Test
    void auditShouldListOnlyInconsistentMembers() {
        MemberContactAuditService service = createService();

        UserEntity superUser = UserEntity.builder()
                .uid("df_user_super").mobile("13311156272").email("admin@email.com").country("86").build();
        UserEntity normalUser = UserEntity.builder()
                .uid("user-2").mobile("13800138000").email("b@email.com").country("86").build();

        // 超管的陈旧成员记录（本次故障场景）：Member 仍为旧号 13345678000
        MemberEntity stale = member("member-super", "df_org_uid", superUser,
                "13345678000", "admin@email.com", "86");
        // 一致的成员
        MemberEntity consistent = member("member-2", "org-1", normalUser,
                "13800138000", "b@email.com", "86");

        when(memberRepository.findAllActiveWithUser()).thenReturn(List.of(stale, consistent));

        List<MemberContactAuditItem> items = service.audit();

        assertEquals(1, items.size());
        assertEquals("member-super", items.get(0).getMemberUid());
        assertEquals("13345678000", items.get(0).getMemberMobile());
        assertEquals("13311156272", items.get(0).getUserMobile());
    }

    @Test
    void fixShouldCopyNonBlankUserValuesAndKeepMemberValueWhenUserBlank() {
        MemberContactAuditService service = createService();

        UserEntity user = UserEntity.builder()
                .uid("user-1").mobile("13311156272").email(null).country("86").build();
        // Member 手机号陈旧 + 邮箱有组织侧值但 User 侧为空 → 邮箱保留
        MemberEntity stale = member("member-1", "df_org_uid", user,
                "13345678000", "org-side@email.com", null);

        when(memberRepository.findByUid("member-1")).thenReturn(Optional.of(stale));
        when(authService.getUser()).thenReturn(user);

        List<MemberContactFixResult> results = service.fix(List.of("member-1"));

        assertEquals(1, results.size());
        MemberContactFixResult result = results.get(0);
        assertTrue(result.getFixed());
        assertEquals("13345678000", result.getBeforeMobile());
        assertEquals("13311156272", result.getAfterMobile());
        // User 侧邮箱为空：不覆盖组织侧邮箱
        assertEquals("org-side@email.com", stale.getEmail());
        assertEquals("86", stale.getCountry());
        verify(memberRepository).save(stale);
    }

    @Test
    void fixShouldReportMissingMemberWithoutChanges() {
        MemberContactAuditService service = createService();
        when(memberRepository.findByUid("missing")).thenReturn(Optional.empty());

        List<MemberContactFixResult> results = service.fix(List.of("missing"));

        assertEquals(1, results.size());
        assertFalse(results.get(0).getFixed());
        assertEquals("member not found", results.get(0).getMessage());
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    void fixShouldSkipAlreadyConsistentMember() {
        MemberContactAuditService service = createService();

        UserEntity user = UserEntity.builder()
                .uid("user-1").mobile("13800138000").email("b@email.com").country("86").build();
        MemberEntity consistent = member("member-1", "org-1", user,
                "13800138000", "b@email.com", "86");

        when(memberRepository.findByUid("member-1")).thenReturn(Optional.of(consistent));

        List<MemberContactFixResult> results = service.fix(List.of("member-1"));

        assertEquals(1, results.size());
        assertFalse(results.get(0).getFixed());
        assertEquals("already consistent", results.get(0).getMessage());
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }
}
