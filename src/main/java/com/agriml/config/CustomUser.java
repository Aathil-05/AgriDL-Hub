package com.agriml.config;



import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.agriml.model.Userdetail;


public class CustomUser implements UserDetails {

	private Userdetail user;

	public CustomUser(Userdetail user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	@Override
	public boolean isEnabled() {
		return user.getIsEnabled();
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return user.getAccountNonLocked();
	}

}
