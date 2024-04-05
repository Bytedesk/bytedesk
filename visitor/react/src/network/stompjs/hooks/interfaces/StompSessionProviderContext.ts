import { Client, messageCallbackType, StompHeaders } from "@stomp/stompjs";

export interface StompSessionProviderContext {
  client?: Client;
  subscribe: (
    destination: string,
    callback: messageCallbackType,
    headers?: StompHeaders,
  ) => () => void;
}
