/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-09 14:56:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-09 15:01:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.route;

import java.util.Objects;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

/**
 * https://mp.weixin.qq.com/s/Wib0Ly45te00HMUnIG-tbg
 * https://edu.aliyun.com/course/313614
 * 
 * 通用的“策略树“框架，通过树形结构实现分发与委托，每层通过指定的参数进行向下分发委托，直到达到最终的执行者。
 * 该框架包含两个类：{@code StrategyHandler} 和 {@code AbstractStrategyRouter}
 * 其中：通过实现 {@code AbstractStrategyRouter} 抽象类完成对策略的分发，
 * 实现 {@code StrategyHandler} 接口来对策略进行实现。
 * 像是第二层 A、B 这样的节点，既是 Root 节点的策略实现者也是策略A1、A2、B1、B2 的分发者，这样的节点只需要
 * 同时继承 {@code StrategyHandler} 和实现 {@code AbstractStrategyRouter} 接口就可以了。
 *
 * <pre>
 *           +---------+
 *           |  Root   |   ----------- 第 1 层策略入口
 *           +---------+
 *            /       \  ------------- 根据入参 P1 进行策略分发
 *           /         \
 *     +------+      +------+
 *     |  A   |      |  B   |  ------- 第 2 层不同策略的实现
 *     +------+      +------+
 *       /  \          /  \  --------- 根据入参 P2 进行策略分发
 *      /    \        /    \
 *   +---+  +---+  +---+  +---+
 *   |A1 |  |A2 |  |B1 |  |B2 |  ----- 第 3 层不同策略的实现
 *   +---+  +---+  +---+  +---+
 * </pre>
 *
 * @author
 * @date
 * @see StrategyHandler
 */
@Component
public abstract class AbstractStrategyRouter<T, R> {

    /**
     * 策略映射器，根据指定的入参路由到对应的策略处理者。
     *
     * @param <T> 策略的入参类型
     * @param <R> 策略的返回值类型
     */
    public interface StrategyMapper<T, R> {
        /**
         * 根据入参获取到对应的策略处理者。可通过 if-else 实现，也可通过 Map 实现。
         *
         * @param param 入参
         * @return 策略处理者
         */
        StrategyHandler<T, R> get(T param);
    }

    private StrategyMapper<T, R> strategyMapper;

    /**
     * 类初始化时注册分发策略 Mapper
     */
    @PostConstruct
    private void abstractInit() {
        strategyMapper = registerStrategyMapper();
        Objects.requireNonNull(strategyMapper, "strategyMapper cannot be null");
    }

    @Getter
    @Setter
    @SuppressWarnings("unchecked")
    private StrategyHandler<T, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    /**
     * 执行策略，框架会自动根据策略分发至下游的 Handler 进行处理
     *
     * @param param 入参
     * @return 下游执行者给出的返回值
     */
    public R applyStrategy(T param) {
        final StrategyHandler<T, R> strategyHandler = strategyMapper.get(param);
        if (strategyHandler != null) {
            return strategyHandler.apply(param);
        }

        return defaultStrategyHandler.apply(param);
    }

    /**
     * 抽象方法，需要子类实现策略的分发逻辑
     *
     * @return 分发逻辑 Mapper 对象
     */
    protected abstract StrategyMapper<T, R> registerStrategyMapper();
    
}
