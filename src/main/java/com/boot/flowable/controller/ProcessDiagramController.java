package com.boot.flowable.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Api(value = "ProcessDiagramController|生成运行节点流程图")
@RestController
@RequestMapping("/processDiagram")
public class ProcessDiagramController {
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
     * 展示流程图
     * @param httpServletResponse
     * @param processId
     * @throws Exception
     */
    @RequestMapping(value = "singleNode")
    @ApiOperation(value="单节点展示流程图", notes="当前运行时为单节点")
    public void singleNode(HttpServletResponse httpServletResponse, String processId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        genProcessDiagram(httpServletResponse, pi, activityIds, flows);
    }

    /**
     *  多节点展示流程图
     * @param httpServletResponse
     * @param processId
     * @throws Exception
     */
    @RequestMapping(value = "pluralNode")
    @ApiOperation(value="多节点展示流程图", notes="当前运行时为多节点")
    public void pluralNode(HttpServletResponse httpServletResponse, String processId) throws Exception{
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        //查询多个节点任务
        List<Task> list = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        System.out.println("lists is="+list.toString());
        for (Task task : list) {
            //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
            String InstanceId = task.getProcessInstanceId();
            List<Execution> executions = runtimeService
                    .createExecutionQuery()
                    .processInstanceId(InstanceId)
                    .list();


            for (Execution exe : executions) {
                List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
                activityIds.addAll(ids);
            }
        }
        //获取流程图
        genProcessDiagram(httpServletResponse, pi, activityIds, flows);

    }

    private void genProcessDiagram(HttpServletResponse httpServletResponse, ProcessInstance pi, List<String> activityIds, List<String> flows) throws IOException {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
