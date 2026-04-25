package com.bytedesk.core.topic_subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
// import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class TopicSubscriptionRestServiceTest {

    @Mock
    private TopicSubscriptionRepository topicSubscriptionRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

//     @Mock
//     private PermissionService permissionService;

    @Mock
    private MemberRepository memberRepository;

    private TopicSubscriptionRestService topicSubscriptionRestService;

    @BeforeEach
    void setUp() {
        topicSubscriptionRestService = new TopicSubscriptionRestService(
                topicSubscriptionRepository,
                modelMapper,
                uidUtils,
                authService,
                userRepository,
                memberRepository
                // permissionService
        );
    }

    @Test
    void convertToResponseFallsBackToMemberProfileWhenUserUidIsMemberUid() {
        TopicSubscriptionEntity entity = TopicSubscriptionEntity.builder()
                .topic("org/member/1892222777688240/df_mb_uid_internal")
                .type(TopicSubscriptionTypeEnum.CHAT.name())
                .build();
        entity.setUserUid("1892222777688241");

        TopicSubscriptionResponse mapped = TopicSubscriptionResponse.builder()
                .uid("sub-1")
                .userUid("1892222777688241")
                .build();

        UserEntity memberUser = new UserEntity();
        memberUser.setUid("user-1");
        memberUser.setUsername("alice");

        MemberEntity member = MemberEntity.builder()
                .nickname("Alice")
                .avatar("https://img.test/member.png")
                .build();
        member.setUid("1892222777688241");
        member.setUser(memberUser);

        when(modelMapper.map(entity, TopicSubscriptionResponse.class)).thenReturn(mapped);
        when(userRepository.findByUid("1892222777688241")).thenReturn(Optional.empty());
        when(memberRepository.findByUid("1892222777688241")).thenReturn(Optional.of(member));

        TopicSubscriptionResponse response = topicSubscriptionRestService.convertToResponse(entity);

        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getUserNickname()).isEqualTo("Alice");
        assertThat(response.getUserAvatar()).isEqualTo("https://img.test/member.png");
    }

        @Test
        void queryByUserFallsBackToCurrentUserOrgUidWhenRequestMissingOrgUid() {
                OrganizationEntity organization = OrganizationEntity.builder()
                        .uid("org-1")
                        .build();

                UserEntity user = new UserEntity();
                user.setUid("user-1");
                user.setCurrentOrganization(organization);

                TopicSubscriptionRequest request = new TopicSubscriptionRequest();

                when(authService.getUser()).thenReturn(user);
                when(topicSubscriptionRepository.findAll(anySpecification(), anyPageable()))
                                .thenReturn(Page.empty());

                topicSubscriptionRestService.queryByUser(request);

                assertThat(request.getUserUid()).isEqualTo("user-1");
                assertThat(request.getOrgUid()).isEqualTo("org-1");
                verify(topicSubscriptionRepository).findAll(anySpecification(), anyPageable());
        }

        private static Specification<TopicSubscriptionEntity> anySpecification() {
                return any();
        }

        private static Pageable anyPageable() {
                return any();
        }
}