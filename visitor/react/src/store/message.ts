import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface MessageState {
  //
  messages: MESSAGE.Message[];
  //
  getHistoryMessage: () => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useMessageStore = create<MessageState>()(
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
      name: "MESSAGE_STORE_VISITOR",
    },
    // )
  ),
);
