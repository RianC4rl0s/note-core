package com.note_core.security;

import com.note_core.auth.dto.TokenResponse;
import com.note_core.common.exception.BusinessException;
import com.note_core.common.exception.ResourceNotFoundException;
import com.note_core.user.User;
import com.note_core.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImpersonationService {

    private static final Set<String> ADMIN_ROLES = Set.of("ADMIN", "SUPER_ADMIN");

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ImpersonationService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public TokenResponse impersonate(UUID impersonatorId, UUID targetUserId) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        boolean targetIsAdmin = target.getRoles().stream()
                .anyMatch(role -> ADMIN_ROLES.contains(role.getName()));
        if (targetIsAdmin) {
            throw new BusinessException("Cannot impersonate admin users");
        }

        String roles = target.getRoles().stream()
                .flatMap(role -> {
                    var perms = role.getPermissions().stream().map(p -> p.getName());
                    var roleAuth = java.util.stream.Stream.of("ROLE_" + role.getName());
                    return java.util.stream.Stream.concat(roleAuth, perms);
                })
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.generateAccessToken(
                target.getId(), target.getEmail(), roles, impersonatorId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(target.getId());

        return new TokenResponse(accessToken, refreshToken);
    }
}
