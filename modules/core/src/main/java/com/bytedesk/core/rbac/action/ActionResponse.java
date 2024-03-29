/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 14:03:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.action;

import com.bytedesk.core.utils.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对应权限Authority中操作：query/create/update/delete/import/export
 *
 * @author bytedesk.com on 2019-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionResponse extends BaseResponse {

    private String aid;

    private String name;

    private String value;

}
