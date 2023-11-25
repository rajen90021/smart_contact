package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entites.Contact;
import com.smart.entites.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
//pagable canntent  current page and content per page
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable pageable);
	
	
	@Query("SELECT c FROM Contact c WHERE c.name LIKE %:name% AND c.user = :user")
	public List<Contact> findByNameContainingAndUser(@Param("name") String name, @Param("user") User user);

}
