package com.example.vblogserver.init;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.init.naver.NaverService;
import com.example.vblogserver.init.youtube.YoutubeService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoardComponent implements CommandLineRunner {
    private final BoardRepository boardRepository;
    private final YoutubeService youtubeService;
    private final NaverService naverService;

    @Autowired
    public BoardComponent(BoardRepository boardRepository, YoutubeService youtubeService, NaverService naverService){
        this.boardRepository = boardRepository;
        this.youtubeService = youtubeService;
        this.naverService = naverService;
    }

    @Override
    public void run(String... arg){
        // 카테고리 항목
        String[][] category_name = {
                {"1","1|2","국내여행|해외여행"},
                {"2","3|4|5|6|7|8","롤|발로란트|메이플|피프 온라인 4|디아블로4|로스트아크"}//,
                //{"3","9|10|11","건강기능식품|운동|식이요법"},
                //{"4","12|13|14","카페|양식|한식"},
                //{"5","15|16","예능|드라마"},
                //{"6","17|18","남성|여성"}
        };

        // 조회 최대 건수
        int maxResults = 1;
        for (int z=1; z<3; z++) {

            // 대분류 카테고리
            CategoryG categoryG = new CategoryG();
            categoryG.setId(Long.valueOf(z));

            // 중분류 카테고리
            for (int x = 0; x < category_name.length; x++) {
                CategoryM categoryM = new CategoryM();
                categoryM.setId(Long.parseLong(category_name[x][0]));

                // 소분류 카테고리
                String[] categoryS_id = category_name[x][1].split("\\|");
                String[] categoryS_name = category_name[x][2].split("\\|");
                System.out.println(categoryS_name[0]);
                for (int y = 0; y < categoryS_name.length; y++) {
                    CategoryS categoryS = new CategoryS();
                    categoryS.setId(Long.parseLong(categoryS_id[y]));

                    // 소분류 카테고리로 컨텐츠 검색 결과 반환
                    JSONObject response = new JSONObject();

                    //vlog
                    //if (z==1) response = youtubeService.getYoutubeData(categoryS_name[y], maxResults);
                        //blog
                    //else if (z==2) response = naverService.getNaverData(categoryS_name[y], maxResults);
                    response = naverService.getNaverData(categoryS_name[y], maxResults);

                        //String data = "{\"items\":[{\"link\":\"https://www.youtube.com/shorts/i0pHAgAt3xI\",\"description\":\"제주도 #제주도여행 #여행브이로그 #국내여행 #여행유튜버 #우도 #제주도유채꽃 #봄여행지추천 #유채꽃 #성산일출봉 #shorts #jeju ...\",\"writer\":\"정문츄☀\uFE0E : 여행하는 신입사원\",\"title\":\"공항에서 꼭 찍어야하는 #여행쇼츠 #여행릴스\",\"thumbnails\":\"https://i.ytimg.com/vi/i0pHAgAt3xI/default.jpg\",\"createDate\":\"2023-04-06T09:45:03Z\",\"heshtag\":\"#여행쇼츠#여행릴스#제주도여행#여행브이로그#국내여행#여행유튜버#우도#제주도유채꽃#봄여행지추천#유채꽃#성산일출봉#shorts#jeju\"}]}" ;
                        //if (z==1) response = new JSONObject(data);


                    System.out.println("responst : "+response);
                    //{"items":[{"link":"https://www.youtube.com/shorts/H2uANT5s_Vo","description":"","writer":"일상이 여행 Everyday travel","title":"나만 알고싶은 최고의 여행지 #국내여행 #국내여행추천 #한국여행 #숨은명소 #여행유튜버 #한국관광100선 #대한민국구석구석","thumbnails":"https://i.ytimg.com/vi/H2uANT5s_Vo/default.jpg","createDate":"2023-01-26T05:21:53Z","heshtag":"#국내여행#국내여행추천#한국여행#숨은명소#여행유튜버#한국관광100선#대한민국구석구석"}]}
                    //{"items":[{"link":"https://www.youtube.com/shorts/i0pHAgAt3xI","description":"제주도 #제주도여행 #여행브이로그 #국내여행 #여행유튜버 #우도 #제주도유채꽃 #봄여행지추천 #유채꽃 #성산일출봉 #shorts #jeju ...","writer":"정문츄☀︎ : 여행하는 신입사원","title":"공항에서 꼭 찍어야하는 #여행쇼츠 #여행릴스","thumbnails":"https://i.ytimg.com/vi/i0pHAgAt3xI/default.jpg","createDate":"2023-04-06T09:45:03Z","heshtag":"#여행쇼츠#여행릴스#제주도여행#여행브이로그#국내여행#여행유튜버#우도#제주도유채꽃#봄여행지추천#유채꽃#성산일출봉#shorts#jeju"}]}
                    //{"items":[{"link":"https://www.youtube.com/shorts/cglFepfPqVY","description":"징중부#롤#shorts.","writer":"징중부","title":"요즘 내 롤 쇼츠 유튜버","thumbnails":"https://i.ytimg.com/vi/cglFepfPqVY/default.jpg","createDate":"2023-07-14T08:00:30Z","heshtag":"#롤#shorts."}]}
                    //{"items":[{"link":"https://www.youtube.com/shorts/","description":"\"게임 전문 MCN 롤큐\"에서 공식적으로 운영하는 발로란트 쇼츠 채널입니다! 롤큐 소속 크리에이터의 발로란트 하이라이트 쇼츠를 ...","writer":"롤큐 LOLQ 발로란트 쇼츠","title":"롤큐 LOLQ 발로란트 쇼츠","thumbnails":"https://yt3.ggpht.com/EmXY8rWvTZiDq-AWr13Z__igmKBU2PS60J2dm7RWUd7NW2v5swcwl1jjIwmJH7Fi4hcrxvDz=s88-c-k-c0xffffffff-no-rj-mo","createDate":"2016-01-11T02:35:42Z","heshtag":""}]}
                    //[{"link":"https://www.youtube.com/shorts/-JKACYYSf3s","description":"shorts #쇼츠#송화양 #메이플스토리 #돼장장이 ☆ 송화양 채널 구독하기 ▷ http://asq.kr/sLYWt 구독, 좋아요, 댓글, 알림설정은 저 ...","writer":"송화양 TV","title":"메이플에 사기꾼이 많은 이유","thumbnails":"https://i.ytimg.com/vi/-JKACYYSf3s/default.jpg","createDate":"2021-11-16T15:36:17Z","heshtag":"#쇼츠#송화양#메이플스토리#돼장장이"}]\
                    //[{"link":"https://www.youtube.com/shorts/YWaXv6AnWpo","description":"디아블로4말방어구 #디아블로4마갑획득 #하웨자르마갑 #쇼츠 #SHORTS.","writer":"[오시온(OhxioN)]","title":"지역 말 방어구 가장 빠르게 획득 하는 방법! #디아블로4 #SHORTS #쇼츠","thumbnails":"https://i.ytimg.com/vi/YWaXv6AnWpo/default.jpg","createDate":"2023-06-19T17:35:08Z","heshtag":"#디아블로4#SHORTS#쇼츠#디아블로4마갑획득#하웨자르마갑#쇼츠#SHORTS."}]
                    //[{"link":"https://www.youtube.com/shorts/b_-T9cN1w2M","description":"로스트아크#박서림 #일리아칸#웨이 LOST ARK OST. Smilegate RPG \u203b 박서림 '밥먹이기' * (TWIP) ...","writer":"박서림","title":"낭만+전율 에스더 웨이 &#39;일리아칸 히든&#39; #로스트아크  #shorts","thumbnails":"https://i.ytimg.com/vi/b_-T9cN1w2M/default.jpg","createDate":"2022-08-26T11:31:44Z","heshtag":"#39;일리아칸#39;#로스트아크#shorts#박서림#일리아칸#웨이"}]


                    JSONArray items = response.getJSONArray("items");

                    List<Board> boards = new ArrayList<>();

                    for (int i = 0; i < items.length(); i++) {
                        System.out.println("items : " + items.toString());
                        JSONObject json = items.getJSONObject(i);
                        Board board = new Board();
                        board.setCategoryG(categoryG);
                        board.setCategoryM(categoryM);
                        board.setCategoryS(categoryS);
                        String get_title = json.optString("title");
                        board.setTitle(get_title.replaceAll("<b>|</b>", ""));
                        board.setLink(json.optString("link"));

                        String get_description = json.optString("description");
                        board.setDescription(get_description.replaceAll("<b>|</b>", ""));
                        board.setThumbnails(json.optString("thumbnails"));
                        board.setHashtag(json.optString("heshtag"));
                        board.setWriter(json.optString("writer"));
                        board.setCreatedDate(json.optString("createDate"));

                        System.out.println("board : " + board.toString());
                        boards.add(board);
                    }
                    boardRepository.saveAll(boards);
                    /*
                        데이터 조회 시
                        429 Too Many Requests: "{"errorMessage":"Rate limit exceeded. (속도 제한을 초과했습니다.)","errorCode":"012"}"
                        오류가 발생하여 반복 실행 시 시간차를 두고 반복 실행
                     */
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

            }

        }

    }
}
