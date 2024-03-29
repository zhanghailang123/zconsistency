package com.zhl.zconsistency.utils;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
public class ThreadLocalUtil {

    /**
     * 任务表示Action被AOP拦截的时候是不是应该立即执行，不再创建人物
     */
    private static final ThreadLocal<Boolean> FLAG = ThreadLocal.withInitial(() -> false);


    /**
     * 设置为true
     */
    public static void setFlag(boolean flag) {
        FLAG.set(flag);
    }

    /**
     * 获取是否为调度器在执行任务的标识
     *
     * @return 是否是调度器在执行任务
     */
    public static Boolean getFlag() {
        return FLAG.get();
    }
}
