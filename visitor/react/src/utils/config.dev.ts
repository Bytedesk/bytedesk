/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2023-11-17 07:50:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-04 15:46:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
//
export const BASE_HOST = "localhost";
export const BASE_PORT = 9003;
export const BASE_URL = "http://" + BASE_HOST + ":" + BASE_PORT;
//
export const STOMP_WS_URL = "ws://" + BASE_HOST + ":" + BASE_PORT + "/stomp";
export const STOMP_SOCKJS_URL =
  "http://" + BASE_HOST + ":" + BASE_PORT + "/sockjs";
