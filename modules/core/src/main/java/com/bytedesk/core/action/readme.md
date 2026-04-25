# action

This package manages action entities, execution events, permission bootstrap, and async dispatch support.

## Implementation Notes

- Core model files include ActionEntity, ActionRequest, ActionResponse, ActionExcel, and ActionTypeEnum.
- ActionRepository, ActionSpecification, ActionRestController, and ActionRestService provide persistence, query, and API entry points.
- ActionEntityListener, ActionEventListener, and the event subpackage handle lifecycle and domain events.
- ActionInitializer and ActionPermissions register bootstrap data and permission metadata.
- Subpackages aop and disruptor extend cross-cutting interception and asynchronous event delivery.
