package server.server.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import server.server.business.dto.admin.UserResponse;
import server.server.business.facade.admin.AdminFacade;

@Tag(name = "Admin", description = "Управление пользователями (только ADMIN)")
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final AdminFacade adminFacade;

    public AdminController(AdminFacade adminFacade) {
        this.adminFacade = adminFacade;
    }

    @Operation(summary = "Список всех пользователей (USER роль)")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(adminFacade.getUsers(pageable));
    }

    @Operation(summary = "Заблокировать пользователя")
    @PostMapping("/users/{id}/block")
    public ResponseEntity<UserResponse> block(@PathVariable Long id) {
        return ResponseEntity.ok(adminFacade.blockUser(id));
    }

    @Operation(summary = "Разблокировать пользователя")
    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<UserResponse> unblock(@PathVariable Long id) {
        return ResponseEntity.ok(adminFacade.unblockUser(id));
    }
}
