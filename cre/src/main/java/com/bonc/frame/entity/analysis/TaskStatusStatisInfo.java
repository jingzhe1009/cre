package com.bonc.frame.entity.analysis;

public class TaskStatusStatisInfo {
    private String status;
    private String count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if(status == null){
            return;
        }
        switch (status) {
            case "0" :
                this.status = "disEnable";
                break;
            case "1" :
                this.status = "enable";
                break;
            case "2" :
                this.status = "execute";
                break;
            case "4" :
                this.status = "execute";
                break;
            case "5" :
                this.status = "success";
                break;
            case "-1" :
                this.status = "exception";
                break;
            case "-2" :
                this.status = "exception";
                break;
            default:
                this.status = status;
        }
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskStatis{");
        sb.append("status='").append(status).append('\'');
        sb.append(", count='").append(count).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
