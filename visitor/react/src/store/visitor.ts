/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 15:25:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 15:39:53
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
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface VisitorState {
    visitor: VISITOR.Visitor;
}

export const useVisitorStore = create<VisitorState>()(
  devtools(
    persist(
        immer((set, get) => ({
            //
            visitor: {}
        })),
        {
        name: "VISITOR_STORE",
        },
    )
  ),
);
