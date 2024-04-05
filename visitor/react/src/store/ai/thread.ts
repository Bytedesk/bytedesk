import { requestThread } from "src/apis/ai/thread";
import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface ThreadAiState {
  // 用户信息
  thread: THREAD_AI.Thread;
  // message: MESSAGE_AI.Message;
  getThread: (wid: string) => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useThreadAiStore = create<ThreadAiState>()(
  devtools(
    // persist(
    immer((set, get) => ({
      // wid: "",
      thread: {
        tid: "",
        content: "",
        robot: {
          rid: "",
          name: "",
          description: "",
          welcome_tip: "",
          avatar: "",
          show_history: true,
        },
      },
      // message: {
      //   mid: '',
      //   type: '',
      //   content: '',
      //   user: {
      //     vid: '',
      //     nickname: '',
      //     avatar: '',
      //   }
      // },
      getThread: async (rid) => {
        //
        const response = await requestThread(rid);
        console.log("requestThread response", response);
        if (response.data.status_code > 0) {
          set({
            thread: response.data.data,
            // message: {
            //   content: response.data.data.content,
            //   // user: {
            //   //   avatar: response.data.data.robot.avatar
            //   // }
            // },
          });
        } else {
          console.log("请求会话报错", response.data.message);
        }
      },
      deleteEverything: () => set({}, true),
    })),
    {
      name: "THREAD_AI_STORE",
    },
  ),
  // )
);
