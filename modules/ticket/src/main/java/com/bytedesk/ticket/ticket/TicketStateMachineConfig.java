/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-16 19:05:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-16 23:39:03
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

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;

@Configurable
@EnableStateMachine(name = "ticketStateMachine")
public class TicketStateMachineConfig extends EnumStateMachineConfigurerAdapter<TicketStateEnum, TicketStateEventEnum> {
    
    @Override
	public void configure(StateMachineConfigurationConfigurer<TicketStateEnum, TicketStateEventEnum> config) throws Exception {
		config.withConfiguration()
			.autoStartup(true)
			.listener(new TicketStateMachineListener());
	}

    // @Override
	// public void configure(StateMachineConfigBuilder<TicketStateEnum, TicketStateEventEnum> config) throws Exception {
	// }

	@Override
	public void configure(StateMachineStateConfigurer<TicketStateEnum, TicketStateEventEnum> states) throws Exception {
		states.withStates()
			.initial(TicketStateEnum.INIT)
			.states(EnumSet.allOf(TicketStateEnum.class));
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<TicketStateEnum, TicketStateEventEnum> transitions) throws Exception {
		transitions
			.withExternal()
				.source(TicketStateEnum.INIT).target(TicketStateEnum.ASSIGNED).event(TicketStateEventEnum.ASSIGN)
			.and()
			.withExternal()
				.source(TicketStateEnum.ASSIGNED).target(TicketStateEnum.CLOSED).event(TicketStateEventEnum.CLOSE);
	}

	// @Override
	// public void configure(StateMachineModelConfigurer<TicketStateEnum, TicketStateEventEnum> model) throws Exception {
	// }
	
	@Bean
	public DefaultStateMachinePersister<TicketStateEnum, TicketStateEventEnum, Ticket> ticketStatePersister() {
		
		return new DefaultStateMachinePersister<>(new StateMachinePersist<TicketStateEnum, TicketStateEventEnum, Ticket>() {

			@Override
			public void write(StateMachineContext<TicketStateEnum, TicketStateEventEnum> context, Ticket contextObj)
					throws Exception {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException("Unimplemented method 'write'");
			}

			@Override
			public StateMachineContext<TicketStateEnum, TicketStateEventEnum> read(Ticket contextObj) throws Exception {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException("Unimplemented method 'read'");
			}
			
		});
	}


}
