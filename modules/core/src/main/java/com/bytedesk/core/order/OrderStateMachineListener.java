/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-16 18:23:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 10:34:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.order;

// import org.springframework.messaging.Message;
// import org.springframework.statemachine.StateContext;
// import org.springframework.statemachine.StateMachine;
// import org.springframework.statemachine.annotation.WithStateMachine;
// import org.springframework.statemachine.listener.StateMachineListener;
// import org.springframework.statemachine.state.State;
// import org.springframework.statemachine.transition.Transition;
// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;


// @Slf4j
// @Component("orderStateMachineListener")
// @WithStateMachine(name = "orderStateMachine")
// public class OrderStateMachineListener implements StateMachineListener<OrderStateEnum, OrderStateEventEnum> {

//     @Override
//     public void stateChanged(State<OrderStateEnum, OrderStateEventEnum> from,
//             State<OrderStateEnum, OrderStateEventEnum> to) {
//         log.info("state machine order stateChanged from {} to {}", from, to);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void stateEntered(State<OrderStateEnum, OrderStateEventEnum> state) {
//         log.info("state machine order stateEntered {}", state);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void stateExited(State<OrderStateEnum, OrderStateEventEnum> state) {
//         log.info("state machine order stateExited {}", state);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void eventNotAccepted(Message<OrderStateEventEnum> event) {
//         log.info("state machine order eventNotAccepted {}", event);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void transition(Transition<OrderStateEnum, OrderStateEventEnum> transition) {
//         log.info("state machine order transition {}", transition);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void transitionStarted(Transition<OrderStateEnum, OrderStateEventEnum> transition) {
//         log.info("state machine order transitionStarted {}", transition);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void transitionEnded(Transition<OrderStateEnum, OrderStateEventEnum> transition) {
//         log.info("state machine order transitionEnded {}", transition);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void stateMachineStarted(StateMachine<OrderStateEnum, OrderStateEventEnum> stateMachine) {
//         log.info("state machine order stateMachineStarted {}", stateMachine);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void stateMachineStopped(StateMachine<OrderStateEnum, OrderStateEventEnum> stateMachine) {
//         log.info("state machine order stateMachineStopped {}", stateMachine);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void stateMachineError(StateMachine<OrderStateEnum, OrderStateEventEnum> stateMachine,
//             Exception exception) {
//                 log.info("state machine order stateMachineError {} {}", stateMachine, exception);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void extendedStateChanged(Object key, Object value) {
//         log.info("state machine order extendedStateChanged {} {}", key, value);
//         // TODO Auto-generated method stub
//     }

//     @Override
//     public void stateContext(StateContext<OrderStateEnum, OrderStateEventEnum> stateContext) {
//         log.info("state machine order stateContext {}", stateContext);
//         // TODO Auto-generated method stub
//     }
    
    
// }
