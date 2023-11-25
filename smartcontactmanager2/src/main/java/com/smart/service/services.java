package com.smart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class services {
	
	@Autowired
	private JavaMailSender  javaMailSender;
	
	@Autowired
	private HttpSession httpSession;
	public void yourServiceMethod() {
	    //... your logic

	    httpSession.removeAttribute("message");
	}
	
	 public boolean sendSimpleEmail(String toEmail, String subject, String message) {
		    try {
	            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	            
	            helper.setTo(toEmail);
	            helper.setSubject(subject);
	            helper.setText(message, true); // The 'true' here indicates you're sending HTML content

	            javaMailSender.send(mimeMessage);
	            return true;  // email sent successfully

	        } catch (MessagingException e) {
	            e.printStackTrace();
	            return false;  // failed to send email
	        }
	    }
	  
	  
}
