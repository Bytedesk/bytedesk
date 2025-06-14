# FreeSwitch CDR Package

This package provides the Call Detail Record (CDR) management functionality for FreeSwitch integration.

## Overview

The CDR package implements the FreeSwitch call detail record system, which allows:

- Call detail recording and tracking
- Call duration and billing management
- Call quality monitoring
- Call history analysis

## Key Components

- `FreeSwitchCdrEntity`: CDR configuration entity
- `FreeSwitchCdrService`: CDR management service
- `FreeSwitchCdrController`: REST API endpoints

## Features

1. Call Detail Recording
   - Caller and callee information tracking
   - Call duration and billing time recording
   - Call start, answer, and end time tracking
   - Call status and hangup cause monitoring

2. Call Quality Monitoring
   - Codec information tracking
   - Call direction monitoring
   - Call status tracking
   - Recording file management

3. Call Analysis
   - Call duration formatting
   - Call success rate analysis
   - Call status description
   - Extended information in JSON format

## Usage

```java
// Example: Creating a new CDR record
FreeSwitchCdrEntity cdr = FreeSwitchCdrEntity.builder()
    .callerIdName("John Doe")
    .callerIdNumber("1234567890")
    .destinationNumber("0987654321")
    .startStamp(LocalDateTime.now())
    .duration(300)
    .billsec(280)
    .hangupCause("NORMAL_CLEARING")
    .direction("outbound")
    .build();
```

## Best Practices

1. Regular CDR data backup
2. Implement proper CDR data retention policies
3. Monitor call quality metrics
4. Analyze call patterns and trends
5. Maintain call recording storage

## Related Documentation

- [FreeSwitch CDR Documentation](https://freeswitch.org/confluence/display/FREESWITCH/XML+CDR)
- [CDR Format Reference](https://freeswitch.org/confluence/display/FREESWITCH/XML+CDR+Format)
- [Call Recording Guide](https://freeswitch.org/confluence/display/FREESWITCH/Recording)
