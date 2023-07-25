package com.zhl.zconsistency.manager;

import cn.hutool.core.util.ReflectUtil;
import com.zhl.zconsistency.model.ConsistencyTaskInstance;
import com.zhl.zconsistency.service.TaskStoreService;
import com.zhl.zconsistency.utils.ReflectTools;
import com.zhl.zconsistency.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
@Slf4j
@Service
public class TaskEngineExecutorImpl implements TaskEngineExecutor{

    private final TaskStoreService taskStoreService;

    public TaskEngineExecutorImpl(TaskStoreService taskStoreService) {this.taskStoreService = taskStoreService;}

    @Override
    public void execute(ConsistencyTaskInstance taskInstance) {
        // start task
        int result = taskStoreService.turnOnTask(taskInstance);
        if (result <= 0) {
            log.warn("【一致性任务框架】任务已经为启动状态，退出执行流程 task：{}", taskInstance);
        }

        //暂时不做分片逻辑 只根据Id来查任务
        taskInstance = taskStoreService.getTaskById(taskInstance.getId());

        //TODO 真正执行任务的地方

        //标记为执行成功，这里会移除任务
        int successResult = taskStoreService.markSuccess(taskInstance);
        log.info("[一致性框架]标记为执行成功的结果为【{}】", successResult > 0);
    }

    @Override
    public void fallBack(ConsistencyTaskInstance taskInstance) {
        if (StringUtils.isEmpty(taskInstance.getFallbackClassName()) || "Void".equals(taskInstance.getFallbackClassName())) {
            //TODO do alert 报警

            return;
        }

        Class<?> fallbackClass = ReflectTools.getClassByName(taskInstance.getFallbackClassName());
        // TODO 是否根据全局执行时间进行降级

        //获取参数值列表的json数组字符串
        String taskParamText = taskInstance.getTaskParam();
        //参数类型字符串 多个用逗号进行了分割
        String parameterTypes = taskInstance.getParameterTypes();
        //构造参数类数组
        Class<?>[] paramTypes = getParameterTypes(parameterTypes);
        Object[] paramValues = ReflectTools.buildArgs(taskParamText, paramTypes);
        Object fallbackClassBean = getBeanBySpringApplicationContext(fallbackClass, paramValues);
        Method fallbackMethod = ReflectUtil.getMethod(fallbackClass, taskInstance.getMethodName(), paramTypes);
    }

    private Object getBeanBySpringApplicationContext(Class<?> fallbackClass, Object[] paramValues) {
        return SpringUtil.getBean(fallbackClass, paramValues);
    }

    private Class<?>[] getParameterTypes(String parameterTypes) {
        return ReflectTools.buildTypeClassArray(parameterTypes.split(","));
    }
}