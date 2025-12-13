package com.bytedesk.ticket.comment;

// import java.util.List;

// import org.flowable.engine.TaskService;
// import org.flowable.engine.task.Comment;
// import org.flowable.task.api.Task;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class TicketCommentService {

//     private final TaskService taskService;

//     /**
//      * 添加工单评论
//      * ACT_HI_COMMENT - 历史评论表，存储所有的任务评论
//      */
//     public void addComment(String taskId, String message, String userId) {
//         log.info("add comment to task: {} by user: {}", taskId, userId);
//         // 获取任务所属的流程实例ID
//         Task task = taskService.createTaskQuery()
//             .taskId(taskId)
//             .singleResult();
        
//         if (task != null) {
//             // 添加评论，关联到任务和流程实例
//             Comment comment = taskService.addComment(taskId, task.getProcessInstanceId(), "ticket-comment", message);
//             comment.setUserId(userId);
//             taskService.saveComment(comment);
            
//             log.info("Added comment {} to task {} by user {}", comment.getId(), taskId, userId);
//         } else {
//             log.warn("Task not found: {}", taskId);
//         }
//     }

//     /**
//      * 获取任务评论列表
//      */
//     public List<Comment> getTaskComments(String taskId) {
//         return taskService.getTaskComments(taskId);
//     }

//     /**
//      * 获取任务特定类型的评论列表
//      */
//     public List<Comment> getTaskComments(String taskId, String type) {
//         return taskService.getTaskComments(taskId, type);
//     }

//     /**
//      * 获取流程实例所有评论
//      */
//     public List<Comment> getProcessInstanceComments(String processInstanceId) {
//         return taskService.getProcessInstanceComments(processInstanceId);
//     }

//     /**
//      * 获取流程实例特定类型的评论
//      */
//     public List<Comment> getProcessInstanceComments(String processInstanceId, String type) {
//         return taskService.getProcessInstanceComments(processInstanceId, type);
//     }

//     /**
//      * 删除评论
//      */
//     public void deleteComment(String commentId) {
//         taskService.deleteComment(commentId);
//         log.info("Deleted comment: {}", commentId);
//     }
// } 