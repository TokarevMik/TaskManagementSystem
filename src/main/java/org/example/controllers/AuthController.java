package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.exception.AlreadyExistException;
import org.example.model.*;
import org.example.repository.UserRepository;
import org.example.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "API for user authentication")
@RestController
@RequestMapping("app/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Operation(summary = "User login", description = "Authenticates a user and returns an access token")
    @Parameter(
            name = "LoginRequest",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(
                            implementation = LoginRequest.class)),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully, returns access and refresh tokens"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid request body")
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }


    @Operation(summary = "Register user", description = "Registering new account")
    @Parameter(
            name = "CreateUserRequest",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(
                            implementation = CreateUserRequest.class)),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registration completed"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid request body"),
            @ApiResponse(responseCode = "409", description = "Conflict - Username or email already exists")})
    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@RequestBody CreateUserRequest createUserRequest) {
        if (userRepository.existsByName(createUserRequest.getUserName())) {
            throw new AlreadyExistException("Username already exists!");
        }
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new AlreadyExistException("Email already exists!");
        }
        securityService.register(createUserRequest);
        return ResponseEntity.ok(new SimpleResponse("User created"));
    }

    @Operation(summary = "Refreshing token", description = "Refreshing an expired JWT token")
    @Parameter(
            name = "RefreshTokenRequest",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(
                            implementation = RefreshTokenRequest.class)),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "403", description = "Invalid refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refreshTokenResponse(request));
    }
    @Operation(summary = "Logout", description = "Logs out the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        securityService.logout();
        return ResponseEntity.ok(new SimpleResponse("User Logout. Username is: " + userDetails.getUsername()));
    }
}