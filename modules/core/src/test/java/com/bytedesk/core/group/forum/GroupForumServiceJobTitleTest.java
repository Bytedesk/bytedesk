package com.bytedesk.core.group.forum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.group.GroupRepository;
import com.bytedesk.core.group.enums.GroupTypeEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessagePersistService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserExtraJobTitleUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;

/**
 * 论坛职务快照：发帖/评论创建时消息作者 extra 携带成员职务，
 * 摘要从关联消息读取 authorJobTitle；无快照/缺关联消息返回空串。
 */
class GroupForumServiceJobTitleTest {

    private GroupForumPostRepository postRepository;
    private GroupForumCommentRepository commentRepository;
    private GroupForumLikeRepository likeRepository;
    private GroupRepository groupRepository;
    private MessageRestService messageRestService;
    private MessagePersistService messagePersistService;
    private ThreadRestService threadRestService;
    private IMessageSendService messageSendService;
    private AuthService authService;
    private MemberRepository memberRepository;
    private UidUtils uidUtils;
    private GroupForumService service;
    private ApplicationContext originalContext;

    private UserEntity sender;
    private GroupEntity group;
    private ThreadEntity thread;

    @BeforeEach
    void setUp() {
        originalContext = ApplicationContextHolder.getApplicationContext();
        // ThreadEntity.toProtobuf 经 ModelMapper 转换，需要 Spring 上下文；测试内以 mock 提供真实 ModelMapper
        ApplicationContext mockContext = mock(ApplicationContext.class);
        when(mockContext.getBean(ModelMapper.class)).thenReturn(new ModelMapper());
        new ApplicationContextHolder().setApplicationContext(mockContext);
        postRepository = mock(GroupForumPostRepository.class);
        commentRepository = mock(GroupForumCommentRepository.class);
        likeRepository = mock(GroupForumLikeRepository.class);
        groupRepository = mock(GroupRepository.class);
        messageRestService = mock(MessageRestService.class);
        messagePersistService = mock(MessagePersistService.class);
        threadRestService = mock(ThreadRestService.class);
        messageSendService = mock(IMessageSendService.class);
        authService = mock(AuthService.class);
        memberRepository = mock(MemberRepository.class);
        uidUtils = mock(UidUtils.class);

        service = new GroupForumService(postRepository, commentRepository, likeRepository, groupRepository,
                messageRestService, messagePersistService, threadRestService, messageSendService, authService,
                memberRepository, uidUtils);

        sender = UserEntity.builder().uid("user-1").username("user-1").nickname("作者").build();
        MemberEntity senderMember = MemberEntity.builder()
                .uid("member-1").orgUid("org-1").user(sender).build();
        group = GroupEntity.builder().uid("group-1").orgUid("org-1").type(GroupTypeEnum.FORUM.name())
                .members(List.of(senderMember)).build();
        thread = ThreadEntity.builder().uid("thread-1").orgUid("org-1").build();

        when(authService.getUser()).thenReturn(sender);
        when(groupRepository.findByUid("group-1")).thenReturn(Optional.of(group));
        lenient().when(threadRestService.findFirstByTopic(anyString())).thenReturn(Optional.of(thread));
        lenient().when(postRepository.findByMessageUid(any())).thenReturn(Optional.empty());
        lenient().when(commentRepository.findByMessageUid(any())).thenReturn(Optional.empty());
        when(uidUtils.getUid()).thenReturn("uid-1", "uid-2", "uid-3", "uid-4");
    }

    @AfterEach
    void tearDown() {
        new ApplicationContextHolder().setApplicationContext(originalContext);
    }

    @Test
    void createPostShouldCarryJobTitleSnapshotOnAuthor() {
        MemberEntity member = MemberEntity.builder()
                .uid("member-1").orgUid("org-1").user(sender).jobTitle("产品经理").build();
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-1"))
                .thenReturn(Optional.of(member));
        // 创建响应的摘要从消息表回读
        when(messageRestService.findByUid(anyString())).thenAnswer(invocation -> {
            String uid = invocation.getArgument(0);
            return Optional.of(MessageEntity.builder().uid(uid).content("{}").thread(thread)
                    .user("{\"uid\":\"user-1\",\"nickname\":\"作者\",\"extra\":\"{\\\"jobTitle\\\":\\\"产品经理\\\"}\"}")
                    .orgUid("org-1").build());
        });

        Map<String, Object> result = service.createPost("group-1", "req-1", "帖子正文", List.of());

        // 创建响应含职务摘要
        assertThat(result.get("authorJobTitle")).isEqualTo("产品经理");
        // 持久化消息作者 extra 携带快照（JSON 内层引号已转义）
        Mockito.verify(messagePersistService).persist(Mockito.argThat(json -> json != null
                && json.contains("jobTitle\\\":\\\"产品经理")));
    }

    @Test
    void createPostShouldOmitJobTitleWhenMemberHasNone() {
        MemberEntity member = MemberEntity.builder()
                .uid("member-1").orgUid("org-1").user(sender).build();
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-1"))
                .thenReturn(Optional.of(member));
        when(messageRestService.findByUid(anyString())).thenAnswer(invocation -> {
            String uid = invocation.getArgument(0);
            return Optional.of(MessageEntity.builder().uid(uid).content("{}").thread(thread)
                    .user("{\"uid\":\"user-1\",\"nickname\":\"作者\",\"extra\":\"{}\"}")
                    .orgUid("org-1").build());
        });

        Map<String, Object> result = service.createPost("group-1", "req-2", "帖子正文", List.of());

        assertThat(result.get("authorJobTitle")).isEqualTo("");
    }

    @Test
    void postSummaryShouldReadJobTitleFromRelatedMessage() {
        GroupForumPostEntity post = GroupForumPostEntity.builder()
                .uid("post-1").orgUid("org-1").groupUid("group-1").messageUid("msg-1")
                .authorUserUid("user-1").title("t").build();
        when(postRepository.findByUid("post-1")).thenReturn(Optional.of(post));
        MessageEntity message = MessageEntity.builder().uid("msg-1").content("{}").thread(thread)
                .user("{\"uid\":\"user-1\",\"nickname\":\"作者\",\"extra\":\"{\\\"jobTitle\\\":\\\"架构师\\\"}\"}")
                .orgUid("org-1").build();
        when(messageRestService.findByUid("msg-1")).thenReturn(Optional.of(message));

        Map<String, Object> summary = service.queryPostDetail("group-1", "post-1");
        assertThat(summary.get("authorJobTitle")).isEqualTo("架构师");
        assertThat(summary.get("authorNickname")).isEqualTo("作者");
    }

    @Test
    void commentSummaryShouldReturnEmptyJobTitleForLegacyMessageWithoutSnapshot() {
        GroupForumPostEntity post = GroupForumPostEntity.builder()
                .uid("post-1").orgUid("org-1").groupUid("group-1").messageUid("msg-post")
                .authorUserUid("user-1").title("t").build();
        when(postRepository.findByUid("post-1")).thenReturn(Optional.of(post));

        GroupForumCommentEntity comment = GroupForumCommentEntity.builder()
                .uid("comment-1").orgUid("org-1").postUid("post-1").messageUid("msg-c1")
                .authorUserUid("user-1").authorNickname("作者").build();
        // 旧消息：user JSON 无 extra/无 jobTitle 键
        MessageEntity legacyMessage = MessageEntity.builder().uid("msg-c1").content("{}").thread(thread)
                .user("{\"uid\":\"user-1\",\"nickname\":\"作者\"}").orgUid("org-1").build();
        when(commentRepository.findByOrgUidAndPostUidAndParentCommentUidIsNullAndDeletedFalseOrderByCreatedAtAsc(
                eq("org-1"), eq("post-1"), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(
                        List.of(comment)));
        when(messageRestService.findByUid("msg-c1")).thenReturn(Optional.of(legacyMessage));

        Map<String, Object> page = service.queryComments("group-1", "post-1", null, 0, 20);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) page.get("content");
        assertThat(content.get(0).get("authorJobTitle")).isEqualTo("");
    }

    @Test
    void commentSummaryShouldNotFailWhenRelatedMessageMissing() {
        GroupForumPostEntity post = GroupForumPostEntity.builder()
                .uid("post-1").orgUid("org-1").groupUid("group-1").messageUid("msg-post")
                .authorUserUid("user-1").title("t").build();
        when(postRepository.findByUid("post-1")).thenReturn(Optional.of(post));

        GroupForumCommentEntity comment = GroupForumCommentEntity.builder()
                .uid("comment-1").orgUid("org-1").postUid("post-1").messageUid("msg-missing")
                .authorUserUid("user-1").authorNickname("作者").build();
        when(commentRepository.findByOrgUidAndPostUidAndParentCommentUidIsNullAndDeletedFalseOrderByCreatedAtAsc(
                eq("org-1"), eq("post-1"), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(
                        List.of(comment)));
        when(messageRestService.findByUid("msg-missing")).thenReturn(Optional.empty());

        Map<String, Object> page = service.queryComments("group-1", "post-1", null, 0, 20);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) page.get("content");
        assertThat(content.get(0).get("authorNickname")).isEqualTo("作者");
    }

    @Test
    void postSummaryShouldSurviveEscapedQuotesInJobTitle() {
        // 论坛消息 extra JSON 序列化往返：转义引号不丢失
        MessageEntity message = MessageEntity.builder().uid("msg-2").content("{}").thread(thread)
                .user("{\"uid\":\"user-1\",\"extra\":" + com.alibaba.fastjson2.JSON.toJSONString(
                        Map.of("jobTitle", "高级\"工程师\"")) + "}")
                .orgUid("org-1").build();
        GroupForumPostEntity post = GroupForumPostEntity.builder()
                .uid("post-2").orgUid("org-1").groupUid("group-1").messageUid("msg-2")
                .authorUserUid("user-1").title("t").build();
        when(postRepository.findByUid("post-2")).thenReturn(Optional.of(post));
        when(messageRestService.findByUid("msg-2")).thenReturn(Optional.of(message));

        Map<String, Object> summary = service.queryPostDetail("group-1", "post-2");
        String jobTitle = (String) summary.get("authorJobTitle");
        assertThat(UserExtraJobTitleUtils.getJobTitle(
                UserExtraJobTitleUtils.putJobTitle(null, jobTitle))).isEqualTo("高级\"工程师\"");
    }

    @Test
    void createCommentShouldCarryJobTitleSnapshot() {
        GroupForumPostEntity post = GroupForumPostEntity.builder()
                .uid("post-1").orgUid("org-1").groupUid("group-1").messageUid("msg-post")
                .authorUserUid("user-1").title("t").build();
        when(postRepository.findByUid("post-1")).thenReturn(Optional.of(post));
        MemberEntity member = MemberEntity.builder()
                .uid("member-1").orgUid("org-1").user(sender).jobTitle("运营专员").build();
        when(memberRepository.findByUser_UidAndOrgUidAndDeletedFalse("user-1", "org-1"))
                .thenReturn(Optional.of(member));
        // 评论摘要读取关联消息：创建路径消息已含快照
        lenient().when(messageRestService.findByUid(anyString())).thenAnswer(invocation -> {
            String uid = invocation.getArgument(0);
            return Optional.of(MessageEntity.builder().uid(uid).content("{}").thread(thread)
                    .user("{\"uid\":\"user-1\",\"nickname\":\"作者\",\"extra\":\"{\\\"jobTitle\\\":\\\"运营专员\\\"}\"}")
                    .orgUid("org-1").build());
        });

        Map<String, Object> result = service.createComment("group-1", "post-1", "req-3", null, null, "评论", List.of());

        assertThat(result.get("authorJobTitle")).isEqualTo("运营专员");
        Mockito.verify(messagePersistService).persist(Mockito.argThat(json -> json != null
                && json.contains("jobTitle\\\":\\\"运营专员")));
    }
}
