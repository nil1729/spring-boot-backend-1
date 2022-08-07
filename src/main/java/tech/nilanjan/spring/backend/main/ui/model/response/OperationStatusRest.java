package tech.nilanjan.spring.backend.main.ui.model.response;

public class OperationStatusRest {
    private String operationName;
    private String operationStatus;

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getOperationStatus() {
        return operationStatus;
    }
}
