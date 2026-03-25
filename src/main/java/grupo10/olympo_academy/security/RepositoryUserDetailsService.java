package grupo10.olympo_academy.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import grupo10.olympo_academy.model.User;
import grupo10.olympo_academy.repository.UserRepository;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(email)
				.or(() -> userRepository.findByUsername(email))
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// check user is not blocked
		if (user.getBlocked()) {	
			throw new LockedException("User is blocked");
		}

		List<GrantedAuthority> roles = new ArrayList<>();

		for (String role : user.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
	}
}
