package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entites.User;

public class UserDetailsServiceImp implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
	User user=	repository.getUserByUsername(username);
	
	if(user==null) {
		throw new UsernameNotFoundException("could not find user");
	}
		
	 CustomUserDetails customuserdetails= new CustomUserDetails(user);
	
		return customuserdetails;
	}

}
