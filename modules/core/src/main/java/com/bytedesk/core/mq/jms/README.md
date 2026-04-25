# jms

This package handles JMS messaging integration, listener wiring, and runtime configuration.

## Implementation Notes

- REST controllers and application services expose package capabilities to upper layers.
- Listeners process entity lifecycle callbacks and domain events.
- Configuration classes, filters, or interceptors wire runtime behavior.
