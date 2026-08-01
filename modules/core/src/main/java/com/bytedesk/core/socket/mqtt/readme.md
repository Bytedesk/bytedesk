# mqtt

This package manages MQTT runtime configuration, session handling, and MQTT transport lifecycle integration.

## Implementation Notes

- MqttProperties, MqttConsts, MqttSession, MqttChannelUtils, and MqttUtils provide runtime properties, constants, session state, channel helpers, and utility logic.
- MqttRestController exposes transport-facing management or integration endpoints.
- The event subpackage and MqttEventPublisher handle connected, disconnected, subscribe, and unsubscribe events.
- The handler, initializer, listener, protocol, server, and service subpackages split protocol processing and server runtime responsibilities.
