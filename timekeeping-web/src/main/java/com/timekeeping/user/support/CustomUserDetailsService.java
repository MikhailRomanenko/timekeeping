package com.timekeeping.user.support;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.timekeeping.user.User;

/**
 * Implementation of the {@link UserDetailsService} interface for the security purpose.
 * 
 * @author Mikhail Romanenko
 *
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Autowired
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/*(non-javadoc)
	 * @see UserDetailsService
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByLogin(username);
		if(user == null)
			throw new UsernameNotFoundException("Could not find user " + username);
		return new CustomUserDetails(user);
	}
	
	@SuppressWarnings("serial")
	private static final class CustomUserDetails extends User implements UserDetails {
		
		private CustomUserDetails(User user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return AuthorityUtils.commaSeparatedStringToAuthorityList(this.getRoles());
		}

		@Override
		public String getUsername() {
			return this.getLogin();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
		
	}

}
