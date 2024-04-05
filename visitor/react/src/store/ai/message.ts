import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface MessageAiState {
  //
  messages: MESSAGE.Message[];
  //
  getHistoryMessage: () => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useMessageAiStore = create<MessageAiState>()(
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
      name: "MESSAGE_STORE",
    },
    // )
  ),
);
