package com.yangzhou.service;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 对eventBus的封装
 * 系统统一使用EventBusCenter进行管理
 */
public class EventBusCenter {

    private final static EventBus eventBus = new EventBus();

    private final static Executor executor = Executors.newCachedThreadPool();

    public static void post(Object event) {
        execute(event);
    }

    /***
     * 同步执行
     * @param event 事件参数
     */
    public static void execute(Object event) {
        if (event == null) {
            return;
        }
        eventBus.post(event);
    }

    /**
     * 异步提交
     *
     * @param event - 事件参数
     */
    public static void submit(final Object event) {
        if (event == null) {
            return;
        }
        executor.execute(() -> eventBus.post(event));
    }

    /**
     * 注册event
     *
     * @param object - 监听对象
     */
    public static void register(Object object) {
        if (object == null) {
            return;
        }
        eventBus.register(object);
    }


    public static void unregister(Object object) {
        if (object == null) {
            return;
        }
        eventBus.unregister(object);
    }

}
