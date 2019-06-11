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

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/orderApproval")
public class OrderApprovalController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * .提交采购订单的审批请求
     *
     * @param userId 用户id
     */
    @PostMapping("/start/{userId}/{purchaseOrderId}")
    @ApiOperation(value="提交采购订单的审批请求", notes="流程key目前为写死的，可以改为参数传入")
    public ResponseUtils startFlow(@PathVariable String userId, @PathVariable String purchaseOrderId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("purchaseOrderId", purchaseOrderId);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("OrderApproval", map);
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
    public ResponseUtils getTasks(@PathVariable String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        return ResponseUtils.ok(tasks.toString());
    }

    /**
     * .审批通过
     */
    @PostMapping("/success/{taskId}")
    public ResponseUtils success(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseUtils.error("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("approved", true);
        taskService.complete(taskId, map);
        return ResponseUtils.ok("流程审核通过！");
    }

    /**
     * .审批不通过
     */
    @PostMapping("/faile/{taskId}")
    public ResponseUtils faile(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseUtils.error("流程不存在");
        }
        //未通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("approved", false);
        taskService.complete(taskId, map);
        return ResponseUtils.ok();
    }

}
