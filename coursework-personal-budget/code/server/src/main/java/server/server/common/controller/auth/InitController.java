package server.server.common.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.common.dto.auth.init.InitRequest;
import server.server.common.dto.util.MessageResponse;
import server.server.common.service.auth.BaseInitService;

@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
public class InitController {

    private final BaseInitService service;

    @GetMapping
    public ResponseEntity<MessageResponse> initDefault() {
        service.init(new InitRequest("admin@admin.com", "admin"));
        return ResponseEntity.ok(new MessageResponse("Server initialized"));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> init(@Valid @RequestBody InitRequest request) {
        service.init(request);
        return ResponseEntity.ok(new MessageResponse("Server initialized"));
    }
}
