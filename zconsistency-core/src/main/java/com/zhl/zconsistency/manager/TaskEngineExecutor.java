package com.zhl.zconsistency.manager;

import com.zhl.zconsistency.model.ConsistencyTaskInstance;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
public interface TaskEngineExecutor {

    void execute(ConsistencyTaskInstance taskInstance);

    void fallBack(ConsistencyTaskInstance taskInstance);
}
