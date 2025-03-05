package com.assets.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.assets.model.User;
import com.assets.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService {
	
	User save(UserRegistrationDto registrationDto);
	
	User update(UserRegistrationDto registrationDto, long id) throws UserNotFoundException;

	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	
	List<User> getAllUser();
	
//	void saveUser(User user);
	
	User getUserById(long id);
	
	void deleteUserById(long id);
	
	Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

	Page<User> searchUsers(String keyword, int pageNo, int pageSize, String sortField, String sortDir); 

	long countTotalUsers();
}

