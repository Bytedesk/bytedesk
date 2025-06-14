# ACD Agent Module

This module provides the agent management functionality for the Automatic Call Distribution (ACD) system.

## Overview

The Agent module implements the ACD agent management system, which allows:

- Agent status management
- Agent skill management
- Agent workload management
- Agent performance tracking
- Agent availability control

## Key Components

- `AgentEntity`: Agent configuration entity
- `AgentStatusEntity`: Agent status management entity
- `AgentSkillEntity`: Agent skill management entity
- `AgentService`: Agent management service
- `AgentController`: REST API endpoints

## Features

1. Agent Management
   - Agent registration and configuration
   - Agent profile management
   - Agent status tracking
   - Agent availability control
   - Agent authentication

2. Agent Status Management
   - Online/Offline status
   - Busy/Idle status
   - Break status
   - Custom status
   - Status history tracking

3. Agent Skill Management
   - Skill assignment
   - Skill level management
   - Skill group management
   - Skill-based routing
   - Skill performance tracking

4. Agent Performance
   - Call handling statistics
   - Response time tracking
   - Customer satisfaction metrics
   - Workload distribution
   - Performance reports

## Usage

```java
// Example: Creating a new agent
AgentEntity agent = AgentEntity.builder()
    .name("John Doe")
    .username("john.doe")
    .email("john.doe@example.com")
    .status("OFFLINE")
    .maxConcurrentCalls(3)
    .build();

// Example: Setting agent status
AgentStatusEntity status = AgentStatusEntity.builder()
    .agent(agent)
    .status("ONLINE")
    .reason("Ready to take calls")
    .startTime(LocalDateTime.now())
    .build();

// Example: Assigning skills to agent
AgentSkillEntity skill = AgentSkillEntity.builder()
    .agent(agent)
    .skill("Technical Support")
    .level(3)
    .build();
```

## Best Practices

1. Implement proper agent authentication
2. Monitor agent status changes
3. Balance agent workload
4. Regular skill assessment
5. Track agent performance metrics
6. Implement proper break management
7. Maintain agent availability records

## Related Documentation

- [Agent Management Guide](../agent-management.md)
- [Agent Status Management](../agent-status.md)
- [Agent Skill Management](../agent-skill.md)
- [Agent API Reference](../api/agent-api.md) 