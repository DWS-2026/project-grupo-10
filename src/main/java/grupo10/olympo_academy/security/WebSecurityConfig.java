package grupo10.olympo_academy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
						.requestMatchers("/", "/index", "/login", "/register", "/error").permitAll()

						// STATIC RESOURCES (CSS/JS/IMAGES)
						.requestMatchers(
							"/styles.css",
							"/ourStyles.css",
							"/favicon.ico",
							"/css/**",
							"/js/**",
							"/images/**",
							"/assets/**"
						).permitAll()
					.requestMatchers("/userProfile").hasAnyRole("USER", "ADMIN")
					.requestMatchers("/bookings").hasAnyRole("USER", "ADMIN")
					.requestMatchers("/reviews").hasAnyRole("USER", "ADMIN")
					.anyRequest().authenticated()
			)

			.formLogin(formLogin -> formLogin
					.loginPage("/login")
					.usernameParameter("email")
					.defaultSuccessUrl("/userProfile") // no funciona el login
					.failureUrl("/login?error")
					.permitAll()
			)
			.logout(logout -> logout
					.logoutUrl("/logout")
					.logoutSuccessUrl("/")
					.permitAll()
			);

		return http.build();
	}
}
