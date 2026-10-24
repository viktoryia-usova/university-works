package org.example.TCP;


public class Request {
    private RequestType requestType;
    private String message;
    private Boolean success;

    public Request(){}

    public Request(RequestType requestType, String request) {
        this.requestType = requestType;
        this.message = request;
        this.success = true;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String request) {
        this.message = request;
    }
}
