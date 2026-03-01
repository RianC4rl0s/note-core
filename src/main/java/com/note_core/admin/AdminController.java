package com.note_core.admin;

import com.note_core.admin.dto.AssignPermissionsRequest;
import com.note_core.admin.dto.AssignRolesRequest;
import com.note_core.admin.dto.CreatePermissionRequest;
import com.note_core.admin.dto.CreateRoleRequest;
import com.note_core.admin.dto.PermissionResponse;
import com.note_core.admin.dto.RoleResponse;
import com.note_core.admin.dto.UpdateRoleRequest;
import com.note_core.auth.dto.ImpersonateRequest;
import com.note_core.auth.dto.TokenResponse;
import com.note_core.plan.PlanService;
import com.note_core.plan.dto.AssignPlanRequest;
import com.note_core.security.ImpersonationService;
import com.note_core.security.PreAuth;
import com.note_core.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ImpersonationService impersonationService;
    private final AdminService adminService;
    private final PlanService planService;

    public AdminController(ImpersonationService impersonationService,
                           AdminService adminService,
                           PlanService planService) {
        this.impersonationService = impersonationService;
        this.adminService = adminService;
        this.planService = planService;
    }

    @PostMapping("/impersonate")
    @PreAuthorize(PreAuth.ADMIN_IMPERSONATE)
    public ResponseEntity<TokenResponse> impersonate(JwtAuthenticationToken auth,
                                                     @Valid @RequestBody ImpersonateRequest request) {
        UUID impersonatorId = UUID.fromString(auth.getToken().getSubject());
        return ResponseEntity.ok(impersonationService.impersonate(impersonatorId, request.targetUserId()));
    }

    // ---- Roles ----

    @GetMapping("/roles")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<List<RoleResponse>> listRoles() {
        return ResponseEntity.ok(adminService.findAllRoles());
    }

    @PostMapping("/roles")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createRole(request));
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.findRoleById(id));
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(adminService.updateRole(id, request));
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        adminService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/roles/{id}/permissions")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<RoleResponse> assignPermissionsToRole(@PathVariable Long id,
                                                                @Valid @RequestBody AssignPermissionsRequest request) {
        return ResponseEntity.ok(adminService.assignPermissionsToRole(id, request.permissionIds()));
    }

    // ---- Permissions ----

    @GetMapping("/permissions")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<List<PermissionResponse>> listPermissions() {
        return ResponseEntity.ok(adminService.findAllPermissions());
    }

    @PostMapping("/permissions")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createPermission(request));
    }

    @PutMapping("/permissions/{id}")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable Long id,
                                                               @Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(adminService.updatePermission(id, request));
    }

    @DeleteMapping("/permissions/{id}")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        adminService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    // ---- User-Role assignment ----

    @PutMapping("/users/{id}/roles")
    @PreAuthorize(PreAuth.IS_ADMIN)
    public ResponseEntity<UserResponse> assignRolesToUser(@PathVariable UUID id,
                                                         @Valid @RequestBody AssignRolesRequest request) {
        return ResponseEntity.ok(adminService.assignRolesToUser(id, request.roleIds()));
    }

    // ---- User-Plan assignment ----

    @PutMapping("/users/{id}/plan")
    @PreAuthorize(PreAuth.PLAN_ASSIGN)
    public ResponseEntity<UserResponse> assignPlanToUser(@PathVariable UUID id,
                                                        @Valid @RequestBody AssignPlanRequest request) {
        return ResponseEntity.ok(planService.assignPlanToUser(id, request.planId()));
    }
}
