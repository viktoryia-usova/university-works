package server.server.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.server.common.dto.util.ErrorMessageResponse;
import server.server.common.exception.auth.AuthException;
import server.server.common.exception.common.EntityNotFoundException;
import server.server.common.exception.common.ExternalApiException;
import server.server.common.util.log.AppLogger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidation(MethodArgumentNotValidException ex) {
        AppLogger.warn("Validation failed: " + ex.getFieldErrors().size() + " error(s)", "GlobalExceptionHandler");
        var firstError = ex.getFieldErrors().get(0);
        return ResponseEntity.badRequest().body(new ErrorMessageResponse(
                firstError.getField().replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase(),
                firstError.getDefaultMessage()
        ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotFound(EntityNotFoundException ex) {
        AppLogger.warn(ex.getMessage(), "GlobalExceptionHandler");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageResponse(ex.getKey(), ex.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuth(AuthException ex) {
        AppLogger.warn(ex.getMessage(), "GlobalExceptionHandler");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessageResponse(ex.getKey(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessageResponse("ACCESS_DENIED", ex.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorMessageResponse> handleExternalApi(ExternalApiException ex) {
        AppLogger.error(ex.getMessage(), "GlobalExceptionHandler");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorMessageResponse(ex.getKey(), ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessageResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        AppLogger.warn("Data integrity violation: " + ex.getMostSpecificCause().getMessage(), "GlobalExceptionHandler");
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessageResponse("DATA_INTEGRITY_VIOLATION", "Запись с такими данными уже существует"));
    }

    @ExceptionHandler(BaseKeyException.class)
    public ResponseEntity<ErrorMessageResponse> handleBaseKey(BaseKeyException ex) {
        AppLogger.error(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.badRequest()
                .body(new ErrorMessageResponse(ex.getKey(), ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorMessageResponse> handleUnsupported(UnsupportedOperationException ex) {
        AppLogger.error(ex.getMessage(), "GlobalExceptionHandler");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageResponse("UNSUPPORTED", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessageResponse> handleRuntime(RuntimeException ex) {
        AppLogger.error(ex.getMessage(), ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageResponse("UNHANDLED", ex.getMessage()));
    }
}
