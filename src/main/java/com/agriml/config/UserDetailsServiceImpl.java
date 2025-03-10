package com.agriml.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agriml.model.Userdetail;
import com.agriml.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Userdetail user = userRepository.findByEmail(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + username);
		}
		return new CustomUser(user);
	}

}
