package com.bytedesk.ai.robot.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.ai.robot.RobotEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RobotCreateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private RobotEntity robot;

    public RobotCreateEvent(RobotEntity robot) {
        super(robot);
        this.robot = robot;
    }
}
