package grupo10.olympo_academy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
					.requestMatchers("/", "/index", "/login", "/register", "/error", "/app-error", "/facilities/{id}",
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
						.requestMatchers("/bookings").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/reviews").hasAnyRole("USER", "ADMIN")
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
							// Check full cause chain to correctly classify blocked-user logins.
							if (hasLockedCause(exception)) {
								// System.out.println(">>> Redirigiendo a /login?blocked");
								response.sendRedirect("/login?blocked");
							} else {
								// System.out.println(">>> Redirigiendo a /login?error");
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

	private boolean hasLockedCause(Throwable throwable) {
		Throwable current = throwable;
		while (current != null) {
			if (current instanceof LockedException) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}
}
