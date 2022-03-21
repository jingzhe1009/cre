package com.bonc.frame.entity.analysis;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ModelExecuteCountInfo {
    private String startDate;
    private String enddate;

    Map<String,String> statesCount; // key=state  value=count

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExecuteCountInfo{");
        sb.append("startDate=").append(startDate);
        sb.append(", enddate=").append(enddate);
        sb.append(", statesCount=").append(statesCount);
        sb.append('}');
        return sb.toString();
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Map<String, String> getStatesCount() {
        return statesCount;
    }

    public void setStatesCount(Map<String, String> statesCount) {
        this.statesCount = statesCount;
    }

    public void  setStatesCount(String state,String count){
        if(StringUtils.isBlank(state) || StringUtils.isBlank(count)){
            return;
        }
        if(statesCount == null ){
            statesCount = new HashMap<>();
        }
        switch (state){
            case "1" :
                statesCount.put("executeCount",count);
                break;
            case "2" :
                statesCount.put("successCount",count);
                break;
            case "-1" :
                statesCount.put("falseCount",count);
                break;
            case "allCount" :
                statesCount.put("allCount",count);
                break;
        }
    }

//    class State{
//        String state;
//        String count;
//
//        @Override
//        public String toString() {
//            final StringBuilder sb = new StringBuilder("State{");
//            sb.append("state='").append(state).append('\'');
//            sb.append(", count='").append(count).append('\'');
//            sb.append('}');
//            return sb.toString();
//        }
//
//        public String getState() {
//            return state;
//        }
//
//        public void setState(String state) {
//            this.state = state;
//        }
//
//        public String getCount() {
//            return count;
//        }
//
//        public void setCount(String count) {
//            this.count = count;
//        }
//    }
}
