package me.dolia.pmm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Operation;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findAllByUserEmailAndOperation(String email, Operation operation);

	List<Category> findAllByUserEmail(String email);

}
