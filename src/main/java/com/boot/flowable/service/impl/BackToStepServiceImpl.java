package com.boot.flowable.service.impl;

import com.boot.flowable.service.BackToStepService;
import com.boot.flowable.utils.ProcessDefinitionUtils;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BackToStepServiceImpl implements BackToStepService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessEngine processEngine;



    /**
     * @param executionId  the id of an execution
     * @param targetKeys      目标节点的key  为act_hi_taskinst 中 TASK_DEF_KEY
     */
    @Override
    public void rollback(String executionId, List<String>  targetKeys) {

        //执行驳回操作
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(executionId)
                .moveSingleExecutionToActivityIds(executionId, targetKeys)
                .changeState();

    }

    /**
     * 从并行网关驳回，需要清除其他并行实例
     * @param proInstanceId
     * @param targetTaskKey
     */
    @Override
    public void backToStepFromParallelGateway(String proInstanceId, String targetTaskKey) {
        // 并行网关的退回
        List<String > currentExecutionIds = new ArrayList<>();
        List<Execution> executions = runtimeService.createExecutionQuery().parentId(proInstanceId).list();
        for (Execution execution : executions) {
            System.out.println("并行网关节点数："+execution.getActivityId());
            currentExecutionIds.add(execution.getId());
        }
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(currentExecutionIds, targetTaskKey)
                .changeState();

    }

    /**
     * 退回到并行网关，需要每个分支都创建实例
     * @param processInstanceId
     */
    @Override
    public void backToStepToParallelGateway(String processInstanceId ) {

        List<String> targetTaskKeys = new ArrayList<>();
//        targetTaskKeys.add(UUID.randomUUID().toString());
//        targetTaskKeys.add(UUID.randomUUID().toString());
        targetTaskKeys.add("BPTask");
        targetTaskKeys.add("managerTask");

        Execution execution = runtimeService.createExecutionQuery().parentId(processInstanceId).singleResult();

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveSingleActivityIdToActivityIds(execution.getActivityId(), targetTaskKeys)
                .changeState();
    }


}
