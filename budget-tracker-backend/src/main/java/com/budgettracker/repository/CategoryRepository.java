package com.budgettracker.repository;

import com.budgettracker.model.Category;
import com.budgettracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    List<Category> findByUser(User user);
    
    List<Category> findByUserOrderByNameAsc(User user);
    
    boolean existsByNameAndUser(String name, User user);
}