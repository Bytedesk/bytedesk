import { requestThread } from "src/apis/thread";
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface ThreadState {
  // 用户信息
  thread: THREAD.Thread;
  message: MESSAGE.MessageDTO;
  getThread: (wid: string) => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useThreadStore = create<ThreadState>()(
  devtools(
    // persist(
    immer((set, get) => ({
      // wid: "",
      thread: {
        tid: "",
        topic: "",
        token: "",
        content: "",
      },
      message: {
        mid: "",
        type: "",
        content: "",
        user: {
          uid: "",
          nickname: "",
          realName: "",
          avatar: "",
        },
      },
      getThread: async (wid) => {
        //
        const response = await requestThread(wid);
        console.log("requestThread response", response);
        if (response.data.status_code > 0) {
          set({
            thread: response.data.data.thread,
            message: response.data.data,
          });
        } else {
          console.log("请求会话报错", response.data.message);
        }
      },
      deleteEverything: () => set({}, true),
    })),
    {
      name: "THREAD_STORE_VISITOR",
    },
  ),
  // )
);
