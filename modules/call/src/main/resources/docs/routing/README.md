# ACD Routing Module

This module provides the routing rule management functionality for the Automatic Call Distribution (ACD) system.

## Overview

The Routing module implements the ACD routing system, which allows:

- Custom routing rule creation
- Multi-condition routing
- Priority-based routing
- Time-based routing
- Skill-based routing

## Key Components

- `RoutingRuleEntity`: Routing rule configuration entity
- `RoutingStrategyEntity`: Routing strategy management entity
- `RoutingService`: Routing management service
- `RoutingController`: REST API endpoints

## Features

1. Routing Rule Management
   - Rule creation and configuration
   - Rule priority settings
   - Rule status management
   - Rule condition management
   - Rule action management

2. Routing Strategy
   - Round-robin routing
   - Least-recent routing
   - Random routing
   - Weight-based routing
   - Skill-based routing

3. Routing Conditions
   - Time-based conditions
   - Queue-based conditions
   - Agent-based conditions
   - Customer-based conditions
   - Custom conditions

4. Routing Actions
   - Queue assignment
   - Agent assignment
   - Transfer actions
   - Notification actions
   - Custom actions

## Usage

```java
// Example: Creating a routing rule
RoutingRuleEntity rule = RoutingRuleEntity.builder()
    .name("Business Hours Rule")
    .description("Route to support queue during business hours")
    .priority(1)
    .enabled(true)
    .condition("time >= '09:00' && time <= '18:00'")
    .action("assign_to_queue('support')")
    .build();

// Example: Creating a routing strategy
RoutingStrategyEntity strategy = RoutingStrategyEntity.builder()
    .name("Skill Based Routing")
    .type("SKILL_BASED")
    .parameters(Map.of(
        "skill_weights", Map.of(
            "technical", 3,
            "sales", 2,
            "general", 1
        )
    ))
    .build();
```

## Best Practices

1. Design clear and maintainable routing rules
2. Implement proper rule prioritization
3. Monitor routing effectiveness
4. Regular rule optimization
5. Maintain routing strategy documentation
6. Test routing rules thoroughly
7. Implement fallback routing

## Related Documentation

- [Routing Rule Management Guide](../routing-management.md)
- [Routing Strategy Guide](../routing-strategy.md)
- [Routing Conditions Reference](../routing-conditions.md)
- [Routing API Reference](../api/routing-api.md) 