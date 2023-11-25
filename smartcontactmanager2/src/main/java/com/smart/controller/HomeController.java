package com.smart.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entites.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {

	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository repository;
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title","home-smart contact manager");
		 
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title","home-smart contact manager");
		 
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title","signup-smart contact manager");
		 model.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/do_register")
	public String registeruser(@Valid @ModelAttribute("user") User user,BindingResult result1,
			@RequestParam(value="agreement",defaultValue = "false") boolean agreement,
			Model model,HttpSession session)
	{
	try {
		System.out.println("1111");
		
		if (!agreement) {
		   throw new Exception("you have not agreed the term and condition");
		}
		
		System.out.println("2222222222");
		if(result1.hasErrors()) {
			System.out.println("33333333333");
			System.out.println(result1.toString());
			model.addAttribute("user", user);
			return "signup";
		}
		System.out.println("4444444444");
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("cartoon.jpg");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		User result =	this.repository.save(user);
		
		 model.addAttribute("user", new User());
		 Message message = new Message("sucessfully register ","alert-success");
//	      session.setAttribute("message", new Message("sucessfully register ","alert-success"));  
		 session.setAttribute("message", message);
	      model.addAttribute("httpSession", session);

		return "signup";
	}
	catch(Exception e) {
		e.printStackTrace();
		model.addAttribute("user", user);
		
		Message message = new Message("something went wrong "+e.getMessage(), "alert-danger");
		//session.setAttribute("message", new Message("something went wrong"+e.getMessage(), "alert-danger"));
		session.setAttribute("message", message);
		 model.addAttribute("httpSession", session);
		return "signup";
	}
		
		
	}
	
	
	@RequestMapping("/signin")
 public String login() {
	 return "login";
 }
	
	
}
