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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import grupo10.olympo_academy.security.jwt.JwtRequestFilter;
//import grupo10.olympo_academy.security.jwt.JwtTokenProvider;
import grupo10.olympo_academy.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	public RepositoryUserDetailsService userDetailService;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	// @Autowired
	// private JwtTokenProvider jwtTokenProvider;

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
						// PRIVATE ENDPOINTS
						// Images
						.requestMatchers(HttpMethod.POST, "/api/v1/images/**").hasRole("USER")
						.requestMatchers(HttpMethod.PUT, "/api/v1/images/*/media").hasRole("USER")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/images/**").hasRole("USER")

						// Classes
						.requestMatchers(HttpMethod.POST, "/api/v1/classes/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/classes/**").hasRole("USER") //revisar
						.requestMatchers(HttpMethod.DELETE, "/api/v1/classes/**").hasRole("ADMIN")

						//Users
						.requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasAnyRole("USER","ADMIN")

						

						// PUBLIC ENDPOINTS
						.anyRequest().permitAll());

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
