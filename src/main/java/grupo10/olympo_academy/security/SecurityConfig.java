package grupo10.olympo_academy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import grupo10.olympo_academy.security.jwt.JwtRequestFilter;
import grupo10.olympo_academy.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	public RepositoryUserDetailsService userDetailService;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/", "/index", "/login", "/register", "/error", "/facilities/{id}",
								"/classes/{id}")
						.permitAll()

						// STATIC RESOURCES (CSS/JS/IMAGES)
						.requestMatchers(
								"/styles.css",
								"/ourStyles.css",
								"/favicon.ico",
								"/css/**",
								"/js/**",
								"/images/**",
								"/assets/**")
						.permitAll()
						// PROTECTED PAGES
						.requestMatchers("/userProfile").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/admin/**", "/adminPanel").hasRole("ADMIN")
						.anyRequest().authenticated())

				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.usernameParameter("email")
						.defaultSuccessUrl("/")
						// .failureUrl("/login?error")
						// Custom failure handler to redirect user to /login?blocked if LockedException
						// is thrown
						.failureHandler((request, response, exception) -> {

							Throwable cause = exception.getCause();

							// check if the exception or its cause is a LockedException (which we throw when
							// a user is blocked)
							if (exception instanceof LockedException ||
									(cause != null && cause instanceof LockedException)) {

								response.sendRedirect("/login?blocked");
							} else {

								response.sendRedirect("/login?error");
							}
						})
						.permitAll())

				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());
		return http.build();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.securityMatcher("/api/v1/**")
				.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http
				.authorizeHttpRequests(authorize -> authorize

						// PUBLIC ENDPOINTS
						.requestMatchers(HttpMethod.GET,
        						"/api/v1/classes/**",
        						"/api/v1/facilities/**")
						.permitAll()

						.requestMatchers(HttpMethod.POST,
								"/api/v1/auth/login",
								"/api/v1/users") // register method
						.permitAll()

						// PROTECTED ENDPOINTS
						.requestMatchers(HttpMethod.POST,
								"/api/v1/classes/{id}/newReview",
								"/api/v1/facilities/{id}/newReview",
								"/api/v1/documents/me")
						.hasAnyRole("USER", "ADMIN")

						.requestMatchers(HttpMethod.DELETE,
								"/api/v1/classes/{id}/review/{reviewId}",
								"/api/v1/facilities/{id}/review/{reviewId}",
								"/api/v1/users/reservations/{id}")
						.hasAnyRole("USER", "ADMIN")

						.requestMatchers(HttpMethod.GET,
								"/api/v1/documents/me",
								"/api/v1/documents/me/view",
								"/api/v1/reservations",
								"/api/v1/reservations/{id}",
								"/api/v1/users/me",
								"/api/v1/users/image")
						.hasAnyRole("USER", "ADMIN")

						.requestMatchers(HttpMethod.POST,
								"/api/v1/auth/logout",
								"/api/v1/auth/refresh")
						.hasAnyRole("USER", "ADMIN")

						.requestMatchers(HttpMethod.PUT,
								"/api/v1/users/{id}",
								"/api/v1/users/image")
						.hasAnyRole("USER", "ADMIN")

						.requestMatchers(HttpMethod.PATCH,
								"/api/v1/users/{id}/password")
						.hasAnyRole("USER", "ADMIN")

						// ADMIN ENDPOINTS

						.requestMatchers(HttpMethod.POST,
								"/api/v1/classes",
								"/api/v1/facilities",
								"/api/v1/images/{id}/images")
						.hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT,
								"/api/v1/classes/{id}",
								"/api/v1/facilities/{id}",
								"/api/v1/users/admin/{id}",
								"/api/v1/users/admin/{id}/image",
								"/api/v1/images/{id}/media")
						.hasRole("ADMIN")

						.requestMatchers(HttpMethod.DELETE,
								"/api/v1/classes/{id}",
								"/api/v1/facilities/{id}",
								"/api/v1/users/admin/{id}",
								"/api/v1/images/{id}")
						.hasRole("ADMIN")

						.requestMatchers(HttpMethod.PATCH,
								"/api/v1/users/admin/{id}/*")
						.hasRole("ADMIN")

						.requestMatchers(HttpMethod.GET,
								"/api/v1/documents/users/**",
								"/api/v1/users",
								"/api/v1/users/admin/{id}")
						.hasRole("ADMIN")

						.anyRequest().authenticated());

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());

		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
