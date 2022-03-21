package com.bonc.framework.rule.executor.worker.impl;

import com.bonc.framework.rule.executor.worker.ITraversal;
import com.bonc.framework.rule.resources.flow.FlowNode;
import com.bonc.framework.rule.resources.flow.FlowNodeFactory;
import com.bonc.framework.rule.resources.flow.basicflow.AbstractFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.PathFlowNode;
import com.bonc.framework.rule.resources.flow.basicflow.impl.ConvergeFlowNode;
import com.bonc.framework.rule.resources.flow.util.FlowNodeUtil;

import java.util.*;

/**
 * 遍历思路：广度优先搜索思想，一层一层的遍历。
 * 1.解决重复遍历节点问题
 * 将当前层的子节点放到Map中，每放一个，判定是否已经存在，若存在，则不重复放。
 *
 * @author qxl
 * @version 1.0
 * @date 2017年8月30日 上午11:46:52
 */
public class DefaultTraversal implements ITraversal {
//	private Log log = LogFactory.getLog(getClass());

    private FlowNode preNode;//上一个节点

    private FlowNode curNode;//当前节点

    private FlowNode fromNode;//来源节点

    private FlowNode nextNode;//下一节点

    private FlowNode headNode;//头节点

    private Queue<FlowNode> queue;//待遍历节点的队列

//	private Map<String,FlowNode> nodeMap;//当前层的所有子节点

    private List<FlowNode> traversalLog;//遍历的历史记录

    //private Stack<FlowNode> stack;//待执行的聚合 栈

    private Queue<FlowNode> forkQueue;//待执行的聚合 优先队列，深度小的优先出队

    private List<FlowNode> nodeList;

    private boolean hasTraversal = false;

    private boolean hasNext;//是否还有下一个节点

    private boolean isElseValue = false;

    public DefaultTraversal() {
    }

    public DefaultTraversal(FlowNode node) {
        this.headNode = node;
        init();
    }

    private void init() {
        queue = new LinkedList<FlowNode>();
//		nodeMap = new HashMap<String,FlowNode>();
        traversalLog = new ArrayList<FlowNode>();
        nodeList = new ArrayList<FlowNode>();
        //stack = new Stack<FlowNode>();
        //优先队列
        forkQueue = new PriorityQueue<>(16, new Comparator<FlowNode>() {
            @Override
            public int compare(FlowNode o1, FlowNode o2) {
                return o1.getMaxNodeDeep() - o2.getMaxNodeDeep();
            }
        });

        //将开始节点入队
        nextNode = headNode;
        hasNext = true;
        // 广度优先更新每个节点最大高度
        FlowNodeUtil.setNodeDeep(headNode);
    }

    @Override
    public void setHeadNode(FlowNode flowNode) {
        this.headNode = flowNode;
        init();
    }

    @Override
    public FlowNode preNode() throws Exception {
        return preNode;
    }

    @Override
    public FlowNode fromNode() throws Exception {
        List<FlowNode> parentNode = this.curNode.getParentFlowNodes();
        for (FlowNode node : traversalLog) {
            if (parentNode.contains(node)) {
                fromNode = node;
            }
        }
        return fromNode;
    }

    @Override
    public FlowNode curNode() throws Exception {
//		nextNode = queue.poll();//从待遍历的队列中拿出一个节点
//		if(curNode != null){
//			//nodeMap.remove(curNode.getNodeId());//移除
//			traversalLog.add(curNode);//记录遍历日志
//		}
        return curNode;
    }

    /**
     * 调用此方法时说明curNode的执行后状态为true
     *
     * @return
     * @throws Exception
     */
    @Override
    public FlowNode nextNode() throws Exception {
        offerChildNode();
        if (curNode != null) {
            String nodeType = ((AbstractFlowNode) curNode).getNodeType();
            if (FlowNodeFactory.TYPE_PATH.equals(nodeType)) {
                PathFlowNode pfn = (PathFlowNode) curNode;
                boolean isElse = pfn.isElse();
                if (!isElse) {
                    // 如果当前节点为PathFlowNode，且不是else分支
                    this.isElseValue = true;
                }
            }
        }
        return nextBroNode();
    }

    /**
     * 将子节点入队
     */
    private void offerChildNode() {
        if (curNode == null) {//当前节点为空，则无子节点
            return;
        }
        List<FlowNode> childNodes = curNode.getChildFlowNodes();
        if (childNodes == null || childNodes.size() == 0) {
            return;
        }
        String nodeType = ((AbstractFlowNode) curNode).getNodeType();
        if (FlowNodeFactory.TYPE_FORK.equals(nodeType)) {
            // 将else分支放到最后
            Collections.sort(childNodes, new Comparator<FlowNode>() {
                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 *
                 * 比较PathFlowNode，
                 * 如果其中一个不是PathFlowNode的示例，
                 * 则认为相等（不进行比较）；
                 */
                public int compare(FlowNode o1, FlowNode o2) {
                    PathFlowNode pfn1 = null;
                    PathFlowNode pfn2 = null;
                    if (o1 instanceof PathFlowNode) {
                        pfn1 = (PathFlowNode) o1;
                    }
                    if (o2 instanceof PathFlowNode) {
                        pfn2 = (PathFlowNode) o2;
                    }
                    if (pfn1 == null || pfn2 == null) {
                        return 0;
                    }
                    if (!pfn1.isElse() && pfn2.isElse()) {
                        return -1;
                    }
                    return 1;
                }
            });
//			System.out.println(childNodes);
        }
        for (FlowNode childNode : childNodes) {
            push(childNode);//放入队列
            //判断是否已经遍历过此节点，防止重复遍历
//			if(!nodeMap.containsKey(childNode.getNodeId())){
//				nodeMap.put(childNode.getNodeId(), childNode);
//			}
        }
    }

    @Override
    public FlowNode nextBroNode() throws Exception {
        if (nextNode == null) {
            nextNode = poll();//从队列中取出下一个节点
        }
//		System.out.println("....hasNext:"+hasNext()+" , isWait:"+isWait()+"...");
        if (hasNext() && isWait()) {//针对下一节点为聚合节点的特殊处理
            preNode = curNode;//将当前节点赋值给前一节点
            curNode = nextNode;//将下一节点赋值给当前节点返回
            nextNode = poll();//从队列中取出下一个节点
//			System.out.println("......curNode:"+curNode);
            return nextBroNode();
        } else {
            preNode = curNode;//将当前节点赋值给前一节点
            curNode = nextNode;//将下一节点赋值给当前节点返回
            nextNode = poll();//从队列中取出下一个节点
        }
//		log.info("traversal history :"+traversalLog);
        traversalLog.add(curNode);//记录遍历日志
//		System.out.println("traversal history :"+traversalLog);
        // 在未执行前，判断是否跳过else分支
        if (curNode != null) {
            String nodeType = ((AbstractFlowNode) curNode).getNodeType();
//			if(FlowNodeFactory.TYPE_FORK.equals(nodeType)) {
//				this.isElseValue = false;
//			}
            if (FlowNodeFactory.TYPE_PATH.equals(nodeType)) {
                PathFlowNode pfn = (PathFlowNode) curNode;
                boolean isElse = pfn.isElse();
                if (isElseValue && isElse) {
                    // 跳过else分支
                    return nextBroNode();
                }
            } else {
                this.isElseValue = false;
            }
        }

        return curNode;
    }

    //判断  聚合的等待
    private boolean isWait() {
        if (nextNode instanceof ConvergeFlowNode) {//聚合，判断是否执行过聚合
            if (traversalLog.contains(nextNode)) {
                return true;
            }
            if (!queue.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean hasNext() {
        if (nextNode == null) {
            hasNext = false;
        } else {
            hasNext = true;
        }
        return hasNext;
    }

    //存入待遍历的节点
    private void push(FlowNode node) {
        if (node instanceof ConvergeFlowNode) {//聚合，放到栈中
//			stack.push(node);
            forkQueue.add(node);
        } else {
            queue.offer(node);
        }
    }

    //从待遍历节点列表中取出一个
    private FlowNode poll() {
        FlowNode node = queue.poll();
        if (node == null && !forkQueue.isEmpty()) {
//			node = stack.pop();
            node = forkQueue.poll();
        }
        return node;
    }

    @Override
    public List<FlowNode> getList() throws Exception {
        if (hasTraversal) {//判断是否遍历过
            return nodeList;
        }
        hasTraversal = true;
        FlowNode currectFlowNode = null;
        Queue<FlowNode> queue = new LinkedList<FlowNode>();//节点对应的队列
        queue.offer(headNode);//将开始节点入队
        Map<String, FlowNode> nodeMap = new HashMap<String, FlowNode>();//处理过的节点
        while ((currectFlowNode = queue.poll()) != null) {
            if (nodeMap.containsKey(currectFlowNode.getNodeId())) {
                //已处理过此节点
                continue;
            }
            String nodeType = ((AbstractFlowNode) currectFlowNode).getNodeType();
            if (FlowNodeFactory.TYPE_FORK.equals(nodeType)) {
                nodeList = null;
                return nodeList;
            }
            nodeList.add(currectFlowNode);
            //获取当前节点的所有孩子节点
            List<FlowNode> childNodesList = currectFlowNode.getChildFlowNodes();
            if (childNodesList != null && childNodesList.size() > 1) {
                nodeList = null;
                return nodeList;
            }
            if (childNodesList != null && childNodesList.size() == 1) {
                queue.offer(childNodesList.get(0));//将孩子节点入队，等待处理
            }
            //将处理过的节点存到map中，防止重复处理
            nodeMap.put(currectFlowNode.getNodeId(), currectFlowNode);
        }
        return nodeList;
    }

    @Override
    public boolean canParallel() {
        return false;
    }

    @Override
    public List<FlowNode> pollNodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void end() {

    }
}
