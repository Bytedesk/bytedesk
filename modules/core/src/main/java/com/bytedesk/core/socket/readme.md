# socket

This package coordinates realtime messaging runtimes across connection presence, MQTT transport, and STOMP over WebSocket.

## Implementation Notes

- The connection subpackage handles connection registry, online presence synchronization, heartbeat flushing, metrics, and presence TTL resolution.
- The mqtt subpackage contains protocol constants and properties, session and channel utilities, REST entrypoints, and server, service, handler, initializer, and listener support.
- The stomp subpackage contains STOMP configuration, controllers, handlers, interceptors, listeners, and event integration for WebSocket messaging.
