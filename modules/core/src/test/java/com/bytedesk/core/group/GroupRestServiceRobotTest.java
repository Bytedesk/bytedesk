package com.bytedesk.core.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;

/**
 * 群机器人管理单测：权限校验、fail-closed 校验、输入规范化、容量校验。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GroupRestServiceRobotTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private ObjectProvider<GroupRobotValidator> validatorProvider;

    @Mock
    private GroupRobotValidator validator;

    private GroupRestService groupRestService;

    private UserEntity owner;
    private UserEntity otherUser;
    private GroupEntity group;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        groupRestService = new GroupRestService(
                groupRepository,
                authService,
                modelMapper,
                uidUtils,
                null, // memberRestService（本测试未用到）
                threadRestService,
                validatorProvider);
        //
        owner = UserEntity.builder().uid("owner-1").username("owner").build();
        otherUser = UserEntity.builder().uid("user-2").username("other").build();
        group = GroupEntity.builder().build();
        group.setUid("group-1");
        group.setOrgUid("org-1");
        group.setCreator(owner);
        lenient().when(authService.getUser()).thenReturn(owner);
        lenient().when(groupRepository.findByUid("group-1")).thenReturn(Optional.of(group));
        lenient().when(groupRepository.save(any(GroupEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        lenient().when(validatorProvider.getIfAvailable()).thenReturn(validator);
        lenient().when(validator.findInvalidRobotUids(any(), anyString(), anyString()))
                .thenReturn(List.of());
    }

    private GroupRequest request(String... robotUids) {
        GroupRequest req = new GroupRequest();
        req.setUid("group-1");
        req.setRobotUids(new java.util.ArrayList<>(List.of(robotUids)));
        return req;
    }

    @Test
    void addRobotsShouldRejectNonAdmin() {
        when(authService.getUser()).thenReturn(otherUser);
        assertThatThrownBy(() -> groupRestService.addRobots(request("r-1")))
                .isInstanceOf(Exception.class);
        verify(groupRepository, never()).save(any(GroupEntity.class));
    }

    @Test
    void addRobotsShouldRejectWhenValidatorMissing() {
        when(validatorProvider.getIfAvailable()).thenReturn(null);
        assertThatThrownBy(() -> groupRestService.addRobots(request("r-1")))
                .hasMessageContaining("not available");
    }

    @Test
    void addRobotsShouldRejectInvalidRobots() {
        when(validator.findInvalidRobotUids(List.of("r-bad"), "org-1", "owner-1"))
                .thenReturn(List.of("r-bad"));
        assertThatThrownBy(() -> groupRestService.addRobots(request("r-bad")))
                .hasMessageContaining("r-bad");
    }

    @Test
    void addRobotsShouldNormalizeAndDedupe() {
        GroupResponse response = groupRestService.addRobots(request(" r-1 ", "r-1", "r-2"));
        assertThat(group.getRobotUids()).containsExactly("r-1", "r-2");
        assertThat(response.getRobotUids()).containsExactly("r-1", "r-2");
    }

    @Test
    void addRobotsShouldRejectIllegalUidCharacters() {
        // CSV 存储：逗号/特殊字符注入必须拒绝
        assertThatThrownBy(() -> groupRestService.addRobots(request("r,1")))
                .hasMessageContaining("Invalid robot uid");
        assertThatThrownBy(() -> groupRestService.addRobots(request("r 1")))
                .hasMessageContaining("Invalid robot uid");
    }

    @Test
    void addRobotsShouldRejectEmptyList() {
        assertThatThrownBy(() -> groupRestService.addRobots(request()))
                .hasMessageContaining("No robots");
        assertThatThrownBy(() -> groupRestService.addRobots(request(" ", "")))
                .hasMessageContaining("No robots");
    }

    @Test
    void addRobotsShouldEnforceCapacity() {
        // 2048 列宽：16 个 128 字符 uid + 分隔符即超限（单个 uid 均合法）
        String longUid = "r".repeat(128);
        String[] uids = new String[20];
        for (int i = 0; i < 20; i++) {
            uids[i] = longUid + i; // 129 字符 -> 超过单 uid 上限则此构造无效
        }
        // 用 16 个恰好 128 字符的 uid：128*16 + 15 分隔符 = 2063 > 2048
        String[] exactUids = new String[16];
        for (int i = 0; i < 16; i++) {
            exactUids[i] = String.format("r%03d", i) + "x".repeat(124); // 4+124=128 字符
        }
        assertThatThrownBy(() -> groupRestService.addRobots(request(exactUids)))
                .hasMessageContaining("capacity");
    }

    @Test
    void removeRobotsShouldBeIdempotent() {
        group.addRobot("r-1");
        // 移除不存在的机器人不报错
        GroupResponse response = groupRestService.removeRobots(request("r-1", "r-not-exist"));
        assertThat(response.getRobotUids()).isEmpty();
    }

    @Test
    void removeRobotsShouldRejectNonAdmin() {
        when(authService.getUser()).thenReturn(otherUser);
        assertThatThrownBy(() -> groupRestService.removeRobots(request("r-1")))
                .isInstanceOf(Exception.class);
    }

    @Test
    void queryRobotUidsShouldReturnEmptyListWhenNull() {
        group.setRobotUids(null);
        List<String> result = groupRestService.queryRobotUids("group-1");
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void adminMemberShouldBeAbleToAdd() {
        // 群主不在 admins 列表但为 creator，可操作（requireGroupAdmin 校验 creator）
        GroupResponse response = groupRestService.addRobots(request("r-1"));
        assertThat(group.getRobotUids()).containsExactly("r-1");
        assertThat(response.getRobotUids()).containsExactly("r-1");
        verify(groupRepository).save(any(GroupEntity.class));
    }
}
