package com.bonc.frame.module.aBTest.manager.schedule;

/**
 * @author yedunyao
 * @since 2020/9/21 15:46
 */
public enum  SchedulePolicy {

    FIFO("FIFO"), RANDOM("RANDOM");

    private String policy;

    SchedulePolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicy() {
        return policy;
    }

    public SchedulePolicy getSchedulePolicy(String value) {
        SchedulePolicy[] values = SchedulePolicy.values();
        for (SchedulePolicy schedulePolicy : values) {
            if (schedulePolicy.getPolicy().equals(value)) {
                return schedulePolicy;
            }
        }
        return SchedulePolicy.FIFO;
    }

}
