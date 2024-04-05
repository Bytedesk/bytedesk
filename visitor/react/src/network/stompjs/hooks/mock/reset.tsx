import { subscriptions } from "./subscriptions";
import { messages } from "./client";

/**
 * Resets the state of the mock implementation, clearing all subscriptions and messages.
 */
export function reset() {
  subscriptions.clear();
  messages.clear();
}
