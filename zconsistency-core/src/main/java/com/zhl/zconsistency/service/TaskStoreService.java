package com.zhl.zconsistency.service;

import com.zhl.zconsistency.model.ConsistencyTaskInstance;

import java.util.List;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
public interface TaskStoreService {

    int turnOnTask(ConsistencyTaskInstance taskInstance);

    ConsistencyTaskInstance getTaskById(Long id);

    int markSuccess(ConsistencyTaskInstance taskInstance);

    List<ConsistencyTaskInstance> listByUnFinishTask();

}
