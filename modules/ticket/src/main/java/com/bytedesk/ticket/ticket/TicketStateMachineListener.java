/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-16 19:05:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-17 07:18:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@WithStateMachine(name = "ticketStateMachine")
public class TicketStateMachineListener implements StateMachineListener<TicketStateEnum, TicketStateEventEnum> {

    @Override
    public void stateChanged(State<TicketStateEnum, TicketStateEventEnum> from,
            State<TicketStateEnum, TicketStateEventEnum> to) {
        log.info("state machine ticket stateChanged from {} to {}", from, to);
        // TODO Auto-generated method stub
    }

    @Override
    public void stateEntered(State<TicketStateEnum, TicketStateEventEnum> state) {
        log.info("state machine ticket stateEntered {}", state);
        // TODO Auto-generated method stub
    }

    @Override
    public void stateExited(State<TicketStateEnum, TicketStateEventEnum> state) {
        log.info("state machine ticket stateExited {}", state);
        // TODO Auto-generated method stub
    }

    @Override
    public void eventNotAccepted(Message<TicketStateEventEnum> event) {
        log.info("state machine ticket eventNotAccepted {}", event);
        // TODO Auto-generated method stub
    }

    @Override
    public void transition(Transition<TicketStateEnum, TicketStateEventEnum> transition) {
        log.info("state machine ticket transition {}", transition);
        // TODO Auto-generated method stub
    }

    @Override
    public void transitionStarted(Transition<TicketStateEnum, TicketStateEventEnum> transition) {
        log.info("state machine ticket transitionStarted {}", transition);
        // TODO Auto-generated method stub
    }

    @Override
    public void transitionEnded(Transition<TicketStateEnum, TicketStateEventEnum> transition) {
        log.info("state machine ticket transitionEnded {}", transition);
        // TODO Auto-generated method stub
    }

    @Override
    public void stateMachineStarted(StateMachine<TicketStateEnum, TicketStateEventEnum> stateMachine) {
        log.info("state machine ticket stateMachineStarted {}", stateMachine);
        // TODO Auto-generated method stub
    }

    @Override
    public void stateMachineStopped(StateMachine<TicketStateEnum, TicketStateEventEnum> stateMachine) {
        log.info("state machine ticket stateMachineStopped {}", stateMachine);
        // TODO Auto-generated method stub
    }

    @Override
    public void stateMachineError(StateMachine<TicketStateEnum, TicketStateEventEnum> stateMachine,
            Exception exception) {
                log.info("state machine ticket stateMachineError {} {}", stateMachine, exception);
        // TODO Auto-generated method stub
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
        log.info("state machine ticket extendedStateChanged {} {}", key, value);
        // TODO Auto-generated method stub
    }

    @Override
    public void stateContext(StateContext<TicketStateEnum, TicketStateEventEnum> stateContext) {
        log.info("state machine ticket stateContext {}", stateContext);
        // TODO Auto-generated method stub
    }
    
    
}
