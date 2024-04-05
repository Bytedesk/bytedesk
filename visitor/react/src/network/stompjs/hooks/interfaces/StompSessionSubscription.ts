import {
  messageCallbackType,
  StompHeaders,
  StompSubscription,
} from "@stomp/stompjs";

export interface StompSessionSubscription {
  destination: string;
  callback: messageCallbackType;
  headers: StompHeaders;
  subscription?: StompSubscription;
}
