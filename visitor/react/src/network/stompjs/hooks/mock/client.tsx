import { IPublishParams } from "@stomp/stompjs";

export const messages = new Map<string, Array<IPublishParams>>();

/**
 * A mock implementation of the publish function of the @stomp/stompjs client.
 * Will store the messages in a map, keyed by the destination.
 * @param params
 */
export function mockClientPublish(params: IPublishParams) {
  if (!messages.has(params.destination)) {
    messages.set(params.destination, []);
  }

  // @ts-ignore
  messages.get(params.destination).push(params);
}

/**
 * Gets a default Mock of the @stomp/stompjs client.
 * If you require a custom client, you can use this as a base.
 */
export function getMockClient() {
  return {
    publish: mockClientPublish,
  };
}

/**
 * Gets all messages which have been sent via a mock client.
 * @param destination The destination to get messages for, or undefined to get all messages.
 */
export function getSentMockMessages(destination?: string) {
  if (destination) {
    return messages.get(destination);
  }
  return messages;
}
