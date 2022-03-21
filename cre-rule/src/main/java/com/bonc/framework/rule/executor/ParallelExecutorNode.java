package com.bonc.framework.rule.executor;

import com.bonc.framework.api.log.entity.ConsumerInfo;
import com.bonc.framework.rule.constant.RuleEngineConstant;
import com.bonc.framework.rule.exception.ExecuteModelException;
import com.bonc.framework.rule.executor.builder.AbstractExecutorBuilder;
import com.bonc.framework.rule.executor.context.impl.ExecutorRequest;
import com.bonc.framework.rule.executor.context.impl.ExecutorResponse;
import com.bonc.framework.rule.log.IRuleLog;
import com.bonc.framework.rule.log.entity.TraceModelLogDetail;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.util.RuleEnginePropertiesUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author yedunyao
 * @date 2019/4/19 17:58
 */
public class ParallelExecutorNode implements ExecutorNode {
    private static Log log = LogFactory.getLog(ParallelExecutorNode.class);

    private static ExecutorService executorService;

    static {
        // TODO: 分离
        int threadNum = RuleEnginePropertiesUtil.getInt(
                RuleEngineConstant.PREFIX_THREADPOOL_THREADNUM);
        log.info("Parallel executor thread num: " + threadNum);

        executorService = Executors.newFixedThreadPool(threadNum);

        Runtime.getRuntime().addShutdownHook(new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        executorService.shutdown();
                    }
                }
        ));
    }

    private List<FlowNode> flowNodes;

    public ParallelExecutorNode(List<FlowNode> flowNodes) {
        this.flowNodes = flowNodes;
    }

    @Override
    @Deprecated
    public int executor(Map<String, Object> param, boolean isLog,
                        AbstractExecutorBuilder builder,
                        String logId, IRuleLog ruleLog,
                        ConsumerInfo... consumerInfo) throws Exception {
        log.info("Start to execute tasks in parallel, current tasks: " + flowNodes);
        long start = System.currentTimeMillis();
        // param在并行执行中的并发访问，改用ConcurrentHashMap
        Map<String, Object> parallelParam = new ConcurrentHashMap<>();
        parallelParam.putAll(param);
        final int size = flowNodes.size();
        List<Future<?>> futures = new ArrayList<>(size);
        try {
//            for (int i = 0; i < size; i++) {
//                ParallerTask task = new ParallerTask(flowNodes.get(i), param, isLog,
//                        builder, logId, ruleLog);
//                final Future<?> future = executorService.submit(task);
//                futures.add(future);
//            }
//
//            // 等待执行完成
//            for (Future<?> future : futures) {
//                future.get();
//            }
//            param.putAll(parallelParam);
        } catch (Exception e) {
            throw new ExecuteModelException(e);
        }
        log.info("End of parallel execution, used time: " + (System.currentTimeMillis() - start) + "ms");
        return 0;
    }

    @Override
    @Deprecated
    public int executor(Map<String, Object> param, boolean isLog,
                        AbstractExecutorBuilder builder,
                        String logId, IRuleLog ruleLog,
                        boolean isTraceDetail, TraceModelLogDetail traceModelLogDetail,
                        ConsumerInfo... consumerInfo) throws Exception {
        log.info("Start to execute tasks in parallel, current tasks: " + flowNodes);
        long start = System.currentTimeMillis();

        final int size = flowNodes.size();
        List<Future<Map<String, Object>>> futures = new ArrayList<>(size);
        try {
//            for (int i = 0; i < size; i++) {
//                ParallerTask task = new ParallerTask(flowNodes.get(i), param, isLog,
//                        builder, logId, ruleLog, isTraceDetail, traceModelLogDetail);
//                final Future<Map<String, Object>> future = executorService.submit(task);
//                futures.add(future);
//            }
//
//            // 等待执行完成
//            for (Future<Map<String, Object>> future : futures) {
//                Map<String, Object> result = future.get();
//                param.putAll(result);
//            }

        } catch (Exception e) {
            throw new ExecuteModelException(e);
        }
        log.info("End of parallel execution, used time: " + (System.currentTimeMillis() - start) + "ms");
        return 0;
    }

    @Override
    public int executor(AbstractExecutorBuilder builder, ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws Exception {
        log.info("Start to execute tasks in parallel, current tasks: " + flowNodes);
        long start = System.currentTimeMillis();
        // param在并行执行中的并发访问，改用ConcurrentHashMap
        final int size = flowNodes.size();
        List<Future<Map<String, Object>>> futures = new ArrayList<>(size);
        try {
            for (int i = 0; i < size; i++) {
                ParallerTask task = new ParallerTask(flowNodes.get(i),
                        builder, executorRequest, executorResponse);
                final Future<Map<String, Object>> future = executorService.submit(task);
                futures.add(future);
            }
            // 等待执行完成
            Map<String, Object> param = executorRequest.getParam();
            for (Future<Map<String, Object>> future : futures) {
                Map<String, Object> map = future.get();
                param.putAll(map);
            }
        } catch (Exception e) {
            throw new ExecuteModelException(e);
        }
        log.info("End of parallel execution, used time: " + (System.currentTimeMillis() - start) + "ms");
        return 0;
    }

    static class ParallerTask implements Callable<Map<String, Object>> {
        private Log log = LogFactory.getLog(getClass());

        private RealExecutorNode realExecutorNode;
        private FlowNode flowNode;

        private AbstractExecutorBuilder builder;

        private ExecutorRequest executorRequest;
        private ExecutorResponse executorResponse;

        // map 复制
        private Map<String, Object> cloneMap(Map<String, Object> param) {
            Map<String, Object> result = new HashMap<>(param.size());
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                // 因为输入的参数都是基本类型，直接put即可
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }


        public ParallerTask(FlowNode flowNode, AbstractExecutorBuilder builder,
                            ExecutorRequest executorRequest, ExecutorResponse executorResponse) throws ExecuteModelException {
            if (executorRequest == null) {
                throw new IllegalArgumentException("参数[executorRequest]不能为null");
            }
            this.flowNode = flowNode;
            this.builder = builder;
            this.realExecutorNode = RealExecutorNode.INSTANCE;
            try {
                this.executorRequest = executorRequest.clone();
            } catch (CloneNotSupportedException e) {
                throw new ExecuteModelException("执行并行节点失败:请求克隆失败", e);
            }
            this.executorResponse = executorResponse;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            long start = System.currentTimeMillis();
            realExecutorNode.executor(flowNode, builder, executorRequest, executorResponse);
            log.info("End of execution task, task: " + flowNode +
                    " used time: " + (System.currentTimeMillis() - start) + "ms");
            return executorRequest.getParam();
        }
    }

}
