# quartz

This package manages Quartz scheduler runtime wiring, scheduled jobs, and schedule-event integration.

## Implementation Notes

- QuartzConfig, QuartzConsts, QuartzEventPublisher, and QuartzJob define runtime wiring, shared constants, event publication, and the base scheduling abstraction.
- The event subpackage publishes fixed-interval schedule events such as five-second, one-minute, hourly, half-hour, and daily triggers.
- The job subpackage contains concrete Quartz jobs that correspond to those scheduled trigger cadences.
