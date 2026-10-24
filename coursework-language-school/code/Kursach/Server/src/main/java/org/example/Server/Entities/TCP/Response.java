package org.example.Server.Entities.TCP;

public class Response {
    private String message;
    private RequestType responseType;
    private Boolean success;

    public Response() {}

    public Response(String response, RequestType responseType,Boolean success) {
        this.message = response;
        this.responseType = responseType;
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public RequestType getResponseType() {
        return responseType;
    }

    public void setResponseType(RequestType responseType) {
        this.responseType = responseType;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
