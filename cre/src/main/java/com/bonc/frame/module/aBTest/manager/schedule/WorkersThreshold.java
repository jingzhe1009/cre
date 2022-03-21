package com.bonc.frame.module.aBTest.manager.schedule;

/**
 * @author yedunyao
 * @since 2020/9/25 16:41
 */
public enum WorkersThreshold {

    ALL("all"), HALF("half");

    private String value;

    WorkersThreshold(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WorkersThreshold getWorkersThreshold(String value) {
        WorkersThreshold[] values = WorkersThreshold.values();
        for (WorkersThreshold workersThreshold : values) {
            if (workersThreshold.getValue().equalsIgnoreCase(value)) {
                return workersThreshold;
            }
        }
        return WorkersThreshold.ALL;
    }
}
