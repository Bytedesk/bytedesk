/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-02 13:30:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 12:25:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import com.bytedesk.core.utils.BaseRequest;

// import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MemberRequest extends BaseRequest {

    // private String uid;

    /**
     * job number
     * 工号
     */
    private String jobNo;

    /**
     * nickname
     * 姓名
     */
    // @NotEmpty
    private String nickname;

    private String password;

    /**
     * mobile
     * 手机号
     */
    // @NotEmpty
    private String mobile;

    private String email;

    /**
     * seat no
     * 工位
     */
    private String seatNo;

    /**
     * telephone
     * 电话-分机号
     */
    private String telephone;

    /**
     * is verified
     * 是否已验证
     */
    @Builder.Default
    private Boolean verified = false;

    /**
     * department did
     */
    private String depUid;

    // 
    private String orgUid;

}
