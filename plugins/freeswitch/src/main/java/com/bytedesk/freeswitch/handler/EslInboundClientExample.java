/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-05 20:19:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-05 20:20:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.handler;

import link.thingscloud.freeswitch.esl.IEslEventListener;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.ServerConnectionListener;
import link.thingscloud.freeswitch.esl.inbound.option.InboundClientOption;
import link.thingscloud.freeswitch.esl.inbound.option.ServerOption;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;

/**
 * https://github.com/zhouhailin/freeswitch-externals
 * 
 * <p>EslInboundClientExample class.</p>
 *
 * @author : <a href="mailto:ant.zhou@aliyun.com">zhouhailin</a>
 * @version 1.0.0
 */
public class EslInboundClientExample {

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        InboundClientOption option = new InboundClientOption();

        option.defaultPassword("ClueCon")
                .addServerOption(new ServerOption("172.16.44.246", 8021));
        option.addEvents("all");

        option.addListener(new IEslEventListener() {
            @Override
            public void eventReceived(String addr, EslEvent event) {
                System.out.println(addr);
                System.out.println(event);
            }

            @Override
            public void backgroundJobResultReceived(String addr, EslEvent event) {
                System.out.println(addr);
                System.out.println(event);
            }
        });

        option.serverConnectionListener(new ServerConnectionListener() {
            @Override
            public void onOpened(ServerOption serverOption) {
                System.out.println("---onOpened--");
            }

            @Override
            public void onClosed(ServerOption serverOption) {
                System.out.println("---onClosed--");
            }
        });

        InboundClient inboundClient = InboundClient.newInstance(option);

        inboundClient.start();


        System.out.println(option.serverAddrOption().first());
        System.out.println(option.serverAddrOption().last());
        System.out.println(option.serverAddrOption().random());


    }

}
