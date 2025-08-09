# ACD Queue Module

This module provides the queue management functionality for the Automatic Call Distribution (ACD) system.

## Overview

The Queue module implements the ACD queue management system, which allows:

- Multi-level queue management
- Queue member management
- Queue priority settings
- Queue load balancing
- Queue statistics and monitoring

## Key Components

- `QueueEntity`: Queue configuration entity
- `QueueMemberEntity`: Queue member management entity
- `QueueService`: Queue management service
- `QueueController`: REST API endpoints

## Features

1. Queue Management
   - Queue creation and configuration
   - Queue priority settings
   - Queue status monitoring
   - Queue capacity management
   - Queue overflow handling

2. Queue Member Management
   - Member addition and removal
   - Member status tracking
   - Member skill management
   - Member workload balancing
   - Member performance monitoring

3. Queue Operations
   - Queue join and leave operations
   - Queue transfer operations
   - Queue overflow handling
   - Queue timeout management
   - Queue announcement settings

4. Queue Statistics
   - Queue length monitoring
   - Queue wait time tracking
   - Queue abandonment rate
   - Queue service level monitoring
   - Queue performance reports

## Usage

```java
// Example: Creating a new queue
QueueEntity queue = QueueEntity.builder()
    .name("Support Queue")
    .description("Technical support queue")
    .priority(1)
    .maxLength(100)
    .timeout(300)
    .retry(3)
    .build();

// Example: Adding a member to queue
QueueMemberEntity member = QueueMemberEntity.builder()
    .queue(queue)
    .agent(agent)
    .penalty(0)
    .paused(false)
    .build();
```

## Best Practices

1. Implement proper queue capacity planning
2. Set appropriate queue timeouts
3. Monitor queue performance metrics
4. Balance queue member workload
5. Regular queue statistics analysis
6. Implement queue overflow strategies
7. Maintain queue member skills

## Related Documentation

- [Queue Management Guide](../queue-management.md)
- [Queue Member Management](../queue-member.md)
- [Queue Statistics](../queue-statistics.md)
- [Queue API Reference](../api/queue-api.md) 