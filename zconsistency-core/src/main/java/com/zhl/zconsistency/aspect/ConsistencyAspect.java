package com.zhl.zconsistency.aspect;

import com.zhl.zconsistency.annotation.Consistency;
import com.zhl.zconsistency.model.ConsistencyTaskInstance;
import com.zhl.zconsistency.service.TaskStoreService;
import com.zhl.zconsistency.utils.ThreadLocalUtil;
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

    //TODO: 高并发情况下 tomcat线程池的线程 使用ThreadLocal 感觉会有问题 不能只靠ThreadLocal来控制任务的执行
    @Around("@annotation(consistency)")
    public Object markConsistencyTask(ProceedingJoinPoint point, Consistency consistency) throws Throwable {

        //是否是调度器在执行任务，如果是则直接执行任务即可，因为之前已经进行了任务持久化
        //TODO: 直接执行方法是有问题的 如果是直接去库里拿任务我觉得是可以接受的
        if (ThreadLocalUtil.getFlag()) {
            return point.proceed();
        }

        ConsistencyTaskInstance taskInstance = createTaskInstance(consistency, point);

        taskStoreService.initTask(taskInstance);

        //无论是调度执行还是立即执行的任务，任务初始化完成之后不对目标方法进行访问，返回null即可
        return null;
    }

    private ConsistencyTaskInstance createTaskInstance(Consistency consistency, ProceedingJoinPoint point) {
        return null;
    }
}