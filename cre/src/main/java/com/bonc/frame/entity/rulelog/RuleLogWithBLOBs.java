package com.bonc.frame.entity.rulelog;

public class RuleLogWithBLOBs extends RuleLog {
    private String inputData;

    private String outputData;
    
    private String exception;
    private String ruleName;

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    
}