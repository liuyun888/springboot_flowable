package com.boot.flowable.service;

import java.util.List;


public interface BackToStepService  {


    void rollback(String executionId, List<String> targetKeys);


    void backToStepFromParallelGateway(String proInstanceId,String targetTaskKey);

    void backToStepToParallelGateway(String processInstanceId);
}
