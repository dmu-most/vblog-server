package com.example.vblogserver.domain.category.initializer;

import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.repository.CategoryGRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryGInitializer implements CommandLineRunner {
    private final CategoryGRepository categoryGRepository;

    @Autowired
    public CategoryGInitializer(CategoryGRepository categoryGRepository){
        this.categoryGRepository = categoryGRepository;
    }

    @Override
    public void run(String... arg){

        String[] categoryG_name = {"vlog","blog"};

        for(int i=0; i<categoryG_name.length; i++) {
            CategoryG categoryG = new CategoryG();
            categoryG.setCategoryName(categoryG_name[i]);
            categoryGRepository.save(categoryG);
        }

    }
}
