package com.boot.flowable.delegate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.impl.SLF4JLog;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: ReviewApprove
 * Description:
 *
 */
@Slf4j
public class ReviewApprove implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ReviewApprove.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        //可以发送消息给某人
        log.info("通过，userId是：{}",  delegateExecution.getVariable("userId"));
    }
}
