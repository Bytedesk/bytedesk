/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-31 12:53:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-04 16:18:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import com.bytedesk.core.constant.AvatarConsts;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
// @Entity
// @Table(name = "core_base_user")
// @Inheritance()
// @DiscriminatorColumn(name = "user_type")
@MappedSuperclass
public abstract class BaseUser extends BaseEntity {
    
    private String nickname;

    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

    
}
