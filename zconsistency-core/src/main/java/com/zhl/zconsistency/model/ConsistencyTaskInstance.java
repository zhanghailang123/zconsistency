package com.zhl.zconsistency.model;

import lombok.Data;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
@Data
public class ConsistencyTaskInstance {
    private Long id;
    private String taskId;
    private String methodSignName;
    private String methodName;
    private String parameterTypes;
    private String taskParam;

    /**
     * 枚举
     */
    private int taskStatues;

    /**
     * 执行间隔 默认60s
     */
    private int executeIntervalSec;

    /**
     * 执行次数
     */
    private int executeTimes;
    /**
     * 执行时间
     */
    private Long executeTime;

    /**
     * 降级的类
     */
    private String fallbackClassName;

    /**
     * 降级失败时的错误信息
     */
    private String fallbackErrorMsg;


}