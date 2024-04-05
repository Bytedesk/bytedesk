import { create } from "zustand";
import { devtools, persist } from "zustand/middleware";
import { immer } from "zustand/middleware/immer";

interface RobotAiState {
  //
  currentRobot: ROBOT_AI.Robot;
  //
  getRobot: () => void;
  // 清空所有信息
  deleteEverything: () => void;
}

export const useRobotAiStore = create<RobotAiState>()(
  devtools(
    // persist(
    immer((set, get) => ({
      //
      currentRobot: {},
      //
      getRobot() {},
      //
      deleteEverything: () => set({}, true),
    })),
    {
      name: "ROBOT_AI_STORE",
    },
  ),
  // )
);
