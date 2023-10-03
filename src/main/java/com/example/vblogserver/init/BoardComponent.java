package com.example.vblogserver.init;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.init.naver.NaverImgSaveService;
import com.example.vblogserver.init.naver.NaverImgScrapService;
import com.example.vblogserver.init.naver.NaverService;
import com.example.vblogserver.init.tmp.*;
import com.example.vblogserver.init.youtube.YoutubeService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class BoardComponent implements CommandLineRunner {
    private final BoardRepository boardRepository;
    private final YoutubeService youtubeService;
    private final NaverService naverService;
    private final TmpDisAndLikeCount tmpLikeCount;
    private final TmpReview tmpReview;
    private final TmpGrade tmpGrade;
    private final TmpBookMark tmpBookMark;
    private final NaverImgScrapService naverImgScrapService;
    private final NaverImgSaveService naverImgSaveService;
    private final TmpClick tmpClick;

    @Autowired
    public BoardComponent(BoardRepository boardRepository, YoutubeService youtubeService, NaverService naverService, TmpDisAndLikeCount tmpLikeCount, TmpReview tmpReview, TmpGrade tmpGrade, TmpBookMark tmpBookMark, NaverImgScrapService naverImgScrapService, NaverImgSaveService naverImgSaveService, TmpClick tmpClick){
        this.boardRepository = boardRepository;
        this.youtubeService = youtubeService;
        this.naverService = naverService;
        this.tmpLikeCount = tmpLikeCount;
        this.tmpReview = tmpReview;
        this.tmpGrade = tmpGrade;
        this.tmpBookMark = tmpBookMark;
        this.naverImgScrapService = naverImgScrapService;
        this.naverImgSaveService = naverImgSaveService;
        this.tmpClick = tmpClick;
    }

    @Override
    public void run(String... arg){
        // 카테고리 항목
        String[][] category_name = {
                {"1","1|2","국내여행|해외여행"},
                {"2","3|4|5|6|7|8","롤|발로란트|메이플|피파 온라인 4|디아블로4|로스트아크"},
                {"3","9|10|11","건강기능식품|운동|식이요법"},
                {"4","12|13|14","카페추천|양식|한식"},
                {"5","15|16","예능|드라마"},
                {"6","17|18","남성뷰티|여성뷰티"}
        };

        // 조회 최대 건수
        int maxResults = 10;
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
                    if (z==1) response = youtubeService.getYoutubeData(categoryS_name[y], maxResults);
                        //blog
                    else if (z==2) response = naverService.getNaverData(categoryS_name[y], maxResults);
                    //System.out.println(response);
                    JSONArray items = response.getJSONArray("items");

                    List<Board> boards = new ArrayList<>();

                    for (int i = 0; i < items.length(); i++) {
                        //System.out.println("items : " + items.toString());
                        JSONObject json = items.getJSONObject(i);
                        Board board = new Board();
                        board.setCategoryG(categoryG);
                        board.setCategoryM(categoryM);
                        board.setCategoryS(categoryS);
                        String get_title = json.optString("title");
                        board.setTitle(get_title.replaceAll("<b>|</b>", ""));
                        board.setLink(json.optString("link"));
                        String thumbnailsLink = "";
                        board.setThumbnails(json.optString("thumbnails"));
                        /*
                        // blog 일 경우 API를 이용하여 썸네일 이미지 url 을 받아옴
                        board.setThumbnails(json.optString("thumbnails"));
                        /*
                        if(z==1) board.setThumbnails(json.optString("thumbnails"));
                            // vlog 일 경우 이미지 크롤링하여 이미지 url을 받아옴
                        else if(z==2) {
                            try {
                                thumbnailsLink = naverImgScrapService.scrapeFirstImage(json.optString("link"));
                                board.setThumbnails(thumbnailsLink);
                            } catch (IOException e) {
                                board.setThumbnails(json.optString("thumbnails"));
                            }
                        }

                         */
                        String get_description = json.optString("description");
                        board.setDescription(get_description.replaceAll("<b>|</b>", ""));
                        board.setHashtag(json.optString("heshtag"));
                        board.setWriter(json.optString("writer"));
                        String get_createDate = json.optString("createDate");
                        get_createDate = get_createDate.replaceAll("-", ".");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                        LocalDate createdDate = LocalDate.parse(get_createDate, formatter);
                        board.setCreatedDate(createdDate);
                        boards.add(board);
                    }
                    boardRepository.saveAll(boards);





                    /*
                        데이터 조회 시
                        429 Too Many Requests: "{"errorMessage":"Rate limit exceeded. (속도 제한을 초과했습니다.)","errorCode":"012"}"
                        오류가 발생하여 반복 실행 시 시간차를 두고 반복 실행
                     */
                    /*
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    */
                }
            }
        }
        /*
                     이미지 url 을 호출하여 파일로 저장하는 로직 추가.
                     네이버 블로그의 경우 스크랩 차단 기능이 설정되어 있어 프론트에서 호출하면 403 에러가 발생하며 차단됨.
                     ∴ 백엔드에서 파일로 저장하여 백엔드에서 이미지를 전달해주도록 변경.
                     게시글의 ID 값을 파일명으로 저장하기 위해 ID가 할당되는 시점인 saveAll 이 실행된 이후로 적용
                     */

        //System.out.println("진입");
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        List<Board> boards = boardRepository.findByCategoryG(categoryG);
        List<Board> boardsCopy = new ArrayList<>(boards);
        for (Board board : boardsCopy) {
            // 게시글 ID 조회
            Long id = board.getId();

            //임시조치
            board.setThumbnails("http://ec2-3-39-126-215.ap-northeast-2.compute.amazonaws.com/img/"+id+".png");
            //naverImgSaveService.scrapeFirstImage2(board.getThumbnails(), id);
            /*
            if(board.getThumbnails().equals("")) board.setThumbnails("");
            else board.setThumbnails("http://ec2-3-39-126-215.ap-northeast-2.compute.amazonaws.com/img/"+id+".png");

             */

            //board.setThumbnails(thumbnailsLink);
            boards.add(board);
        }
        boardRepository.saveAll(boards);

        // 테스트용 데이터 inset : id가 5인 게시글의 좋아요 30 으로 설정
        tmpLikeCount.updateBoardDisAndLikeCount();
        // 테스트용 데이터 insert : 리뷰 insert (평점 + 후기)
        tmpReview.updateTmpReview();
        // 테스트용 데이터 insert : 평점 insert
        tmpGrade.updateGrade();
        // 테스트용 데이터 insert : 찜 insert
        tmpBookMark.updateTmpBookMark();
        // 테스트용 데이터 insert : 클릭 정보 insert
        tmpClick.updateTmpClick();

    }
}
