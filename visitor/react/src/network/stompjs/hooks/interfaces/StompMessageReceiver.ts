import { messageCallbackType } from "@stomp/stompjs";
import React from "react";

export interface MessageReceiverInterface {
  onMessage: messageCallbackType;
}

export type StompMessageReceiver<P = {}> = React.ComponentClass<P> & {
  new (props: P, context?: any): React.Component<P> & MessageReceiverInterface;
};
