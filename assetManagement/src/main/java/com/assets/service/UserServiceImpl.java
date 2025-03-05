package com.assets.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.assets.model.Role;
import com.assets.model.User;
import com.assets.repository.RoleRepository;
import com.assets.repository.UserRepository;
import com.assets.web.dto.UserRegistrationDto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;		
	}
	
	 private Role getOrCreateRole(String roleName) {
	        Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase());
	        if (role == null) {
	            role = new Role("ROLE_" + roleName.toUpperCase());
	            roleRepository.save(role);
	        }
	        return role;
	    }
	 
	  @Override
	    public User save(UserRegistrationDto registrationDto) {
	        Role role = getOrCreateRole(registrationDto.getRole());

	        User user = new User(
	            registrationDto.getFirstName(),
	            registrationDto.getLastName(),
	            registrationDto.getGender(),
	            registrationDto.getEmail(),
	            passwordEncoder.encode(registrationDto.getPassword()),
	            role,
	            registrationDto.getAddress(),
	            registrationDto.getCity(),
	            registrationDto.getState(),
	            registrationDto.getZip()
	        );
	        return userRepository.save(user);
	    }

	    @Override
	    public User update(UserRegistrationDto registrationDto, long id) throws UserNotFoundException {
	        User existingUser = userRepository.findById(id)
	            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

	        existingUser.setFirstName(registrationDto.getFirstName());
	        existingUser.setLastName(registrationDto.getLastName());
	        existingUser.setGender(registrationDto.getGender());
	        existingUser.setEmail(registrationDto.getEmail());

	        if (registrationDto.getPassword() != null && !registrationDto.getPassword().isEmpty()) {
	            existingUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
	        }

	        Role role = getOrCreateRole(registrationDto.getRole());
	        existingUser.setRole(role);

	        existingUser.setAddress(registrationDto.getAddress());
	        existingUser.setCity(registrationDto.getCity());
	        existingUser.setState(registrationDto.getState());
	        existingUser.setZip(registrationDto.getZip());

	        return userRepository.save(existingUser);
	    }
	 
//	@Override
//	public User save(UserRegistrationDto registrationDto) {
//		
//	    Role role = null;
//	    if ("USER".equalsIgnoreCase(registrationDto.getRole())) {
//	        role = roleRepository.findByName("ROLE_USER");
//	        if (role == null) {
//	            // Create and save the role if it doesn't exist
//	            role = new Role("ROLE_USER");
//	            roleRepository.save(role);
//	        }
//	    } else if ("ADMIN".equalsIgnoreCase(registrationDto.getRole())) {
//	        role = roleRepository.findByName("ROLE_ADMIN");
//	        if (role == null) {
//	            // Create and save the role if it doesn't exist
//	            role = new Role("ROLE_ADMIN");
//	            roleRepository.save(role);
//	        }
//	    } else {
//	        throw new IllegalArgumentException("Invalid role: " + registrationDto.getRole());
//	    }
//
//	    User user = new User(
//	        registrationDto.getFirstName(),
//	        registrationDto.getLastName(),
//	        registrationDto.getGender(),
//	        registrationDto.getEmail(), 
//	        passwordEncoder.encode(registrationDto.getPassword()),
//	        Arrays.asList(role),
//	        registrationDto.getAddress(),
//	        registrationDto.getCity(),
//	        registrationDto.getState(),
//	        registrationDto.getZip()      
//	    );
//	    return userRepository.save(user);
//	}
//	
//	
//	 @Override
//	 public User update(UserRegistrationDto registrationDto, long id) throws UserNotFoundException {
//	        User existingUser = userRepository.findById(id)
//	                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
//	        
//	        existingUser.setFirstName(registrationDto.getFirstName());
//	        existingUser.setLastName(registrationDto.getLastName());
//	        existingUser.setGender(registrationDto.getGender());
//	        existingUser.setEmail(registrationDto.getEmail());
//	        
//	        if (registrationDto.getPassword() != null && !registrationDto.getPassword().isEmpty()) {
//	            existingUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
//	        }
//	        
//	        Role role = null;
//	        if ("USER".equalsIgnoreCase(registrationDto.getRole())) {
//	            role = roleRepository.findByName("ROLE_USER");
//	            if (role == null) {
//	                role = new Role("ROLE_USER");
//	                roleRepository.save(role);
//	            }
//	        } else if ("ADMIN".equalsIgnoreCase(registrationDto.getRole())) {
//	            role = roleRepository.findByName("ROLE_ADMIN");
//	            if (role == null) {
//	                role = new Role("ROLE_ADMIN");
//	                roleRepository.save(role);
//	            }
//	        } else {
//	            throw new IllegalArgumentException("Invalid role: " + registrationDto.getRole());
//	        }
//	        
//	        
//	        existingUser.setRoles(Arrays.asList(role));
//	        existingUser.setAddress(registrationDto.getAddress());
//	        existingUser.setCity(registrationDto.getCity());
//	        existingUser.setState(registrationDto.getState());
//	        existingUser.setZip(registrationDto.getZip());
//	        
//	        
//	        return userRepository.save(existingUser);
//	    }
	 
	 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		User user = userRepository.findByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), 
																	  user.getPassword(), 
																	  mapRolesToAuthorities(user.getRole()));
	}
	
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role){
		return List.of(new SimpleGrantedAuthority(role.getName()));
	}            

	
	@Override
	public List<User> getAllUser(){
		return userRepository.findAll();
	}
	
	
//	@Override
//	public void saveUser(User user) {
//		userRepository.save(user);
//	}
	
	
	@Override
	public User getUserById(long id) {
		Optional<User> optional = userRepository.findById(id);
		
		User user = null;
		if(optional.isPresent()) {
			user = optional.get();
		}
		else {
			throw new RuntimeException("User not found for Id:: " + id);
		}
		return user;
	}
	
	
	@Override
	public void deleteUserById(long id) {
		userRepository.deleteById(id);
	}


	@Override
	public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
																			    Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
	    return userRepository.findAll(pageable);
	}
	
	
	 @Override
	    public Page<User> searchUsers(String keyword, int pageNo, int pageSize, String sortField, String sortDir) {
	        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
	        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize, sort);
	        return userRepository.searchUsers(keyword, pageable);
	    }
	 
	 @Override
	 public long countTotalUsers() {
	     return userRepository.count(); 
	 }
}
