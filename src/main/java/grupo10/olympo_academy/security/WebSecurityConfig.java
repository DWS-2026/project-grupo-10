package grupo10.olympo_academy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	public RepositoryUserDetailsService userDetailService;

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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

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
						.requestMatchers("/tareas").hasAnyRole("USER","ADMIN")
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
						.permitAll())

				.exceptionHandling(ex -> ex.accessDeniedPage("/"));
		return http.build();
	}
}
