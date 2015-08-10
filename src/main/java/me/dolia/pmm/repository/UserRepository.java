package me.dolia.pmm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dolia.pmm.entity.User;


public interface UserRepository extends JpaRepository<User, Integer> {

	User findOneByEmail(String email);

}
