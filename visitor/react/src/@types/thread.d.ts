// @ts-ignore
/* eslint-disable */

declare namespace THREAD {
  //
  type Thread = {
    id?: number;
    tid?: string;
    topic?: string;
    token?: string;
    content?: string;
    ip?: string;
    location?: string;
    status?: string;
    timestamp?: string;
    type?: string;
    client?: string;
    closeType?: string;
    startedAt?: string;
    appointed?: boolean;
    rated?: boolean;
    closed?: boolean;
    timeLength?: number;
    initResponseLength?: number;
    averageResponseSeconds?: number;
    autoClose?: boolean;
    current?: boolean;
    starType?: number;
    unreadCount?: number;
    unreadCountVisitor?: number;
    nodisturb?: boolean;
    nodisturbVisitor?: boolean;
    top?: boolean;
    topVisitor?: boolean;
    unread?: boolean;
    unreadVisitor?: boolean;
    solved?: boolean;
    temp?: boolean;
    queue?: string;
    visitor?: VISITOR_AI.Visitor;
    contact?: VISITOR_AI.Visitor;
    group?: string;
    agent?: VISITOR_AI.Visitor;
    workGroup?: WORKGROUP.WorkGroup;
    channel?: Channel;
    createdAt?: string;
    updatedAt?: string;
    closedAt?: string;
  };

  type PageResultThread = {
    message?: string;
    status_code?: number;
    data?: Thread;
  };
}
