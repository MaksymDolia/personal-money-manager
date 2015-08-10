package me.dolia.pmm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;
import me.dolia.pmm.entity.User;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findAllByUser(User user);

	List<Category> findAllByUserAndOperation(User user, Operation operation);

}
