// @ts-ignore
/* eslint-disable */

declare namespace THREAD_AI {
  //
  type HttpResultThread = {
    message?: string;
    status_code?: number;
    data?: Thread;
  };
  //
  type Thread = {
    tid?: string;
    content?: string;
    robot?: ROBOT_AI.Robot;
  };
}
