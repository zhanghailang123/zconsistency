package com.zhl.zconsistency.manager;

import com.zhl.zconsistency.exceptions.ConsistencyException;
import com.zhl.zconsistency.model.ConsistencyTaskInstance;
import com.zhl.zconsistency.service.TaskStoreService;
import com.zhl.zconsistency.utils.ReflectTools;
import com.zhl.zconsistency.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
@Slf4j
@Component
public class TaskScheduleManager {

    /**
     * 此处@Autowired 和 @Resource 是否会有区别
     */
    private final TaskStoreService taskStoreService;
    private final CompletionService<ConsistencyTaskInstance> consistencyTaskPool;
    private final TaskEngineExecutor taskEngineExecutor;

    public TaskScheduleManager(TaskStoreService taskStoreService, CompletionService<ConsistencyTaskInstance> consistencyTaskPool, TaskEngineExecutor taskEngineExecutor) {
        this.taskStoreService = taskStoreService;
        this.consistencyTaskPool = consistencyTaskPool;
        this.taskEngineExecutor = taskEngineExecutor;
    }

    /**
     * 该方法在业务服务中的定时任务进行调度 查询并执行未完成的一执行任务
     */
    public void performceTask() throws InterruptedException {
        List<ConsistencyTaskInstance> taskInstances = taskStoreService.listByUnFinishTask();
        if (CollectionUtils.isEmpty(taskInstances)) {
            return;
        }
        taskInstances = taskInstances.stream().filter(e -> e.getExecuteTime() - System.currentTimeMillis() <= 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskInstances)) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(taskInstances.size());
        for (ConsistencyTaskInstance taskInstance : taskInstances) {
            consistencyTaskPool.submit(() -> {
                try {
                    taskEngineExecutor.execute(taskInstance);
                    return taskInstance;
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        log.info("[一致性任务框架] 执行完成");
    }

    /**
     * 执行指定任务
     * @param taskInstance
     */
    public void performanceTask(ConsistencyTaskInstance taskInstance) {
        String methodSignName = taskInstance.getMethodSignName();
        Class<?> clazz = getTaskMethodClass(methodSignName.split("#")[0]);
        if (ObjectUtils.isEmpty(clazz)) {
            return;
        }

        Object bean = SpringUtil.getBean(clazz);
        if (ObjectUtils.isEmpty(bean)) {
            return;
        }

        String methodName = taskInstance.getMethodName();
        String[] parameterTypes = taskInstance.getParameterTypes().split(",");
        Class<?>[] classes = ReflectTools.buildTypeClassArray(parameterTypes);

        Method targetMethod = getTargetMethod(methodName, classes, clazz);
        if (ObjectUtils.isEmpty(targetMethod)) {
            return;
        }

        Object[] args = ReflectTools.buildArgs(taskInstance.getTaskParam(), classes);
        try {
            //TODO 需不需要判断重复执行方法
            targetMethod.invoke(bean, args);
        } catch (InvocationTargetException e) {
            log.error("调用目标方法时，发生异常", e);
        } catch (Exception e) {
            throw new ConsistencyException(e);
        }
    }

    private Method getTargetMethod(String methodName, Class<?>[] classes, Class<?> clazz) {
        try {
            return clazz.getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            log.error("获取目标方法失败", e);
            return null;
        }
    }

    private Class<?> getTaskMethodClass(String s) {
        Class<?> clazz;
        try {
            clazz = Class.forName(s);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}