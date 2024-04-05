import React, { useRef } from "react";
import useSubscription from "../hooks/useSubscription";
import { StompHeaders, IMessage } from "@stomp/stompjs";
import {
  MessageReceiverInterface,
  StompMessageReceiver,
} from "../interfaces/StompMessageReceiver";

function withSubscription<P>(
  WrappedComponent: StompMessageReceiver<P>,
  destinations: string | string[],
  headers: StompHeaders = {},
) {
  return (props: P) => {
    const ref = useRef<MessageReceiverInterface>();
    useSubscription(
      destinations,
      (message: IMessage) => {
        if (ref.current) ref.current.onMessage(message);
      },
      headers,
    );

    // @ts-ignore
    return <WrappedComponent ref={ref} {...props} />;
  };
}

export default withSubscription;
