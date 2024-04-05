import React, { useEffect, useRef, useState } from "react";
import StompContext from "../context/StompContext";
import SockJS from "sockjs-client";
import {
  Client,
  IStompSocket,
  messageCallbackType,
  StompHeaders,
} from "@stomp/stompjs";
import { StompSessionProviderProps } from "../interfaces/StompSessionProviderProps";
import { StompSessionSubscription } from "../interfaces/StompSessionSubscription";

/**
 * The StompSessionProvider manages the STOMP connection
 * All Hooks and HOCs in this library require an ancestor of this type.
 * The URL to connect to can be specified via the url prop.
 * Depending on the Schema of the URL either Sockjs or a raw Websocket is used.
 * You can override this behavior with the brokerURL or webSocketFactory props, which will then be forwarded to @stomp/stompjs
 * Custom @stomp/stompjs options can be used as props.
 * Please consult the @stomp/stompjs documentation for more information.
 */
function StompSessionProvider(props: StompSessionProviderProps) {
  let { url, children, stompClientOptions, ...stompOptions } = props;

  // Support old API
  if (stompClientOptions) stompOptions = stompClientOptions;

  const [client, setClient] = useState<Client | undefined>(undefined);
  const subscriptionRequests = useRef(new Map());

  useEffect(() => {
    const _client = new Client(stompOptions);

    if (!stompOptions.brokerURL && !stompOptions.webSocketFactory) {
      _client.webSocketFactory = function () {
        const parsedUrl = new URL(url, window?.location?.href);
        if (parsedUrl.protocol === "http:" || parsedUrl.protocol === "https:") {
          return new SockJS(url) as IStompSocket;
        } else if (
          parsedUrl.protocol === "ws:" ||
          parsedUrl.protocol === "wss:"
        ) {
          return new WebSocket(url) as IStompSocket;
        } else throw new Error("Protocol not supported");
      };
    }

    _client.onConnect = function (frame) {
      if (stompOptions.onConnect) stompOptions.onConnect(frame);

      subscriptionRequests.current.forEach((value) => {
        value.subscription = _client.subscribe(
          value.destination,
          value.callback,
          value.headers,
        );
      });

      setClient(_client);
    };

    _client.onWebSocketClose = function (event) {
      if (stompOptions.onWebSocketClose) stompOptions.onWebSocketClose(event);

      setClient(undefined);
    };

    if (!stompOptions.onStompError) {
      _client.onStompError = function (frame) {
        throw frame;
      };
    }

    _client.activate();

    return () => {
      _client.deactivate();
    };
  }, [url, ...Object.values(stompOptions)]);

  const subscribe = (
    destination: string,
    callback: messageCallbackType,
    headers: StompHeaders = {},
  ) => {
    const subscriptionId = Math.random().toString(36).substr(2, 9);
    const subscriptionRequest: StompSessionSubscription = {
      destination,
      callback,
      headers,
    };

    subscriptionRequests.current.set(subscriptionId, subscriptionRequest);

    if (client && client.connected) {
      subscriptionRequest.subscription = client.subscribe(
        destination,
        callback,
        headers,
      );
    }

    return () => {
      const subscriptionData = subscriptionRequests.current.get(subscriptionId);

      if (subscriptionData.subscription) {
        subscriptionData.subscription.unsubscribe();
      }

      subscriptionRequests.current.delete(subscriptionId);
    };
  };

  return (
    <StompContext.Provider
      value={{
        client,
        subscribe,
      }}
    >
      {children}
    </StompContext.Provider>
  );
}

export default StompSessionProvider;
