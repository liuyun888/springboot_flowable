package com.boot.flowable.controller;

import com.boot.flowable.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 并行网关测试
 */

@Api(value = "ParallelGatewayController|并行网关测试")
@RestController
@RequestMapping("/parallelGateway")
public class ParallelGatewayController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * .提交审批请求
     */
    @PostMapping("/start")
    @ApiOperation(value="提交审批请求", notes="流程key目前为写死的，可以改为参数传入")
    public ResponseUtils startFlow() {
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("myParallelGateway");
        String processId = processInstance.getId();
        String name = processInstance.getName();
        System.out.println(processId + ":" + name);
        return ResponseUtils.ok(processId + ":" + name);
    }

    /**
     * .获取用户的任务
     *
     * @param userId 用户id
     */
    @GetMapping("/getTasks/{userId}")
    @ApiOperation(value="根据userId获取用户的任务", notes="参数：userId")
    public ResponseUtils getTasks(@PathVariable String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        return ResponseUtils.ok(tasks.toString());
    }

    /**
     * .审批通过
     */
    @PostMapping("/taskPass/{taskId}")
    @ApiOperation(value="审批通过", notes="参数：taskId")
    public ResponseUtils taskPass(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseUtils.error("流程不存在");
        }
        //通过审核
        taskService.complete(taskId);
        return ResponseUtils.ok("流程审核通过！");
    }

}
