package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class myconfig
{	
	
	@Bean
	public UserDetailsService detailsService() {
		return new UserDetailsServiceImp();	
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return  new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(detailsService());
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return authenticationProvider;
	}
	
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//		
//		auth.authenticationProvider(authenticationProvider());
//	}
	
//	protected void configure(HttpSecurity http) throws Exception{
//     
//		http.authorizeRequests().
//	}
	
//	    @Bean
//	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	        http.authorizeRequests()http;
//	        
//	    }
	    
	@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	    	  http
//	            .authorizeHttpRequests((authz) -> authz
//	                .requestMatchers("/admin/**").hasRole("ADMIN")
//	                .requestMatchers("/user/**").hasRole("USER")
//	                .requestMatchers("/**").permitAll().and().
//	                
//	            );
//	        return http.build();
	    	http.csrf().disable()
	    	.authorizeHttpRequests()
	    	.requestMatchers("/ADMIN**")
	    	.hasRole("ADMIN")
	    	.requestMatchers("/user/**")
	    	.hasRole("USER")
	    	.requestMatchers("/**")
	    	.permitAll()
	    	.anyRequest().authenticated().and().formLogin()
	    	.loginPage("/signin")
	    	.loginProcessingUrl("/dologin")
	    	.defaultSuccessUrl("/user/index");
//	    	.failureUrl("/login-fail");
	    	http.authenticationProvider(authenticationProvider());
	    	return http.build();
	    	
	    }
	
	}

	
	
	
	
	
	
	
	
	