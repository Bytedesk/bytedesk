# FreeSwitch Number Package

This package provides FreeSwitch integrated SIP user management functionality.

## Overview

The Number package implements the FreeSwitch user management system, supporting:

- SIP user account management
- User registration tracking
- User status monitoring
- User authentication

## Core Components

- `FreeSwitchNumberEntity`: User configuration entity
- `FreeSwitchNumberService`: User management service
- `FreeSwitchNumberController`: REST API interface

## Features

1. User Account Management
   - Create and manage SIP user accounts
   - Configure user credentials
   - Set display names and email addresses
   - Enable/disable user accounts

2. Registration Management
   - Track user registration status
   - Monitor registration IP addresses
   - Track user agent information
   - Handle registration timeouts

3. User Configuration
   - SIP username and domain
   - Password management
   - Account code assignment
   - Extended user information

## Usage Examples

```java
// Example: Create a new SIP user
FreeSwitchNumberEntity user = FreeSwitchNumberEntity.builder()
    .username("john.doe")
    .domain("sip.example.com")
    .password("secure123")
    .displayName("John Doe")
    .email("john.doe@example.com")
    .accountcode("ACC001")
    .enabled(true)
    .build();
```

## Best Practices

1. Implement secure password policies
2. Regular user account maintenance
3. Monitor user registration status
4. Track user activity
5. Regular backup of user configurations

## Related Documentation

- [FreeSwitch User Guide](https://freeswitch.org/confluence/display/FREESWITCH/User+Directory)
- [SIP Authentication Guide](https://freeswitch.org/confluence/display/FREESWITCH/SIP+Authentication)
- [User Management Commands](https://freeswitch.org/confluence/display/FREESWITCH/User+Management+Commands) 