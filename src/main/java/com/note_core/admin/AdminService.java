package com.note_core.admin;

import com.note_core.admin.dto.CreatePermissionRequest;
import com.note_core.admin.dto.CreateRoleRequest;
import com.note_core.admin.dto.PermissionResponse;
import com.note_core.admin.dto.RoleResponse;
import com.note_core.admin.dto.UpdateRoleRequest;
import com.note_core.common.exception.BusinessException;
import com.note_core.common.exception.ResourceNotFoundException;
import com.note_core.user.Permission;
import com.note_core.user.PermissionRepository;
import com.note_core.user.Role;
import com.note_core.user.RoleRepository;
import com.note_core.user.User;
import com.note_core.user.UserRepository;
import com.note_core.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public AdminService(RoleRepository roleRepository,
                        PermissionRepository permissionRepository,
                        UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    // ---- Roles ----

    @Transactional(readOnly = true)
    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse findRoleById(Long id) {
        return roleRepository.findById(id)
                .map(RoleResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new BusinessException("Role name already exists");
        }

        Instant now = Instant.now();
        Role role = new Role();
        role.setName(request.name());
        role.setDescription(request.description());
        role.setBuiltIn(false);
        role.setCreatedAt(now);
        role.setUpdatedAt(now);

        return RoleResponse.from(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (role.isBuiltIn()) {
            throw new BusinessException("Built-in roles cannot be edited");
        }

        if (request.name() != null) {
            if (!request.name().equals(role.getName()) && roleRepository.existsByName(request.name())) {
                throw new BusinessException("Role name already exists");
            }
            role.setName(request.name());
        }
        if (request.description() != null) {
            role.setDescription(request.description());
        }
        role.setUpdatedAt(Instant.now());

        return RoleResponse.from(roleRepository.save(role));
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (role.isBuiltIn()) {
            throw new BusinessException("Built-in roles cannot be deleted");
        }

        roleRepository.delete(role);
    }

    @Transactional
    public RoleResponse assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (permissions.size() != permissionIds.size()) {
            throw new BusinessException("One or more permissions not found");
        }

        role.setPermissions(permissions);
        role.setUpdatedAt(Instant.now());
        return RoleResponse.from(roleRepository.save(role));
    }

    // ---- Permissions ----

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::from)
                .toList();
    }

    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        if (permissionRepository.existsByName(request.name())) {
            throw new BusinessException("Permission name already exists");
        }

        Instant now = Instant.now();
        Permission permission = new Permission();
        permission.setName(request.name());
        permission.setDescription(request.description());
        permission.setBuiltIn(false);
        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);

        return PermissionResponse.from(permissionRepository.save(permission));
    }

    @Transactional
    public PermissionResponse updatePermission(Long id, CreatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        if (permission.isBuiltIn()) {
            throw new BusinessException("Built-in permissions cannot be edited");
        }

        if (request.name() != null) {
            if (!request.name().equals(permission.getName()) && permissionRepository.existsByName(request.name())) {
                throw new BusinessException("Permission name already exists");
            }
            permission.setName(request.name());
        }
        if (request.description() != null) {
            permission.setDescription(request.description());
        }
        permission.setUpdatedAt(Instant.now());

        return PermissionResponse.from(permissionRepository.save(permission));
    }

    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        if (permission.isBuiltIn()) {
            throw new BusinessException("Built-in permissions cannot be deleted");
        }

        permissionRepository.delete(permission);
    }

    // ---- User-Role assignment ----

    @Transactional
    public UserResponse assignRolesToUser(UUID userId, Set<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new BusinessException("One or more roles not found");
        }

        user.setRoles(roles);
        return UserResponse.from(userRepository.save(user));
    }
}
