/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-12 10:25:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-29 09:51:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.test.flux;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class CoffeeLoader {
    // private final ReactiveRedisConnectionFactory factory;
    // private final ReactiveRedisOperations<String, Coffee> coffeeOps;

    // public CoffeeLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Coffee> coffeeOps) {
    //     this.factory = factory;
    //     this.coffeeOps = coffeeOps;
    // }

    @PostConstruct
    public void loadData() {
        // factory.getReactiveConnection().serverCommands().flushAll().thenMany(
        //         Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
        //                 .map(name -> new Coffee(UUID.randomUUID().toString(), name))
        //                 .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.id(), coffee)))
        //         .thenMany(coffeeOps.keys("*")
        //                 .flatMap(coffeeOps.opsForValue()::get))
        //         .subscribe(System.out::println);
    }
}
