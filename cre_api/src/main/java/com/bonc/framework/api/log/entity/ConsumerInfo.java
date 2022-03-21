package com.bonc.framework.api.log.entity;

import com.google.common.base.Objects;

/**
 * @author yedunyao
 * @date 2019/8/6 11:47
 */
public class ConsumerInfo {

    /**
     * 渠道标识
     */
    protected String consumerId;

    /**
     * 接口标识
     */
    protected String serverId;

    /**
     * 流水号
     */
    protected String consumerSeqNo;

    public ConsumerInfo() {
    }

    public ConsumerInfo(String consumerId, String serverId, String consumerSeqNo) {
        this.consumerId = consumerId;
        this.serverId = serverId;
        this.consumerSeqNo = consumerSeqNo;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getConsumerSeqNo() {
        return consumerSeqNo;
    }

    public void setConsumerSeqNo(String consumerSeqNo) {
        this.consumerSeqNo = consumerSeqNo;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("consumerId", consumerId)
                .add("serverId", serverId)
                .add("consumerSeqNo", consumerSeqNo)
                .toString();
    }
}
