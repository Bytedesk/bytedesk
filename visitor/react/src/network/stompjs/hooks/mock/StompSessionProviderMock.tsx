import React from "react";
import StompContext from "../context/StompContext";
import { subscribeMock } from "./subscriptions";
import { getMockClient } from "./client";

/**
 * A mock StompSessionProvider.
 * Messages send via this mock implementation can be received via the getSentMockMessages method.
 * Subscriptions can be received via the getMockSubscriptions method.
 * The sendMockMessage method can be used, to simulate receiving a message from the server.
 *
 * @param props.client Optional. Can be used to provide a custom mock of the sompjs client,
 * in case you require additional properties/functions to be present. getMockClient can be used as a base.
 * @constructor
 */
export default function StompSessionProviderMock(props: {
  children: React.ReactNode;
  client?: any;
}) {
  return (
    <StompContext.Provider
      value={{
        subscribe: subscribeMock,
        // @ts-ignore
        client: props.client ?? getMockClient(),
      }}
    >
      {props.children}
    </StompContext.Provider>
  );
}
