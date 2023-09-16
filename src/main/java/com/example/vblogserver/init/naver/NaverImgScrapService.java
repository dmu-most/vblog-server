package com.example.vblogserver.init.naver;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.io.IOException;

@Service
public class NaverImgScrapService {

    //@PostConstruct
    public String scrapeFirstImage(String contentUrl) throws IOException {
        // 게시글 url 을 요청하여 html 소스 로드
        String baseurl = "https://blog.naver.com/PostView.naver?blogId=";

        // vlog 게시글 url에서 naver 블로그가 아닌 경우가 있어 naver 블로그 게시글일 경우에만 이미지 크롤링
        if(contentUrl.split("/")[2].equals("blog.naver.com")) {
            String writer = contentUrl.split("/")[3];
            String contentId = contentUrl.split("/")[4];
            Document document = Jsoup.connect(baseurl+writer+"&logNo="+contentId).get();

            // 이미지가 있는 <a> 태그 class 명 추출
            Elements imageLinks = document.select("a.se-module-image-link.__se_image_link.__se_link");

            String imageUrl = "";
            if (!imageLinks.isEmpty()) {
                // 게시글의 첫번째 영역만 추출
                Element firstImageLink = imageLinks.first();

                // div 영역 내의 이미지 class 추출
                String dataLinkData = firstImageLink.attr("data-linkdata");

                // data-linkdata 속성의 값을 JSON으로 파싱
                JSONObject jsonData = new JSONObject(dataLinkData);

                // JSON에서 "src" 값을 추출. type=w966 은 이미지 dpi 를 의미함. 숫자가 높을수록 고화질
                imageUrl = jsonData.getString("src")+"?type=w966";

                return imageUrl;
            }
        }

        return "";
    }
}
