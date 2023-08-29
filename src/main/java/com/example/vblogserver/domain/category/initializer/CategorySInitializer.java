package com.example.vblogserver.domain.category.initializer;

import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.domain.category.repository.CategorySRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySInitializer implements CommandLineRunner {
    private final CategorySRepository categorySRepository;

    public CategorySInitializer(CategorySRepository categorySRepository) {
        this.categorySRepository = categorySRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        String[] categoryS_name = {"국내여행","해외여행","롤","발로란트","메이플","피파 온라인 4","디아블로4","로스트아크","건강기능식품","운동","식이요법","카페","양식","한식","예능","드라마","남성뷰티","여성뷰티"};

        for(int i=0; i<categoryS_name.length; i++) {
            CategoryS categoryS = new CategoryS();
            categoryS.setCategoryName(categoryS_name[i]);
            categorySRepository.save(categoryS);
        }
    }
}
