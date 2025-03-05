package com.assets.model;

//import java.util.Collection;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames="email"))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	private String gender;
	
	@Column(unique = true, nullable = false) 
	private String email;
	
	@Column(nullable = false)
	private String password;
	
//	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE) 
//	@JoinTable( name="users_roles", 
//				joinColumns = @JoinColumn( name ="user_id", referencedColumnName="id"),
//				inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName="id"))	
//	private Collection<Role> roles;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
	private Role role;
	
	private String address;
	
	private String city;
	
	private String state;
	
	private String zip;

	public User() {
		
	}
	
	
	public User(String firstName, String lastName,String gender, String email, String password, Role role,String address,String city,String state,String zip) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.email = email;
		this.password = password;
		this.role = role;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}
//	public Collection<Role> getRoles() {
//		return roles;
//	}
//
//
//	public void setRoles(Collection<Role> roles) {
//		this.roles = roles;
//	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
