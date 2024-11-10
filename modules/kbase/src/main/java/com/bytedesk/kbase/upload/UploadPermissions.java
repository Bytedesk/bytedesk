/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:42:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

public class UploadPermissions {

    public static final String UPLOAD_PREFIX = "UPLOAD_";
    // Upload permissions
    public static final String UPLOAD_CREATE = "hasAuthority('UPLOAD_CREATE')";
    public static final String UPLOAD_READ = "hasAuthority('UPLOAD_READ')";
    public static final String UPLOAD_UPDATE = "hasAuthority('UPLOAD_UPDATE')";
    public static final String UPLOAD_DELETE = "hasAuthority('UPLOAD_DELETE')";
    public static final String UPLOAD_EXPORT = "hasAuthority('UPLOAD_EXPORT')";
}