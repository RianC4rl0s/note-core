package com.note_core.auth;

import com.note_core.auth.dto.LoginRequest;
import com.note_core.auth.dto.RegisterRequest;
import com.note_core.auth.dto.TokenResponse;
import com.note_core.common.exception.BusinessException;
import com.note_core.security.JwtTokenProvider;
import com.note_core.security.UserPrincipal;
import com.note_core.plan.Plan;
import com.note_core.plan.PlanRepository;
import com.note_core.user.Role;
import com.note_core.user.RoleRepository;
import com.note_core.user.User;
import com.note_core.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtDecoder jwtDecoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PlanRepository planRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       JwtDecoder jwtDecoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.planRepository = planRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtDecoder = jwtDecoder;
    }

    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String roles = principal.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.generateAccessToken(principal.getId(), principal.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal.getId());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already in use");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new BusinessException("Default role not found"));

        Plan freePlan = planRepository.findByName("FREE")
                .orElseThrow(() -> new BusinessException("Default plan not found"));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setBirthDate(request.birthDate());
        user.setRoles(Set.of(userRole));
        user.setPlan(freePlan);

        user = userRepository.save(user);

        String roles = "ROLE_USER";
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        String type = jwt.getClaimAsString("type");
        if (!"refresh".equals(type)) {
            throw new BusinessException("Invalid token type");
        }

        UUID userId = UUID.fromString(jwt.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        String roles = user.getRoles().stream()
                .flatMap(role -> {
                    var perms = role.getPermissions().stream().map(p -> p.getName());
                    var roleAuth = java.util.stream.Stream.of("ROLE_" + role.getName());
                    return java.util.stream.Stream.concat(roleAuth, perms);
                })
                .collect(Collectors.joining(","));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
