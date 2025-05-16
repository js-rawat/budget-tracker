package com.budgettracker.repository;

import com.budgettracker.model.Category;
import com.budgettracker.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {
    
    List<Subcategory> findByCategory(Category category);
    
    List<Subcategory> findByCategoryOrderByNameAsc(Category category);
    
    boolean existsByNameAndCategory(String name, Category category);
}