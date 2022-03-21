package com.bonc.framework.rule.log.entity;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 模型执行详情
 *
 * @author yedunyao
 * @since 2019/12/10 19:47
 */
public class TraceModelLogDetail {

    /**
     * 模型执行日志
     */
    private RuleLog ruleLog;

    /**
     * 模型执行明细日志
     */
    private Queue<RuleLogDetail> ruleLogDetailQueue;

    public TraceModelLogDetail(boolean isConcurrency) {
        if (isConcurrency) {
            ruleLogDetailQueue = new ConcurrentLinkedQueue();
        } else {
            ruleLogDetailQueue = new LinkedList<>();
        }
    }

    public void addRuleLogDetail(RuleLogDetail ruleLogDetail) {
        if (ruleLogDetail != null) {
            ruleLogDetailQueue.offer(ruleLogDetail);
        }
    }

    public RuleLogDetail[] toArray() {
        return (RuleLogDetail[]) ruleLogDetailQueue.toArray();
    }

    public RuleLog getRuleLog() {
        return ruleLog;
    }

    public void setRuleLog(RuleLog ruleLog) {
        this.ruleLog = ruleLog;
    }

    public Queue<RuleLogDetail> getRuleLogDetailQueue() {
        return ruleLogDetailQueue;
    }

    public void setRuleLogDetailQueue(Queue<RuleLogDetail> ruleLogDetailQueue) {
        this.ruleLogDetailQueue = ruleLogDetailQueue;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TraceModelLogDetail{");
        sb.append("ruleLog=").append(ruleLog);
        sb.append(", ruleLogDetailQueue=").append(ruleLogDetailQueue);
        sb.append('}');
        return sb.toString();
    }
}
