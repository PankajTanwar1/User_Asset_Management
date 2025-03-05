package com.assets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.assets.model.Role;

public interface RoleRepository extends JpaRepository<Role , Long> {

		Role findByName(String name);
}
