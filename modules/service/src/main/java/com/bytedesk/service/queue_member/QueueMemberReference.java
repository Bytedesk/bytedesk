package com.bytedesk.service.queue_member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Column;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 队列成员引用实体
 * 用于将队列成员与多个队列关联起来
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_queue_member_reference", 
       indexes = {
           @Index(name = "idx_queue_uid", columnList = "queue_uid"),
           @Index(name = "idx_member_uid", columnList = "member_uid"),
           @Index(name = "idx_thread_uid", columnList = "thread_uid")
       })
public class QueueMemberReference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "queue_uid", nullable = false)
    private String queueUid;
    
    @Column(name = "member_uid", nullable = false)
    private String memberUid;
    
    @Column(name = "thread_uid", nullable = false)
    private String threadUid;
}
