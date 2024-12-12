/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 17:59:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-05 10:20:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

// import org.modelmapper.ModelMapper;
// import org.springframework.security.core.context.SecurityContextHolder;

// import com.bytedesk.core.rbac.user.UserEntity;
// import com.bytedesk.core.rbac.user.UserDetailsImpl;

// public class AuthUser {

//     public static UserEntity getCurrentUser() {
//         // not logged in
//         if (SecurityContextHolder.getContext().getAuthentication() == null) {
//             return null;
//         }
//         //
//         try {
//             UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
//                     .getPrincipal();
//             return new ModelMapper().map(userDetails, UserEntity.class);
//         } catch (Exception e) {
//             // TODO: handle exception
//             // FIXME: 验证码错误时会报错：java.lang.ClassCastException: class java.lang.String cannot be cast to
//             // class com.bytedesk.core.rbac.user.UserDetailsImpl (java.lang.String is in
//             // module java.base of loader 'bootstrap';
//             // com.bytedesk.core.rbac.user.UserDetailsImpl is in unnamed module of loader 'app')
//             e.printStackTrace();
//         }
//         // 
//         return null;
//     }
// }
