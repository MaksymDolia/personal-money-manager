package me.dolia.pmm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dolia.pmm.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	Role findByName(String name);

}
