/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2023-11-18 22:13:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 10:49:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface RobotState {
  //
  messages: MESSAGE.Message[];
  //
  getHistoryMessage: () => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useRobotStore = create<RobotState>()(
  devtools(
    // persist(
    immer((set, get) => ({
      //
      messages: [],
      //
      getHistoryMessage() {},
      //
      deleteEverything: () => set({}, true),
    })),
    {
      name: "ROBOT_STORE_VISITOR",
    },
  ),
  // )
);
