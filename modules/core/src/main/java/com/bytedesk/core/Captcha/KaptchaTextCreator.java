/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-08 23:57:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-09 10:41:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.Captcha;

// import java.util.Random;
// import com.google.code.kaptcha.text.impl.DefaultTextCreator;

// public class KaptchaTextCreator extends DefaultTextCreator {
//     private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

//     @Override
//     public String getText() {
//         Integer result = 0;
//         Random random = new Random();
//         int x = random.nextInt(10);
//         int y = random.nextInt(10);
//         StringBuilder suChinese = new StringBuilder();
//         int randomoperands = random.nextInt(3);
//         if (randomoperands == 0) {
//             result = x * y;
//             suChinese.append(CNUMBERS[x]);
//             suChinese.append("*");
//             suChinese.append(CNUMBERS[y]);
//         } else if (randomoperands == 1) {
//             if ((x != 0) && y % x == 0) {
//                 result = y / x;
//                 suChinese.append(CNUMBERS[y]);
//                 suChinese.append("/");
//                 suChinese.append(CNUMBERS[x]);
//             } else {
//                 result = x + y;
//                 suChinese.append(CNUMBERS[x]);
//                 suChinese.append("+");
//                 suChinese.append(CNUMBERS[y]);
//             }
//         } else {
//             if (x >= y) {
//                 result = x - y;
//                 suChinese.append(CNUMBERS[x]);
//                 suChinese.append("-");
//                 suChinese.append(CNUMBERS[y]);
//             } else {
//                 result = y - x;
//                 suChinese.append(CNUMBERS[y]);
//                 suChinese.append("-");
//                 suChinese.append(CNUMBERS[x]);
//             }
//         }
//         suChinese.append("=?@" + result);
//         return suChinese.toString();
//     }
// }