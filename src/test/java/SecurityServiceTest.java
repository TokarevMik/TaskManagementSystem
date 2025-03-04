
import org.example.model.*;
import org.example.repository.UserRepository;
import org.example.security.AppUserDetails;
import org.example.security.SecurityService;
import org.example.security.jwt.JwtUtils;
import org.example.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private RefreshTokenService refreshTokenService; // Добавил мок

    @InjectMocks
    private SecurityService securityService;

    @Test
    void testAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UUID userId = UUID.randomUUID();
        AppUserDetails userDetails = new AppUserDetails(new User(
                userId, "test@example.com", "password", "testUser",
                null, null, null,
                null, null, Set.of(RoleType.USER)
        ));

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("fakeToken");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("fakeRefreshToken");

        when(refreshTokenService.createRefreshToken(any())).thenReturn(mockRefreshToken); // Добавил мок refreshToken

        AuthResponse authResponse = securityService.authenticateUser(loginRequest);

        assertNotNull(authResponse);
        assertEquals("fakeToken", authResponse.getToken());
        assertEquals("fakeRefreshToken", authResponse.getRefreshToken());
        assertEquals("testUser", authResponse.getUserName());
    }

    @Test
    void testRegisterUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "test@example.com", Set.of(RoleType.USER), "password");

        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(userRepository.existsByName("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        securityService.register(createUserRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
