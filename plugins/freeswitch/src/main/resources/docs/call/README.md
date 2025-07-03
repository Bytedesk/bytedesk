# FreeSwitch Call Package

This package provides the call management functionality for FreeSwitch integration.

## Overview

The Call package implements the FreeSwitch call management system, which allows:

- Call tracking and monitoring
- Call queue management
- Agent assignment and routing
- Call status tracking
- Call duration management

## Key Components

- `FreeSwitchCallEntity`: Call management entity
- `FreeSwitchCallService`: Call management service
- `FreeSwitchCallController`: REST API endpoints

## Features

1. Call Management
   - Caller and callee number tracking
   - Call type management (Inbound, Outbound, Internal)
   - Call status monitoring (Queued, Ringing, In Progress, Completed, Failed, Abandoned)
   - Call duration and wait time tracking

2. Queue Management
   - Queue assignment
   - Agent assignment
   - Skill-based routing
   - Wait time monitoring

3. Call Analysis
   - Call type analysis
   - Call status tracking
   - Duration analysis
   - Queue performance monitoring
   - Agent performance tracking

## Usage

```java
// Example: Creating a new call record
FreeSwitchCallEntity call = FreeSwitchCallEntity.builder()
    .callUuid("123e4567-e89b-12d3-a456-426614174000")
    .type(CallType.INBOUND)
    .callerNumber("1234567890")
    .calleeNumber("0987654321")
    .status(CallStatus.QUEUED)
    .startTime(ZonedDateTime.now())
    .queueId(1L)
    .agentId(1L)
    .skills("{\"language\":\"en\",\"department\":\"support\"}")
    .build();
```

## Best Practices

1. Implement proper call routing strategies
2. Monitor queue performance
3. Track agent availability and performance
4. Maintain call quality metrics
5. Regular call data analysis
6. Implement proper call data retention policies

## Related Documentation

- [FreeSwitch Call Management](https://freeswitch.org/confluence/display/FREESWITCH/Call+Management)
- [Queue Management Guide](https://freeswitch.org/confluence/display/FREESWITCH/Queue+Management)
- [Agent Management](https://freeswitch.org/confluence/display/FREESWITCH/Agent+Management) 