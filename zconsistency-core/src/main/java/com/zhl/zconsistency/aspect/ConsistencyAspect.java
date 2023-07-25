package com.zhl.zconsistency.aspect;

import com.zhl.zconsistency.annotation.Consistency;
import com.zhl.zconsistency.service.TaskStoreService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 一致性事务框架切面
 * @author hailang.zhang
 * @since 2023-07-25
 */
@Slf4j
@Aspect
@Component
public class ConsistencyAspect {
    private final TaskStoreService taskStoreService;

    public ConsistencyAspect(TaskStoreService taskStoreService) {
        this.taskStoreService = taskStoreService;
    }

    @Around("@annotation(consistency)")
    public Object markConsistencyTask(ProceedingJoinPoint point, Consistency consistency) {
        return null;
    }
}