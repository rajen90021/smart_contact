package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entites.User;
import com.smart.service.services;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;

@Controller
public class forgotcontroller {

	@Autowired
	private services services;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository repository;
	
	Random  random=   new Random(1000);
	@GetMapping("/forgot")
	public String openemailform() {
		
		return "forgot-email_form";
	}
	
	
	
	@PostMapping("/send-otp")
	public String sendotp(@RequestParam("email") String email,HttpSession session) {
		
		System.out.println("email  "+email);
		
		
		
	 int otp=	random.nextInt(9999);
	 
	 System.out.println("otp  "+otp );
	 
	 
	 
	 
//	    @Autowired
//	    private EmailService emailService;

//	    @GetMapping("/send-email")
//	    public String sendEmail() {
//	        emailService.sendSimpleEmail("recipient@example.com", "Test Subject", "This is a test email.");
//	        return "Email sent!";
//	    }
	 
	 String to=email;
	 String otpp = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; max-width: 400px; margin: 20px auto; text-align: center; background-color: #f7f7f7; border-radius: 8px;'>"
	            + "<h2 style='color: #333;'>Your Verification Code</h2>"
	            + "<p style='font-size: 16px; color: #555;'>Please use the following code to complete your verification:</p>"
	            + "<div style='font-size: 24px; color: #000; padding: 10px; border: 2px dashed #333; display: inline-block; margin: 10px 0;'>"
	            + otp
	            + "</div>"
	            + "<p style='font-size: 14px; color: #777;'>If you didn't request this code, please ignore this email.</p>"
	            + "</div>";

	 String subject="otp sending ";
	      boolean status=  services.sendSimpleEmail(to, subject, otpp);
System.out.println(status);
	      
	      if(status) {
	    	  
	    	  session.setAttribute("otp",otp);
	    	  session.setAttribute("email", email);
	    	  return "verify-otp";
	      }
	      else {
	    	 
	    	  session.setAttribute("message", "check your email id !!");
	    	  return "forgot-email_form";
	      }
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyotp(@RequestParam("otp") int otp,HttpSession session) {
		
		
		String sessionemail= (String) session.getAttribute("email");
		    int sessionotp=(int)   session.getAttribute("otp");   
		  
		    if(sessionotp== otp) {
		    	
		         User user=	this.repository.getUserByUsername(sessionemail);
		    	if(user==null) {
		    		session.setAttribute("message", "email is incorrect ");
		    		return "forgot-email_form";
		    	}else {
		    		
		    		///send change passsord form 
		    	}
		    	
		    	
		    	return "password_change_form";
		    }else {
		    	
		    	session.setAttribute("message", "you have entered a wrong otp");
		    	return "verify-otp";
		    }
		    
		
	}
	
	@PostMapping("change-password")
	public String change_password(@RequestParam("newpassword") String newpassword,HttpSession session){
		
		String email= (String)session.getAttribute("email");
	User user= 	this.repository.getUserByUsername(email);
		
	user.setPassword(bCryptPasswordEncoder.encode(newpassword));
	this.repository.save(user);
		
		return "redirect:/signin?change=password change successfully..";
	}
	
	
}
