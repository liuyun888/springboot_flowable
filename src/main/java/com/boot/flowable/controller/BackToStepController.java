package com.boot.flowable.controller;

import com.boot.flowable.entity.RollBackVo;
import com.boot.flowable.service.BackToStepService;
import com.boot.flowable.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(value = "BackToStepController|一个用来测试流程驳回的控制器")
@RestController
@RequestMapping("/rollback")
public class BackToStepController {

    @Autowired
    private BackToStepService backToStepService;

    @Autowired
    private RuntimeService runtimeService;
    /**
     * .提交审批请求
     */
    @PostMapping("/start")
    @ApiOperation(value="提交审批请求", notes="流程key目前为写死的，可以改为参数传入")
    public ResponseUtils startFlow() {
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("rollback");
        String processId = processInstance.getId();
        String name = processInstance.getName();
        System.out.println(processId + ":" + name);
        return ResponseUtils.ok(processId + ":" + name);
    }

    /**
     *  根据executionId驳回到单个/多个目标key
     *  executionId        the id of an execution
     *  targetTaskDefKeys  目标节点keys
     * @return
     */
    @RequestMapping(value = "/rollback",method = RequestMethod.POST,produces = "application/json")
    @ApiOperation(value="根据executionId驳回到单个/多个目标key", notes="参数为 executionId  不是 processInstanceId")
    public ResponseUtils rollback(@RequestBody RollBackVo rollBackVo) {

        backToStepService.rollback(rollBackVo.getExecutionId(),rollBackVo.getTargetTaskDefKeys());

        return ResponseUtils.ok();
    }

    /**
     * 并行网关的驳回到单一节点
     * @param processInstanceId  当前进程id
     * @param targetTaskDefKey  目标节点key
     * @return
     */
    @PostMapping("/backToStepFromParallelGateway/{processInstanceId}/{targetTaskDefKey}")
    @ApiOperation(value="并行网关的驳回到单一节点", notes="参数为 processInstanceId，targetTaskDefKey")
    public ResponseUtils backToStepFromParallelGateway(@PathVariable String processInstanceId, @PathVariable String targetTaskDefKey){

        backToStepService.backToStepFromParallelGateway(processInstanceId,  targetTaskDefKey);

        return ResponseUtils.ok();
    }

    /**
     * 退回到并行网关某一节点的驳回
     * @param  processInstanceId  当前进程id
     * @return
     */
    @PostMapping("/backToStepToParallelGateway/{processInstanceId}")
    @ApiOperation(value="退回到并行网关某一节点的驳回", notes="参数为 processInstanceId")
    public ResponseUtils backToStepToParallelGateway(@PathVariable String processInstanceId){

        backToStepService.backToStepToParallelGateway(processInstanceId);

        return ResponseUtils.ok();
    }

}
