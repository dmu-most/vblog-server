package com.example.vblogserver.init.naver;
import java.util.List;

public class NaverEntity {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String title;
        private String link;
        private String description;

        private String bloggername;

        private String bloggerlink;
        private String postdate;

        public String getTitle() {
            return title;
        }
        public String getLink() {
            return link;
        }
        public String getDescription() {
            return description;
        }
        public String getBloggername() {
            return bloggername;
        }
        public String getBloggerlink() {
            return bloggerlink;
        }
        public String getPostdate() {
            return postdate;
        }
    }
}