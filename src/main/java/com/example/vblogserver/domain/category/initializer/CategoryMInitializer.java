package com.example.vblogserver.domain.category.initializer;

import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.repository.CategoryMRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryMInitializer implements CommandLineRunner {
    private final CategoryMRepository categoryMRepository;

    public CategoryMInitializer(CategoryMRepository categoryMRepository) {
        this.categoryMRepository = categoryMRepository;
    }

    @Override
    public void run(String... args){

        String[] categoryM_name = {"여행","게임","건강","맛집","방송","뷰티"};

        for(int i=0; i<categoryM_name.length; i++) {
            CategoryM categoryM = new CategoryM();
            categoryM.setCategoryName(categoryM_name[i]);
            categoryMRepository.save(categoryM);
        }
    }
}
