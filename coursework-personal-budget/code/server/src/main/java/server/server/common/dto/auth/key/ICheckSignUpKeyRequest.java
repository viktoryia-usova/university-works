package server.server.common.dto.auth.key;


public interface ICheckSignUpKeyRequest {
    String getUsername();
    String getPassword();
    String getKey();
}
