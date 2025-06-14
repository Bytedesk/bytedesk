# FreeSwitch Gateway Package

This package provides the gateway management functionality for FreeSwitch integration.

## Overview

The gateway package implements the FreeSwitch gateway management system, which allows:

- Gateway configuration and management
- SIP trunk connection handling
- Gateway status monitoring
- Call routing management

## Key Components

- `FreeSwitchGatewayEntity`: Gateway configuration entity
- `FreeSwitchGatewayService`: Gateway management service
- `FreeSwitchGatewayController`: REST API endpoints

## Features

1. Gateway Management
   - Create, update, and delete gateway configurations
   - Enable/disable gateways
   - Monitor gateway status
   - Configure SIP trunk parameters

2. Status Monitoring
   - Real-time gateway status tracking
   - Connection health monitoring
   - Automatic failover support
   - Status change notifications

3. Configuration
   - SIP server settings
   - Authentication credentials
   - Registration parameters
   - Custom gateway settings

## Usage

```java
// Example: Creating a new gateway
FreeSwitchGatewayEntity gateway = FreeSwitchGatewayEntity.builder()
    .gatewayName("Provider1")
    .proxy("sip.provider.com")
    .username("user123")
    .password("pass123")
    .register(true)
    .build();
```

## Best Practices

1. Always use secure passwords
2. Implement proper gateway monitoring
3. Configure appropriate failover strategies
4. Regular gateway health checks
5. Maintain gateway logs

## Related Documentation

- [FreeSwitch Documentation](https://freeswitch.org/confluence/display/FREESWITCH/FreeSWITCH+Documentation)
- [SIP Protocol Reference](https://tools.ietf.org/html/rfc3261)
- [Gateway Configuration Guide](https://freeswitch.org/confluence/display/FREESWITCH/Gateways)
