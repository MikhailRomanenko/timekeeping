package com.timekeeping.user.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.timekeeping.user.User;

/**
 * Service providing high-level data access and other {@link User}-related
 * operations.
 * 
 * @author Mikhail Romanenko
 *
 */
@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Find {@link User} by login name.
	 * 
	 * @param login
	 *            login-name of the {@link User} to find
	 * @return {@link User} object with specified login name
	 */
	public User find(String login) {
		return userRepository.findByLogin(login);
	}

}
