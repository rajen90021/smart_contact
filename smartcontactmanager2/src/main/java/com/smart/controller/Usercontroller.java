package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entites.Contact;
import com.smart.entites.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class Usercontroller {

	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository repository;
	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addcommondata(Model model, Principal principal) {

		String username = principal.getName();
		System.out.println(username);

		User user = this.repository.getUserByUsername(username);
		System.out.println(user);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dasboard(Model model, Principal principal) {

		model.addAttribute("title", "home page ");
		return "normal/user_dasboard";
	}

	@GetMapping("/add-contact")
	public String openaddcontactform(Model model) {

		model.addAttribute("title", "add contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

//	@GetMapping("/home")
//	public String openhomepage() {
//		return "normal/home";
//	}
//	@ModelAttribute Contact contact,

	@PostMapping("/process-contact")
	public String processaddcontact(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			Principal p, HttpSession session) {

		try {
			String useremail = p.getName();

			User user = repository.getUserByUsername(useremail);

			if (file.isEmpty()) {
				System.out.println("file is empty");

				contact.setImage("contact.png");

			} else {
				// 1. Set the image name in the Contact object to the name of the uploaded file.
				String uploadedFileName = file.getOriginalFilename();
				contact.setImage(uploadedFileName);

				// 2. Locate the directory where we want to save the uploaded image.
				File directory = new ClassPathResource("static/img").getFile();

				// 3. Construct the full path (directory + filename) where the image will be
				// saved.
				String savePath = directory.getAbsolutePath() + File.separator + uploadedFileName;
				Path destinationPath = (Path) Paths.get(savePath);
				// 4. Save (or copy) the uploaded image to the destination path.
				// If an image with the same name already exists, we'll replace it.
				Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");

			}

			contact.setUser(user);

			List<Contact> list = user.getContacts();
			list.add(contact);
			repository.save(user);

			session.setAttribute("message", new Message("contact added successfully ", "success"));
			System.out.println("added to databases ");
//				System.out.println(file.getOriginalFilename());
//				System.out.println(file.getName());
			System.out.println(contact);
		} catch (Exception e) {
			session.setAttribute("message", new Message("contact not added ", "danger"));
			e.printStackTrace();
			System.out.println("errror" + e.getMessage());
		}
		return "normal/add_contact_form";
	}

	/// per page =5[n]
	// currentpage=0
	@GetMapping("/show-contacts/{page}")
	public String showcontact(@PathVariable("page") Integer page, Model model, Principal p) {

		String username = p.getName();

		User user = this.repository.getUserByUsername(username);

		// pagable pageRequest = PageRequest.of(page , 5); or
		PageRequest pageRequest = PageRequest.of(page, 4);

		Page<Contact> contact = this.contactRepository.findContactByUser(user.getId(), pageRequest);
		System.out.println(contact);
		model.addAttribute("contacts", contact);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());

		model.addAttribute("title", "show tables");
		return "normal/show_contacts";
	}

	@RequestMapping("/{cId}/contact")
	public String showcontactdetails(@PathVariable("cId") Integer cId, Model model, Principal p) {

		System.out.println("cid " + cId);

		Optional<Contact> contOptional = this.contactRepository.findById(cId);

		Contact contact = contOptional.get();

		String username = p.getName();
		User user = this.repository.getUserByUsername(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact-detail";
	}

	@GetMapping("/delete/{cId}")
	public String deletecontact(@PathVariable("cId") Integer cId, Model model, Principal p, HttpSession session,
			Principal principal) {

		Optional<Contact> optional = this.contactRepository.findById(cId);

		Contact contact = optional.get();
//	  contact.setUser(null);
//	     this.contactRepository.delete(contact);
		User user = this.repository.getUserByUsername(principal.getName());
		user.getContacts().remove(contact);
		this.repository.save(user);
		session.setAttribute("message", new Message("contact is delete succesfully", "success"));

		return "redirect:/user/show-contacts/0";
	}

	@PostMapping("/update-contact/{cid}")
	public String updatecontact(@PathVariable("cid") Integer cId, Model model) {

		Optional<Contact> optional = this.contactRepository.findById(cId);
		Contact contact = optional.get();

		model.addAttribute("title", "update form ");
		model.addAttribute("contact", contact);
		return "normal/update-form";
	}

	@PostMapping("/process-update")
	public String updatehandler(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {
			/// old contact details
			Optional<Contact> oldcontactdetails = this.contactRepository.findById(contact.getcId());

			Contact contactt = oldcontactdetails.get();

			if (!file.isEmpty()) {
				/// delete old photo

				File deletefile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deletefile, contactt.getImage());
				file1.delete();

				// update new phto

				// 1. Set the image name in the Contact object to the name of the uploaded file.
				String uploadedFileName = file.getOriginalFilename();

				// 2. Locate the directory where we want to save the uploaded image.
				File directory = new ClassPathResource("static/img").getFile();

				// 3. Construct the full path (directory + filename) where the image will be
				// saved.
				String savePath = directory.getAbsolutePath() + File.separator + uploadedFileName;
				Path destinationPath = (Path) Paths.get(savePath);
				// 4. Save (or copy) the uploaded image to the destination path.
				// If an image with the same name already exists, we'll replace it.
				Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(uploadedFileName);

			} else {
				contact.setImage(contactt.getImage());
			}

			String username = principal.getName();

			User user = this.repository.getUserByUsername(username);

			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("your contact is update succesfully...", "success"));
		} catch (Exception e) {

			e.printStackTrace();
		}

		System.out.println("name " + contact.getName());
		System.out.println("id " + contact.getcId());
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	@GetMapping("/profile")
	public String yourprofile(Model model) {

		model.addAttribute("title", "profile page ");
		return "normal/profile";
	}
	@GetMapping("/settings")
	public String opensettingpage() {
		
		return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session ){
		
		System.out.println(oldPassword);
		System.out.println(newPassword);
		
		
	 String username =	principal.getName();
       User user =	this.repository.getUserByUsername(username);	
	 
   boolean b=       bCryptPasswordEncoder.matches(oldPassword, user.getPassword());
	 
       if(b){
    	   
    	   user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    	   this.repository.save(user);
			session.setAttribute("message", new Message("your password is update succesfully...", "success"));
			return "redirect:/user/index";
       }else{
			session.setAttribute("message", new Message("your password is not update...", "danger"));
			return "redirect:/user/settings";
       }
   
		
	}

}
