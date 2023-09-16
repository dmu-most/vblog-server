package com.example.vblogserver.domain.board.dto;

import java.util.Arrays;
import java.util.List;

public class MainBoardDTO {
    private Long ContentId;
    private String ContentDate;
    private String ContentTitle;
    private String Content;
    private Integer Heart;
    private int Review;
    private String UserName;
    private List<String> Hashtags;
    private String imgurl;

    public Long getContentId() {
        return ContentId;
    }

    public void setContentId(Long contentId) {
        ContentId = contentId;
    }

    public String getContentDate() {
        String[] Date = ContentDate.split("T");

        return Date[0].replace("-",".");
    }

    public void setContentDate(String contentDate) {
        ContentDate = contentDate;
    }

    public String getContentTitle() {
        return ContentTitle;
    }

    public void setContentTitle(String contentTitle) {
        ContentTitle = contentTitle;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getHeart() {
        return Heart;
    }

    public void setHeart(Integer heart) {
        Heart = heart;
    }

    public int getReview() {
        return Review;
    }

    public void setReview(int review) {
        Review = review;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public List<String> getHashtags() {
        return Hashtags;
    }

    public void setHashtags(String hashtags) {
        String[] hashtagsArray = hashtags.split("#");
        List<String> hashtagsList = Arrays.asList(hashtagsArray);
        Hashtags = hashtagsList.subList(1, hashtagsList.size());
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
