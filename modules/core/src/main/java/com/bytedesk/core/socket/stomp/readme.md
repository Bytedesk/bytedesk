# stomp

This package manages STOMP over WebSocket configuration, message interception, and STOMP transport lifecycle integration.

## Implementation Notes

- StompConfig and StompController define the STOMP runtime wiring and controller-facing entrypoints.
- The event subpackage and StompEventPublisher handle connected, disconnected, subscribe, and unsubscribe events.
- The handler, interceptor, and listener subpackages split message handling, channel interception, and runtime listener responsibilities.
