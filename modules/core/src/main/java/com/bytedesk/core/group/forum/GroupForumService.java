package com.bytedesk.core.group.forum;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.group.GroupRepository;
import com.bytedesk.core.group.GroupTypeEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.user.UserExtraJobTitleUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 论坛群服务：发帖/评论/点赞的权限校验、事务命令与分页聚合查询。
 * 
 * REST 与消息事件适配器共用本服务，禁止各自实现授权/幂等。
 * 所有命令校验：群存在、类型为 FORUM、操作者为群成员、群未禁言（禁言时仅管理员可发）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupForumService {

    private final GroupForumPostRepository postRepository;

    private final GroupForumCommentRepository commentRepository;

    private final GroupForumLikeRepository likeRepository;

    private final GroupRepository groupRepository;

    private final com.bytedesk.core.message.MessageRestService messageRestService;

    private final com.bytedesk.core.message.MessagePersistService messagePersistService;

    private final ThreadRestService threadRestService;

    private final IMessageSendService messageSendService;

    private final AuthService authService;

    private final MemberRepository memberRepository;

    private final UidUtils uidUtils;

    // ==================== 命令：发帖 ====================

    /**
     * 发帖：创建 POST 消息 + 帖子关系，同一事务提交后广播。
     * clientRequestUid 幂等：同一操作者同一请求 UID 重复提交返回首次结果。
     */
    @Transactional
    public Map<String, Object> createPost(String groupUid, String clientRequestUid, String text,
            List<String> imageUrls) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        requireCanPost(group, user);

        List<String> safeImages = sanitizeImages(imageUrls);
        if (!StringUtils.hasText(text) && safeImages.isEmpty()) {
            throw new RuntimeException("post content required");
        }
        // 幂等：同 clientRequestUid 且同作者已存在的帖子直接返回；跨作者请求 UID 冲突拒绝
        Optional<GroupForumPostEntity> existing = postRepository.findByMessageUid(clientRequestUid);
        if (existing.isPresent()) {
            if (!user.getUid().equals(existing.get().getAuthorUserUid())
                    || !groupUid.equals(existing.get().getGroupUid())) {
                throw new RuntimeException("duplicate clientRequestUid");
            }
            return toPostDetail(existing.get());
        }
        //
        String topic = TopicUtils.getOrgGroupTopic(groupUid);
        ThreadEntity thread = threadRestService.findFirstByTopic(topic)
                .orElseThrow(() -> new RuntimeException("group thread not found"));
        //
        Map<String, Object> content = new HashMap<>();
        content.put("schemaVersion", 1);
        content.put("text", text == null ? "" : text);
        content.put("images", safeImages);
        // 纯图帖摘要占位
        String titleSource = StringUtils.hasText(text) ? text : "[图片]";
        MessageExtra extra = MessageExtra.fromOrgUid(group.getOrgUid());
        //
        String messageUid = StringUtils.hasText(clientRequestUid) ? clientRequestUid : uidUtils.getUid();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(MessageTypeEnum.POST)
                .content(com.alibaba.fastjson2.JSON.toJSONString(content))
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(ZonedDateTime.now())
                .channel(ChannelEnum.WEB)
                .thread(thread.toProtobuf())
                .user(toUserProtobuf(user, group.getOrgUid()))
                .extra(extra.toJson())
                .build();
        // 关系与消息同事务：正文即时落库（不走延迟持久化，保证评论/详情立即可查），提交后广播
        GroupForumPostEntity post = GroupForumPostEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(group.getOrgUid())
                .userUid(user.getUid())
                .groupUid(groupUid)
                .messageUid(messageUid)
                .authorUserUid(user.getUid())
                .title(buildTitle(titleSource))
                .build();
        postRepository.save(post);
        messagePersistService.persist(message.toJson());
        messageSendService.sendProtobufMessage(message);
        log.info("forum post created: postUid: {}, groupUid: {}, messageUid: {}",
                post.getUid(), groupUid, messageUid);
        return toPostDetail(post);
    }

    // ==================== 命令：评论 ====================

    /**
     * 评论：parentCommentUid 可空（根评论）；非空时必须同帖同群。
     */
    @Transactional
    public Map<String, Object> createComment(String groupUid, String postUid, String clientRequestUid,
            String parentCommentUid, String replyToUserUid, String text, java.util.List<String> images) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        requireCanPost(group, user);

        List<String> safeImages = sanitizeImages(images);
        // 文本与图片至少其一
        if (!StringUtils.hasText(text) && safeImages.isEmpty()) {
            throw new RuntimeException("comment content required");
        }
        GroupForumPostEntity post = postRepository.findByUid(postUid)
                .orElseThrow(() -> new RuntimeException("post not found"));
        if (!groupUid.equals(post.getGroupUid())) {
            throw new RuntimeException("post not in group");
        }
        if (Boolean.TRUE.equals(post.isDeleted())) {
            throw new RuntimeException("post deleted");
        }
        // 父评论校验：存在、同帖、有效
        if (StringUtils.hasText(parentCommentUid)) {
            GroupForumCommentEntity parent = commentRepository.findByUid(parentCommentUid)
                    .orElseThrow(() -> new RuntimeException("parent comment not found"));
            if (!postUid.equals(parent.getPostUid())) {
                throw new RuntimeException("parent comment not in same post");
            }
            if (Boolean.TRUE.equals(parent.isDeleted())) {
                throw new RuntimeException("parent comment deleted");
            }
        }
        // 幂等：同 clientRequestUid 且同作者同帖返回已有评论；跨作者/跨帖请求 UID 冲突拒绝
        Optional<GroupForumCommentEntity> existing = commentRepository.findByMessageUid(clientRequestUid);
        if (existing.isPresent()) {
            if (!user.getUid().equals(existing.get().getAuthorUserUid())
                    || !postUid.equals(existing.get().getPostUid())) {
                throw new RuntimeException("duplicate clientRequestUid");
            }
            return toCommentSummary(existing.get());
        }
        //
        String topic = TopicUtils.getOrgGroupTopic(groupUid);
        ThreadEntity thread = threadRestService.findFirstByTopic(topic)
                .orElseThrow(() -> new RuntimeException("group thread not found"));
        //
        Map<String, Object> content = new HashMap<>();
        content.put("schemaVersion", 1);
        content.put("text", text);
        content.put("images", safeImages);
        content.put("postUid", postUid);
        if (StringUtils.hasText(parentCommentUid)) {
            content.put("parentCommentUid", parentCommentUid);
        }
        MessageExtra extra = MessageExtra.fromOrgUid(group.getOrgUid());
        //
        String messageUid = StringUtils.hasText(clientRequestUid) ? clientRequestUid : uidUtils.getUid();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid(messageUid)
                .type(MessageTypeEnum.POST_COMMENT)
                .content(com.alibaba.fastjson2.JSON.toJSONString(content))
                .status(MessageStatusEnum.SUCCESS)
                .createdAt(ZonedDateTime.now())
                .channel(ChannelEnum.WEB)
                .thread(thread.toProtobuf())
                .user(toUserProtobuf(user, group.getOrgUid()))
                .extra(extra.toJson())
                .build();
        //
        GroupForumCommentEntity comment = GroupForumCommentEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(group.getOrgUid())
                .userUid(user.getUid())
                .groupUid(groupUid)
                .postUid(postUid)
                .postMessageUid(post.getMessageUid())
                .messageUid(messageUid)
                .parentCommentUid(StringUtils.hasText(parentCommentUid) ? parentCommentUid : null)
                .replyToUserUid(StringUtils.hasText(replyToUserUid) ? replyToUserUid : null)
                .authorUserUid(user.getUid())
                .authorNickname(user.getNickname())
                .authorAvatar(user.getAvatar())
                .build();
        commentRepository.save(comment);
        // 正文即时落库（同事务）：避免联查时消息仍在延迟持久化队列导致首条评论无内容
        messagePersistService.persist(message.toJson());
        messageSendService.sendProtobufMessage(message);
        log.info("forum comment created: commentUid: {}, postUid: {}, parent: {}",
                comment.getUid(), postUid, parentCommentUid);
        return toCommentSummary(comment);
    }

    // ==================== 命令：点赞（set 语义，非 toggle） ====================

    /**
     * 设置点赞状态：liked=true 建立关系（已存在则无操作），liked=false 删除关系。
     * 重复 set 同值幂等，不会反转。
     */
    @Transactional
    public Map<String, Object> setLike(String groupUid, String postUid, boolean liked) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        // 点赞仅要求群成员（禁言不影响点赞）
        requireGroupMember(group, user);
        GroupForumPostEntity post = postRepository.findByUid(postUid)
                .orElseThrow(() -> new RuntimeException("post not found"));
        if (!groupUid.equals(post.getGroupUid())) {
            throw new RuntimeException("post not in group");
        }
        if (Boolean.TRUE.equals(post.isDeleted())) {
            throw new RuntimeException("post deleted");
        }
        //
        Optional<GroupForumLikeEntity> existing = likeRepository.findByPostUidAndUserUid(postUid, user.getUid());
        if (liked) {
            if (existing.isEmpty()) {
                try {
                    likeRepository.saveAndFlush(GroupForumLikeEntity.builder()
                            .uid(uidUtils.getUid())
                            .orgUid(group.getOrgUid())
                            .userUid(user.getUid())
                            .groupUid(groupUid)
                            .postUid(postUid)
                            .build());
                } catch (DataIntegrityViolationException race) {
                    // 并发重复插入：唯一约束兜底，按已点赞处理
                }
            }
        } else {
            existing.ifPresent(likeRepository::delete);
        }
        long likeCount = likeRepository.countLikesByPostUids(group.getOrgUid(), List.of(postUid))
                .stream().findFirst().map(r -> ((Number) r[1]).longValue()).orElse(0L);
        return Map.of("postUid", postUid, "likeCount", likeCount, "likedByMe", liked);
    }

    // ==================== 命令：删除（作者/管理员，软删保留占位） ====================

    @Transactional
    public Map<String, Object> deletePost(String groupUid, String postUid) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        GroupForumPostEntity post = postRepository.findByUid(postUid)
                .orElseThrow(() -> new RuntimeException("post not found"));
        if (!groupUid.equals(post.getGroupUid())) {
            throw new RuntimeException("post not in group");
        }
        boolean isAuthor = post.getAuthorUserUid().equals(user.getUid());
        if (!isAuthor && !isGroupAdmin(group, user)) {
            throw new RuntimeException("no permission to delete post");
        }
        post.setDeleted(true);
        postRepository.save(post);
        return Map.of("postUid", postUid, "deleted", true);
    }

    @Transactional
    public Map<String, Object> deleteComment(String groupUid, String postUid, String commentUid) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        GroupForumCommentEntity comment = commentRepository.findByUid(commentUid)
                .orElseThrow(() -> new RuntimeException("comment not found"));
        if (!postUid.equals(comment.getPostUid()) || !groupUid.equals(comment.getGroupUid())) {
            throw new RuntimeException("comment not in group/post");
        }
        boolean isAuthor = comment.getAuthorUserUid().equals(user.getUid());
        if (!isAuthor && !isGroupAdmin(group, user)) {
            throw new RuntimeException("no permission to delete comment");
        }
        comment.setDeleted(true);
        commentRepository.save(comment);
        return Map.of("commentUid", commentUid, "deleted", true);
    }

    // ==================== 查询 ====================

    /**
     * 帖子分页：批量聚合评论数/点赞数/likedByMe，不含无界评论。
     */
    public Map<String, Object> queryPosts(String groupUid, int pageNumber, int pageSize) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        requireGroupMember(group, user);
        //
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber), Math.min(Math.max(1, pageSize), 50));
        Page<GroupForumPostEntity> page = postRepository
                .findByOrgUidAndGroupUidOrderByCreatedAtDesc(group.getOrgUid(), groupUid, pageable);
        List<GroupForumPostEntity> posts = page.getContent();
        List<String> postUids = posts.stream().map(post -> post.getUid()).collect(Collectors.toList());
        //
        Map<String, Long> commentCounts = new HashMap<>();
        if (!postUids.isEmpty()) {
            postRepository.countCommentsByPostUids(group.getOrgUid(), postUids)
                    .forEach(r -> commentCounts.put((String) r[0], ((Number) r[1]).longValue()));
        }
        Map<String, Long> likeCounts = new HashMap<>();
        if (!postUids.isEmpty()) {
            likeRepository.countLikesByPostUids(group.getOrgUid(), postUids)
                    .forEach(r -> likeCounts.put((String) r[0], ((Number) r[1]).longValue()));
        }
        Set<String> likedByMe = postUids.isEmpty() ? Set.of()
                : new HashSet<>(likeRepository.findLikedPostUids(group.getOrgUid(), user.getUid(), postUids));
        //
        List<Map<String, Object>> items = posts.stream().map(post -> {
            Map<String, Object> item = toPostSummary(post);
            item.put("commentCount", commentCounts.getOrDefault(post.getUid(), 0L));
            item.put("likeCount", likeCounts.getOrDefault(post.getUid(), 0L));
            item.put("likedByMe", likedByMe.contains(post.getUid()));
            return item;
        }).collect(Collectors.toList());
        //
        return Map.of(
                "content", items,
                "pageNumber", page.getNumber(),
                "pageSize", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages());
    }

    /**
     * 帖子详情（不含评论列表，评论走 comment/query 分页接口）。
     */
    public Map<String, Object> queryPostDetail(String groupUid, String postUid) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        requireGroupMember(group, user);
        GroupForumPostEntity post = postRepository.findByUid(postUid)
                .orElseThrow(() -> new RuntimeException("post not found"));
        if (!groupUid.equals(post.getGroupUid())) {
            throw new RuntimeException("post not found");
        }
        Map<String, Object> detail = toPostDetail(post);
        detail.put("likeCount", likeRepository.countLikesByPostUids(group.getOrgUid(), List.of(postUid))
                .stream().findFirst().map(r -> ((Number) r[1]).longValue()).orElse(0L));
        detail.put("likedByMe", likeRepository.existsByPostUidAndUserUid(postUid, user.getUid()));
        return detail;
    }

    /**
     * 评论分页：parentCommentUid 空=根评论，非空=该评论的回复（楼中楼）。
     */
    public Map<String, Object> queryComments(String groupUid, String postUid, String parentCommentUid,
            int pageNumber, int pageSize) {
        UserEntity user = requireLogin();
        GroupEntity group = requireForumGroup(groupUid);
        requireGroupMember(group, user);
        GroupForumPostEntity post = postRepository.findByUid(postUid)
                .orElseThrow(() -> new RuntimeException("post not found"));
        if (!groupUid.equals(post.getGroupUid())) {
            throw new RuntimeException("post not found");
        }
        //
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber), Math.min(Math.max(1, pageSize), 50));
        Page<GroupForumCommentEntity> page;
        if (StringUtils.hasText(parentCommentUid)) {
            page = commentRepository
                    .findByOrgUidAndParentCommentUidAndDeletedFalseOrderByCreatedAtAsc(
                            group.getOrgUid(), parentCommentUid, pageable);
        } else {
            page = commentRepository
                    .findByOrgUidAndPostUidAndParentCommentUidIsNullAndDeletedFalseOrderByCreatedAtAsc(
                            group.getOrgUid(), postUid, pageable);
        }
        List<Map<String, Object>> items = page.getContent().stream()
                .map(this::toCommentSummary)
                .collect(Collectors.toList());
        return Map.of(
                "content", items,
                "parentCommentUid", parentCommentUid == null ? "" : parentCommentUid,
                "pageNumber", page.getNumber(),
                "pageSize", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages());
    }

    // ==================== 私有工具 ====================

    private UserEntity requireLogin() {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException("login required");
        }
        return user;
    }

    /** 群存在、未删除、类型为 FORUM（统一 not found，不泄露存在性） */
    private GroupEntity requireForumGroup(String groupUid) {
        GroupEntity group = groupRepository.findByUid(groupUid)
                .orElseThrow(() -> new RuntimeException("group not found"));
        if (!GroupTypeEnum.FORUM.name().equals(group.getType())) {
            throw new RuntimeException("group not found");
        }
        return group;
    }

    private void requireGroupMember(GroupEntity group, UserEntity user) {
        boolean isMember = group.getMembers().stream()
                .anyMatch(m -> m != null && m.getUser() != null && user.getUid().equals(m.getUser().getUid()));
        boolean isAdmin = isGroupAdmin(group, user);
        if (!isMember && !isAdmin) {
            throw new RuntimeException("group not found");
        }
    }

    /** 禁言时仅管理员可发帖/评论；点赞不受限 */
    private void requireCanPost(GroupEntity group, UserEntity user) {
        requireGroupMember(group, user);
        if (Boolean.TRUE.equals(group.getMuteAll()) && !isGroupAdmin(group, user)) {
            throw new RuntimeException("group muted");
        }
    }

    private boolean isGroupAdmin(GroupEntity group, UserEntity user) {
        if (group.getCreator() != null && user.getUid().equals(group.getCreator().getUid())) {
            return true;
        }
        return group.getAdmins().stream().anyMatch(a -> a != null && user.getUid().equals(a.getUid()));
    }

    /** 消息作者快照：携带发送者在群组织内的成员职务（user.extra.jobTitle），发帖/评论共用 */
    private UserProtobuf toUserProtobuf(UserEntity user, String orgUid) {
        String jobTitle = "";
        Optional<MemberEntity> memberOpt = memberRepository
                .findByUser_UidAndOrgUidAndDeletedFalse(user.getUid(), orgUid);
        if (memberOpt.isPresent() && StringUtils.hasText(memberOpt.get().getJobTitle())) {
            jobTitle = memberOpt.get().getJobTitle();
        }
        return UserProtobuf.builder()
                .uid(user.getUid())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .extra(UserExtraJobTitleUtils.putJobTitle(null, jobTitle))
                .build();
    }

    /**
     * 图片 URL 列表规范化：去空、去重、上限 9 张。
     */
    private List<String> sanitizeImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .filter(StringUtils::hasText)
                .map(image -> image.trim())
                .distinct()
                .limit(9)
                .collect(Collectors.toList());
    }

    private String buildTitle(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        String trimmed = text.trim();
        return trimmed.length() > 20 ? trimmed.substring(0, 20) : trimmed;
    }

    private Map<String, Object> toPostSummary(GroupForumPostEntity post) {
        Map<String, Object> item = new HashMap<>();
        item.put("postUid", post.getUid());
        item.put("groupUid", post.getGroupUid());
        item.put("messageUid", post.getMessageUid());
        item.put("authorUserUid", post.getAuthorUserUid());
        item.put("title", post.getTitle() == null ? "" : post.getTitle());
        item.put("createdAt", post.getCreatedAt() == null ? "" : post.getCreatedAt().toString());
        item.put("deleted", Boolean.TRUE.equals(post.isDeleted()));
        // 正文真值取消息表
        messageRestService.findByUid(post.getMessageUid()).ifPresent(msg -> {
            item.put("content", msg.getContent());
            item.put("authorNickname", extractNickname(msg.getUser()));
            item.put("authorAvatar", extractAvatar(msg.getUser()));
            item.put("authorJobTitle", extractJobTitle(msg.getUser()));
        });
        return item;
    }

    private Map<String, Object> toPostDetail(GroupForumPostEntity post) {
        Map<String, Object> item = toPostSummary(post);
        item.put("commentCount", 0L);
        item.put("likeCount", 0L);
        item.put("likedByMe", false);
        return item;
    }

    private Map<String, Object> toCommentSummary(GroupForumCommentEntity comment) {
        Map<String, Object> item = new HashMap<>();
        item.put("commentUid", comment.getUid());
        item.put("postUid", comment.getPostUid());
        item.put("messageUid", comment.getMessageUid());
        item.put("parentCommentUid", comment.getParentCommentUid() == null ? "" : comment.getParentCommentUid());
        item.put("replyToUserUid", comment.getReplyToUserUid() == null ? "" : comment.getReplyToUserUid());
        item.put("authorUserUid", comment.getAuthorUserUid());
        item.put("authorNickname", comment.getAuthorNickname() == null ? "" : comment.getAuthorNickname());
        item.put("authorAvatar", comment.getAuthorAvatar() == null ? "" : comment.getAuthorAvatar());
        item.put("createdAt", comment.getCreatedAt() == null ? "" : comment.getCreatedAt().toString());
        item.put("deleted", Boolean.TRUE.equals(comment.isDeleted()));
        // 正文真值取消息表；职务优先取关联消息作者快照（user.extra.jobTitle）
        messageRestService.findByUid(comment.getMessageUid()).ifPresent(msg -> {
            item.put("content", msg.getContent());
            if (!StringUtils.hasText((String) item.get("authorNickname"))) {
                item.put("authorNickname", extractNickname(msg.getUser()));
            }
            if (!StringUtils.hasText((String) item.get("authorAvatar"))) {
                item.put("authorAvatar", extractAvatar(msg.getUser()));
            }
            item.put("authorJobTitle", extractJobTitle(msg.getUser()));
            if (StringUtils.hasText(comment.getReplyToUserUid())) {
                item.put("replyToNickname", comment.getReplyToUserUid());
            }
        });
        return item;
    }

    /** 从消息 user JSON 提取昵称（失败回退 uid） */
    private String extractNickname(String userJson) {
        try {
            UserProtobuf user = UserProtobuf.fromJson(userJson);
            if (user != null && StringUtils.hasText(user.getNickname())) {
                return user.getNickname();
            }
        } catch (Exception ignore) {
        }
        return "";
    }

    /** 从消息 user JSON 提取头像 */
    private String extractAvatar(String userJson) {
        try {
            UserProtobuf user = UserProtobuf.fromJson(userJson);
            return user == null ? "" : (user.getAvatar() == null ? "" : user.getAvatar());
        } catch (Exception ignore) {
            return "";
        }
    }

    /** 从消息 user JSON 的 extra 中提取作者职务快照；无快照/解析失败返回空串 */
    private String extractJobTitle(String userJson) {
        try {
            UserProtobuf user = UserProtobuf.fromJson(userJson);
            return user == null ? "" : UserExtraJobTitleUtils.getJobTitle(user.getExtra());
        } catch (Exception ignore) {
            return "";
        }
    }
}
