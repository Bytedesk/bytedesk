import { IMessage } from "@stomp/stompjs/src/i-message";
import { messageCallbackType, StompHeaders } from "@stomp/stompjs";

export const subscriptions = new Map<string, Map<string, Function>>();

export function subscribeMock(
  destination: string,
  callback: messageCallbackType,
  // @ts-ignore
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  headers: StompHeaders = {},
) {
  const subscriptionId = Math.random().toString(36).substr(2, 9);

  if (!subscriptions.has(destination)) {
    subscriptions.set(destination, new Map<string, Function>());
  }

  // @ts-ignore
  subscriptions.get(destination).set(subscriptionId, callback);

  return () => {
    // @ts-ignore
    subscriptions.get(destination).delete(subscriptionId);
  };
}

/**
 * Simulates receiving a message from the server to the specified destination
 * @param destination The topic to send the message to
 * @param message The message to send
 */
export function mockReceiveMessage(
  destination: string,
  message: IMessage,
): void {
  if (subscriptions.has(destination)) {
    // @ts-ignore
    subscriptions.get(destination).forEach((callback: Function) => {
      callback(message);
    });
  }
}

/**
 * Gets the current subscriptions for the specified destination
 * @param destination The topic to get the subscriptions for, or undefined to get all subscriptions
 */
export function getMockSubscriptions(destination?: string) {
  if (destination) {
    return subscriptions.get(destination);
  }
  return subscriptions;
}
