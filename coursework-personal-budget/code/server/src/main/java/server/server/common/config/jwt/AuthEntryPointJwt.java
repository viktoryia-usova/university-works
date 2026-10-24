package server.server.common.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import server.server.common.dto.util.ErrorMessageResponse;
import server.server.common.util.log.AppLogger;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final String LOG = AppLogger.name(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        AppLogger.warn("Unauthorized access: " + request.getServletPath() + " - " + e.getMessage(), LOG);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        new ObjectMapper().writeValue(
                response.getOutputStream(),
                new ErrorMessageResponse("UNAUTHORIZED", e.getMessage())
        );
    }
}
