/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2023-11-18 22:13:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-28 10:49:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
import { getWorkGroup } from "src/apis/workgroup";
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface WorkGroupState {
  //
  currentWorkGroup: WORKGROUP.WorkGroup;
  //
  getWorkGroup: (wid: string) => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useWorkGroupStore = create<WorkGroupState>()(
  devtools(
    // persist(
    immer((set, get) => ({
      //
      currentWorkGroup: {
        id: 0,
        wid: "",
        nickname: "",
        avatar: "",
        description: "",
      },
      //
      getWorkGroup: async (wid) => {
        //
        const response = await getWorkGroup(wid);
        console.log("getWorkGroup", response);
        if (response.data.status_code === 200) {
          set({
            currentWorkGroup: response.data.data,
          });
        } else {
          console.log("error");
        }
      },
      //
      deleteEverything: () => set({}, true),
    })),
    {
      name: "WORKGROUP_STORE_VISITOR",
    },
  ),
  // )
);
