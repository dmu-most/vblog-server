package com.example.vblogserver.domain.board.dto;

public class CategoryMDTO {
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryMDTO(String categoryName) {
        this.categoryName = covCategory(categoryName);
    }

    private String covCategory(String CategoryName){
        String covCategory="";
        switch (CategoryName) {
            case "여행":
                covCategory = "Travel";
                break;
            case "게임":
                covCategory = "Game";
                break;
            case "건강":
                covCategory = "Health";
                break;
            case "맛집":
                covCategory = "Restaurant";
                break;
            case "방송":
                covCategory = "Broadcasting";
                break;
            case "뷰티":
                covCategory = "Beauty";
                break;
        }
        return covCategory;
    }
}
