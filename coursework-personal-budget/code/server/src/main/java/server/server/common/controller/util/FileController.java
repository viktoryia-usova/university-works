package server.server.common.controller.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.server.common.dto.util.MessageResponse;
import server.server.common.service.util.FileService;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService service;

    @GetMapping("/{filename}")
    public ResponseEntity<?> get(@PathVariable String filename) {
        return ResponseEntity.ok(service.get(filename));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(new MessageResponse(service.save(file)));
    }
}
