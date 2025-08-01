package com.lab5.saga.domain;

public class SagaResult {
    private boolean success;
    private String message;
    private Object data;
    private SagaState finalState;

    public SagaResult() {}

    public SagaResult(boolean success, String message, Object data, SagaState finalState) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.finalState = finalState;
    }

    public static SagaResult success(Object data) {
        return new SagaResult(true, "Saga completed successfully", data, SagaState.COMPLETED);
    }

    public static SagaResult failed(String message) {
        return new SagaResult(false, message, null, SagaState.FAILED);
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public SagaState getFinalState() { return finalState; }
    public void setFinalState(SagaState finalState) { this.finalState = finalState; }
} 