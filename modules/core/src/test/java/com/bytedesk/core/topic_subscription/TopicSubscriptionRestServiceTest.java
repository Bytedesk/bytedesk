package com.bytedesk.core.topic_subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
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

    @Test
    void isSubscribedUsesRequestedTypeWhenProvided() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");

        TopicSubscriptionRequest request = TopicSubscriptionRequest.builder()
                .topic("org/workgroup/wg-1/visitor-1")
                .type(TopicSubscriptionTypeEnum.MONITOR.name())
                .build();

        TopicSubscriptionEntity subscription = TopicSubscriptionEntity.builder()
                .topic(request.getTopic())
                .type(TopicSubscriptionTypeEnum.MONITOR.name())
                .build();
        ReflectionTestUtils.setField(subscription, "userUid", "user-1");

        when(authService.getUser()).thenReturn(user);
        when(topicSubscriptionRepository.findByUserUidAndTopic("user-1", request.getTopic()))
                .thenReturn(java.util.List.of(subscription));

        Boolean subscribed = topicSubscriptionRestService.isSubscribed(request);

        assertThat(subscribed).isTrue();
    }

    @Test
    void subscribeUsesRequestedTypeWhenProvided() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");

        TopicSubscriptionRequest request = TopicSubscriptionRequest.builder()
                .topic("org/workgroup/wg-1/visitor-1")
                .type(TopicSubscriptionTypeEnum.INSERT.name())
                .build();

        TopicSubscriptionEntity saved = TopicSubscriptionEntity.builder()
                .topic(request.getTopic())
                .type(TopicSubscriptionTypeEnum.INSERT.name())
                .build();
        saved.setUid("sub-1");
        ReflectionTestUtils.setField(saved, "userUid", "user-1");

        TopicSubscriptionEntity mappedEntity = TopicSubscriptionEntity.builder()
                .topic(request.getTopic())
                .type(TopicSubscriptionTypeEnum.INSERT.name())
                .build();

        TopicSubscriptionResponse mapped = TopicSubscriptionResponse.builder()
                .uid("sub-1")
                .topic(request.getTopic())
                .type(TopicSubscriptionTypeEnum.INSERT.name())
                .userUid("user-1")
                .build();

        when(authService.getUser()).thenReturn(user);
        when(uidUtils.getUid()).thenReturn("sub-1");
        when(topicSubscriptionRepository.findByUserUidAndTopic("user-1", request.getTopic()))
                .thenReturn(java.util.List.of());
        when(modelMapper.map(request, TopicSubscriptionEntity.class)).thenReturn(mappedEntity);
        when(topicSubscriptionRepository.save(argThat(entity -> TopicSubscriptionTypeEnum.INSERT.name().equals(entity.getType()))))
                .thenReturn(saved);
        when(modelMapper.map(saved, TopicSubscriptionResponse.class)).thenReturn(mapped);

        TopicSubscriptionResponse response = topicSubscriptionRestService.subscribe(request);

        assertThat(response.getType()).isEqualTo(TopicSubscriptionTypeEnum.INSERT.name());
    }

        private static Specification<TopicSubscriptionEntity> anySpecification() {
                return any();
        }

        private static Pageable anyPageable() {
                return any();
        }
}