package com.bytedesk.core.group.forum;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 论坛群 REST：帖子/评论/点赞。
 * 
 * 契约见规划 §六：分页参数 pageNumber/pageSize（上限 50）；
 * 点赞为 set 语义（liked boolean，非 toggle）；错误经全局异常映射，不作为成功 data 返回。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/group/post")
@RequiredArgsConstructor
@Tag(name = "论坛群", description = "论坛群帖子/评论/点赞")
public class GroupForumRestController {

    private final GroupForumService groupForumService;

    @Operation(summary = "发帖", description = "论坛群成员发送根帖（文本+图片），clientRequestUid 幂等")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> body) {
        String groupUid = str(body, "groupUid");
        String clientRequestUid = str(body, "clientRequestUid");
        String text = str(body, "text");
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) body.get("images");
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.createPost(groupUid, clientRequestUid, text, images)));
    }

    @Operation(summary = "帖子分页", description = "根帖分页 + 批量评论数/点赞数/likedByMe")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/query")
    public ResponseEntity<?> queryPosts(@RequestParam String groupUid,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.queryPosts(groupUid, pageNumber, pageSize)));
    }

    @Operation(summary = "帖子详情", description = "根帖详情（不含评论列表）")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/detail")
    public ResponseEntity<?> queryPostDetail(@RequestParam String groupUid, @RequestParam String postUid) {
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.queryPostDetail(groupUid, postUid)));
    }

    @Operation(summary = "评论分页", description = "根评论或指定父评论的回复（楼中楼）")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/comment/query")
    public ResponseEntity<?> queryComments(@RequestParam String groupUid, @RequestParam String postUid,
            @RequestParam(required = false) String parentCommentUid,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.queryComments(groupUid, postUid, parentCommentUid, pageNumber, pageSize)));
    }

    @Operation(summary = "评论", description = "根评论（parentCommentUid 空）或回复（非空须同帖），支持文本+图片")
    @ApiResponse(responseCode = "200", description = "创建成功")
    @PostMapping("/comment/create")
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> body) {
        String groupUid = str(body, "groupUid");
        String postUid = str(body, "postUid");
        String clientRequestUid = str(body, "clientRequestUid");
        String parentCommentUid = str(body, "parentCommentUid");
        String replyToUserUid = str(body, "replyToUserUid");
        String text = str(body, "text");
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) body.get("images");
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.createComment(groupUid, postUid, clientRequestUid,
                        parentCommentUid, replyToUserUid, text, images)));
    }

    @Operation(summary = "点赞设置", description = "set 语义：liked=true/false，重复设置不反转")
    @ApiResponse(responseCode = "200", description = "设置成功")
    @PostMapping("/like/set")
    public ResponseEntity<?> setLike(@RequestBody Map<String, Object> body) {
        String groupUid = str(body, "groupUid");
        String postUid = str(body, "postUid");
        boolean liked = Boolean.TRUE.equals(body.get("liked"));
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.setLike(groupUid, postUid, liked)));
    }

    @Operation(summary = "删帖", description = "作者或群管理员；软删保留占位")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody Map<String, Object> body) {
        String groupUid = str(body, "groupUid");
        String postUid = str(body, "postUid");
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.deletePost(groupUid, postUid)));
    }

    @Operation(summary = "删评论", description = "作者或群管理员；软删保留关系")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PostMapping("/comment/delete")
    public ResponseEntity<?> deleteComment(@RequestBody Map<String, Object> body) {
        String groupUid = str(body, "groupUid");
        String postUid = str(body, "postUid");
        String commentUid = str(body, "commentUid");
        return ResponseEntity.ok(JsonResult.success(
                groupForumService.deleteComment(groupUid, postUid, commentUid)));
    }

    private String str(Map<String, Object> body, String key) {
        Object v = body == null ? null : body.get(key);
        return v == null ? null : String.valueOf(v);
    }
}
