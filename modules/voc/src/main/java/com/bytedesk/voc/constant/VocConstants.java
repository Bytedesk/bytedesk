package com.bytedesk.voc.constant;

public class VocConstants {
    
    // 反馈类型
    public static final String TYPE_BUG = "bug";
    public static final String TYPE_SUGGESTION = "suggestion";
    public static final String TYPE_COMPLAINT = "complaint";
    public static final String TYPE_FEATURE = "feature";
    public static final String TYPE_OTHER = "other";
    
    // 反馈状态
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_RESOLVED = "resolved";
    public static final String STATUS_CLOSED = "closed";
    
    // 权限
    public static final String ROLE_VOC_ADMIN = "ROLE_VOC_ADMIN";
    public static final String ROLE_VOC_USER = "ROLE_VOC_USER";
    
    // 缓存
    public static final String CACHE_VOC = "voc";
    public static final String CACHE_VOC_FEEDBACK = "voc_feedback";
    public static final String CACHE_VOC_REPLY = "voc_reply";
    
    // 通知
    public static final String NOTIFY_NEW_FEEDBACK = "new_feedback";
    public static final String NOTIFY_NEW_REPLY = "new_reply";
    public static final String NOTIFY_STATUS_CHANGED = "status_changed";
} 