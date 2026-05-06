package grupo10.olympo_academy.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.controllers.auth.LoginAttemptService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {
	
    private static final Logger log = LoggerFactory.getLogger(UserLoginService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService; 

    public UserLoginService(AuthenticationManager authenticationManager,
                            UserDetailsService userDetailsService,
                            JwtTokenProvider jwtTokenProvider,
                            LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginAttemptService = loginAttemptService;
    }

    public ResponseEntity<AuthResponse> login(
            HttpServletResponse response,
            LoginRequest loginRequest) {

        String username = loginRequest.getUsername();

        // 1. Check if user is blocked
        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(423) // Locked
                    .body(new AuthResponse(
                            AuthResponse.Status.FAILURE,
                            "Account temporarily locked due to too many failed login attempts"
                    ));
        }

        try {
            // 2. Try authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            loginRequest.getPassword()
                    )
            );

            // 3. login successful → reset attempts
            loginAttemptService.loginSucceeded(username);

            UserDetails user = userDetailsService.loadUserByUsername(username);

            var newAccessToken = jwtTokenProvider.generateAccessToken(user);
            var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
            response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

            return ResponseEntity.ok(
                    new AuthResponse(
                            AuthResponse.Status.SUCCESS,
                            "Auth successful. Tokens are created in cookie."
                    )
            );

        } catch (AuthenticationException ex) {

            // 4. login failed → record failed attempt
            loginAttemptService.loginFailed(username);

            return ResponseEntity.status(401)
                    .body(new AuthResponse(
                            AuthResponse.Status.FAILURE,
                            "Invalid username or password"
                    ));
        }
    }

    public ResponseEntity<AuthResponse> refresh(HttpServletResponse response, String refreshToken) {
        try {
            var claims = jwtTokenProvider.validateToken(refreshToken);
            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

            var newAccessToken = jwtTokenProvider.generateAccessToken(user);
            response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

            return ResponseEntity.ok(
                    new AuthResponse(
                            AuthResponse.Status.SUCCESS,
                            "Access token refreshed"
                    )
            );

        } catch (Exception e) {
            log.error("Error while processing refresh token", e);
            return ResponseEntity.status(401)
                    .body(new AuthResponse(
                            AuthResponse.Status.FAILURE,
                            "Failure while processing refresh token"
                    ));
        }
    }

    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.addCookie(removeTokenCookie(TokenType.ACCESS));
        response.addCookie(removeTokenCookie(TokenType.REFRESH));
        return "logout successfully";
    }

    private Cookie buildTokenCookie(TokenType type, String token) {
        Cookie cookie = new Cookie(type.cookieName, token);
        cookie.setMaxAge((int) type.duration.getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private Cookie removeTokenCookie(TokenType type) {
        Cookie cookie = new Cookie(type.cookieName, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
