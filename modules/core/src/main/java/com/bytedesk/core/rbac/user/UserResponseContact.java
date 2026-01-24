/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-22
 * @Description: Lightweight user contact info for admin review screens
 */
package com.bytedesk.core.rbac.user;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseContact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String username;
    private String nickname;
    private String avatar;

    private String email;
    private String mobile;
}
