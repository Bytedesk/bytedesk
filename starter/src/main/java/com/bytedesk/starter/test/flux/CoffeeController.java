/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-12 10:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-12 10:01:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.test.flux;

// import org.springframework.data.redis.core.ReactiveRedisOperations;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
// import reactor.core.publisher.Flux;

// @RestController
// public class CoffeeController {
//     private final ReactiveRedisOperations<String, Coffee> coffeeOps;

//     CoffeeController(ReactiveRedisOperations<String, Coffee> coffeeOps) {
//         this.coffeeOps = coffeeOps;
//     }

//     // http://localhost:1001/coffees
//     @GetMapping("/coffees")
//     public Flux<Coffee> all() {
//         return coffeeOps.keys("*")
//                 .flatMap(coffeeOps.opsForValue()::get);
//     }
    
// }