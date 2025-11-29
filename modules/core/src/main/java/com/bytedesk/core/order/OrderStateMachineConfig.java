/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-16 18:17:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 10:34:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.order;

// import java.util.EnumSet;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.statemachine.StateMachineContext;
// import org.springframework.statemachine.StateMachinePersist;
// import org.springframework.statemachine.config.EnableStateMachine;
// import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
// import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
// import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
// import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
// import org.springframework.statemachine.persist.DefaultStateMachinePersister;

// @Configuration
// @EnableStateMachine(name = "orderStateMachine")
// public class OrderStateMachineConfig extends EnumStateMachineConfigurerAdapter<OrderStateEnum, OrderStateEventEnum> {
    
// 	@Override
// 	public void configure(StateMachineConfigurationConfigurer<OrderStateEnum, OrderStateEventEnum> config) throws Exception {
// 		config.withConfiguration()
// 			.autoStartup(true)
// 			.listener(new OrderStateMachineListener());
// 	}

//     // @Override
// 	// public void configure(StateMachineConfigBuilder<OrderStateEnum, OrderStateEventEnum> config) throws Exception {
// 	// }

// 	@Override
// 	public void configure(StateMachineStateConfigurer<OrderStateEnum, OrderStateEventEnum> states) throws Exception {
// 		states.withStates()
// 			.initial(OrderStateEnum.WAIT_PAYMENT)
// 			.states(EnumSet.allOf(OrderStateEnum.class));
// 	}

// 	@Override
// 	public void configure(StateMachineTransitionConfigurer<OrderStateEnum, OrderStateEventEnum> transitions) throws Exception {
// 		transitions
// 			.withExternal()
// 				.source(OrderStateEnum.WAIT_PAYMENT).target(OrderStateEnum.FINISH).event(OrderStateEventEnum.PAYED)
// 			.and()
// 			.withExternal()
// 				.source(OrderStateEnum.FINISH).target(OrderStateEnum.CANCEL).event(OrderStateEventEnum.REFUNDED);
// 	}

// 	// @Override
// 	// public void configure(StateMachineModelConfigurer<OrderStateEnum, OrderStateEventEnum> model) throws Exception {
// 	// }
	
// 	@Bean
// 	public DefaultStateMachinePersister<OrderStateEnum, OrderStateEventEnum, OrderEntity> orderStatePersister() {
		
// 		return new DefaultStateMachinePersister<>(new StateMachinePersist<OrderStateEnum, OrderStateEventEnum, OrderEntity>() {

// 			@Override
// 			public void write(StateMachineContext<OrderStateEnum, OrderStateEventEnum> context, OrderEntity contextObj)
// 					throws Exception {
// 				// TODO Auto-generated method stub
// 				throw new UnsupportedOperationException("Unimplemented method 'write'");
// 			}

// 			@Override
// 			public StateMachineContext<OrderStateEnum, OrderStateEventEnum> read(OrderEntity contextObj) throws Exception {
// 				// TODO Auto-generated method stub
// 				throw new UnsupportedOperationException("Unimplemented method 'read'");
// 			}
			
// 		});
// 	}
    
// }
