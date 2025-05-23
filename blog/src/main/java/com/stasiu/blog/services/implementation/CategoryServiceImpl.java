package com.stasiu.blog.services.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.stasiu.blog.domain.dtos.UpdateCategoryRequest;
import com.stasiu.blog.domain.entities.Category;
import com.stasiu.blog.repositories.CategoryRepository;
import com.stasiu.blog.services.CategoryService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if(categoryRepository.existsByNameIgnoreCase(category.getName())){
            throw new IllegalArgumentException("Category already exists" + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            if(!category.get().getPosts().isEmpty()){
                throw new IllegalStateException("Category has posts and cannot be deleted");
            } 
            categoryRepository.deleteById(id);
        } 
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    public Category updateCategory(UUID id, UpdateCategoryRequest updateCategoryRequest) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        
        if(!category.getName().equals(updateCategoryRequest.getName())) {
            category.setName(updateCategoryRequest.getName());
        }
        
        return categoryRepository.save(category);
    }

    
}
