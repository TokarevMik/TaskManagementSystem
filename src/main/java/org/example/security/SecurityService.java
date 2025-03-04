package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.AlreadyExistException;
import org.example.exception.TokenRefreshException;
import org.example.model.*;
import org.example.repository.UserRepository;
import org.example.security.jwt.JwtUtils;
import org.example.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return AuthResponse.builder()
                .id(userDetails.getId())
                .token(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .userName(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(roles)
                .build();
    }

    public void register(CreateUserRequest createUserRequest) {
        if (userRepository.existsByName(createUserRequest.getUserName())) {
            throw new AlreadyExistException("Username already exists!");
        }
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new AlreadyExistException("Email already exists!");
        }

        var user = User.builder()
                .userName(createUserRequest.getUserName())
                .email(createUserRequest.getEmail())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .roleType(createUserRequest.getRoles())
                .build();
        userRepository.save(user);
    }

    public RefreshTokenResponse refreshTokenResponse(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User tokenOwner = userRepository.findById(userId).orElseThrow(
                            () -> new TokenRefreshException("Exception trying to get token for userId " + userId)
                    );
                    String token = jwtUtils.generateTokenFromUserName(tokenOwner.getUserName());
                    return new RefreshTokenResponse(token, refreshTokenService.createRefreshToken(userId).getToken());
                }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token not found"));
    }

    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentPrincipal instanceof AppUserDetails userDetails) {
            UUID userId = userDetails.getId();
            refreshTokenService.deleteByUserId(userId);
        }
    }
}