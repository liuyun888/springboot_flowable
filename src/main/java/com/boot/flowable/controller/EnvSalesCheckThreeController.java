package com.boot.flowable.controller;

import com.boot.flowable.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Api(value = "EnvSalesCheckThreeController|环境审批测试，内含排他网关")
@RestController
@RequestMapping("/envSalesCheckThree")
public class EnvSalesCheckThreeController {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngine processEngine;


    /**
     * .提交采购订单的审批请求
     *
     * @param userId 用户id
     */
    @PostMapping("/start/{userId}")
    @ApiOperation(value="提交采购订单的审批请求", notes="流程key目前为写死的，可以改为参数传入")
    public ResponseUtils startFlow(@PathVariable String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("envSalesThreeCheck", map);
        String processId = processInstance.getId();
        String name = processInstance.getName();
        System.out.println(processId + ":" + name);
        return ResponseUtils.ok(processId + ":" + name);
    }

    /**
     * 一级审批
     */
    @PostMapping("/oneCheck/{taskId}/{isPass}")
    @ApiOperation(value="一级审批", notes="根据路由条件确认走向")
    public ResponseUtils oneCheck(@PathVariable String taskId, @PathVariable boolean isPass) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseUtils.error("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
        if (isPass) {
            //通过审核
            map.put("oneCheckBack", false);
            map.put("oneCheckPass", true);


        } else {
            //未通过审核
            map.put("oneCheckBack", true);
            map.put("oneCheckPass", false);
        }

        taskService.complete(taskId, map);
        return ResponseUtils.ok("一级审批通过！");
    }

    /**
     * 二级审批
     */
    @PostMapping("/twoCheck/{taskId}/{isPass}")
    @ApiOperation(value="二级审批", notes="根据路由条件确认走向")
    public ResponseUtils twoCheck(@PathVariable String taskId, @PathVariable boolean isPass) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseUtils.error("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
        if (isPass) {
            //通过审核
            map.put("twoCheckBack", false);
            map.put("twoCheckPass", true);


        } else {
            //未通过审核
            map.put("twoCheckBack", true);
            map.put("twoCheckPass", false);
        }

        taskService.complete(taskId, map);
        return ResponseUtils.ok("二级审批通过！");
    }


    /**
     * .获取审批组的任务
     *@param groupId 审批组id
     */
    @GetMapping("/getGroupTasks/{groupId}")
    @ApiOperation(value="获取审批组的任务", notes="参数： groupId")
    public ResponseUtils getGroupTasks(@PathVariable String groupId) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(groupId).list();
        System.out.println("You have "+tasks.size()+" tasks:");
        for(int i = 0; i<tasks.size();i++) {
            System.out.println((i + 1) + ") " + tasks.get(i).getName());
        }
        return ResponseUtils.ok(tasks.toString());
    }
}
