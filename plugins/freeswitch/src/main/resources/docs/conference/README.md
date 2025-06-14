# FreeSwitch Conference Package

This package provides the conference room management functionality for FreeSwitch integration.

## Overview
The conference package implements the FreeSwitch conference system, which allows:
- Conference room creation and management
- Participant management
- Conference recording
- Conference room access control

## Key Components
- `FreeSwitchConferenceEntity`: Conference room configuration entity
- `FreeSwitchConferenceService`: Conference room management service
- `FreeSwitchConferenceController`: REST API endpoints

## Features
1. Conference Room Management
   - Create and configure conference rooms
   - Set room passwords and access controls
   - Manage maximum participant limits
   - Enable/disable conference rooms

2. Recording Management
   - Enable/disable conference recording
   - Configure recording paths
   - Manage recording files
   - Track recording status

3. Room Configuration
   - Room name and description
   - Password protection
   - Member limits
   - Extended configuration in JSON format

## Usage
```java
// Example: Creating a new conference room
FreeSwitchConferenceEntity conference = FreeSwitchConferenceEntity.builder()
    .conferenceName("Sales Meeting")
    .description("Weekly sales team meeting")
    .password("123456")
    .maxMembers(10)
    .recordEnabled(true)
    .recordPath("/recordings/sales")
    .enabled(true)
    .build();
```

## Best Practices
1. Implement proper password policies
2. Regular conference room maintenance
3. Monitor room capacity
4. Manage recording storage
5. Regular backup of conference configurations

## Related Documentation
- [FreeSwitch Conference Documentation](https://freeswitch.org/confluence/display/FREESWITCH/Conference)
- [Conference Commands Reference](https://freeswitch.org/confluence/display/FREESWITCH/Conference+Commands)
- [Conference Recording Guide](https://freeswitch.org/confluence/display/FREESWITCH/Conference+Recording) 