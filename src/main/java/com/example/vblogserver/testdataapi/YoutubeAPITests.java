package com.example.vblogserver.testdataapi;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class YoutubeAPITests {

    @GetMapping("/v")
    public String getExample() {
        // RestTemplate 객체 생성
        //RestTemplate restTemplate = new RestTemplate();
        String keyword = "쇼츠 "+"강원도 여행";  // 쇼츠만 검색되어야하므로 "쇼츠 "는 필수. 찾아보니 별도로 쇼츠만 검색할 수 있는 api가 따로 제공되지는 않아서 키워드로 넘겨주어야 함.
        String youtubeapikey = "AIzaSyCp1VyoBwp1jP8KRntk3q8UN5ieC6fG5lI";
        String maxResults = "5"; // 조회 건수
        // GET 요청 보내고 응답 받기. 요청량 하루 횟수 제한으로 주석처리 함.
        //String url = "https://content-youtube.googleapis.com/youtube/v3/search?q="+keyword+"&part=snippet&maxResults="+maxResults+"&key="+youtubeapikey; // 원하는 GET API의 URL을 입력하세요.
        //String response = restTemplate.getForObject(url, String.class);
        String response = "{'kind':'youtube#searchListResponse','etag':'5xMzqDu-UiEfiPVcQAP1f0cE-XM','nextPageToken':'CAUQAA','regionCode':'KR','pageInfo':{'totalResults':300279,'resultsPerPage':5},'items':[{'kind':'youtube#searchResult','etag':'JMZ_Pu9PQEQpRFPcM3m0Wg_OOYo','id':{'kind':'youtube#video','videoId':'VvWlHjRD2fE'},'snippet':{'publishedAt':'2023-02-08T09:51:54Z','channelId':'UCvakL6TODG0Wm1ZFN3y13AA','title':'강원도 강릉 여행 60초 총정리\uD83D\uDE97\uD83D\uDCA8#shorts','description':'강원도여행 #강릉여행 #브이로그 ------------------------------ 비지니스 이메일 : s_the92@naver.com 인스타그램 @esther__park 네이버 ...','thumbnails':{'default':{'url':'https://i.ytimg.com/vi/VvWlHjRD2fE/default.jpg','width':120,'height':90},'medium':{'url':'https://i.ytimg.com/vi/VvWlHjRD2fE/mqdefault.jpg','width':320,'height':180},'high':{'url':'https://i.ytimg.com/vi/VvWlHjRD2fE/hqdefault.jpg','width':480,'height':360}},'channelTitle':'박에스더 - PARK ESTHER','liveBroadcastContent':'none','publishTime':'2023-02-08T09:51:54Z'}},{'kind':'youtube#searchResult','etag':'N2x0lGiOsY8gwDsA6IeVy0pymCg','id':{'kind':'youtube#video','videoId':'mP-yu7J7wwI'},'snippet':{'publishedAt':'2023-07-03T13:29:09Z','channelId':'UC74YTg6p9eSUq8-ztShRt4g','title':'#강원도 #강릉 #여행 #여름 #쇼츠 #속초 #양양 #자연','description':'','thumbnails':{'default':{'url':'https://i.ytimg.com/vi/mP-yu7J7wwI/default.jpg','width':120,'height':90},'medium':{'url':'https://i.ytimg.com/vi/mP-yu7J7wwI/mqdefault.jpg','width':320,'height':180},'high':{'url':'https://i.ytimg.com/vi/mP-yu7J7wwI/hqdefault.jpg','width':480,'height':360}},'channelTitle':'지금이진선','liveBroadcastContent':'none','publishTime':'2023-07-03T13:29:09Z'}},{'kind':'youtube#searchResult','etag':'PbgP0Dx6hoOtXwERj9Dv4H0RY3o','id':{'kind':'youtube#video','videoId':'4YlCUj2nju4'},'snippet':{'publishedAt':'2023-06-26T17:01:52Z','channelId':'UCgSGT3WawT1QwUnMbjk8S2A','title':'요즘 뜨는 강원도여행! 꼭 가보셨으면 하는 곳만 모았습니다. #여행에미치다 #국내여행추천 #국내여행 #국내여행지추천 #강원도여행 #강원도 #강원도가볼만한곳 #강원도여행지추천','description':'','thumbnails':{'default':{'url':'https://i.ytimg.com/vi/4YlCUj2nju4/default.jpg','width':120,'height':90},'medium':{'url':'https://i.ytimg.com/vi/4YlCUj2nju4/mqdefault.jpg','width':320,'height':180},'high':{'url':'https://i.ytimg.com/vi/4YlCUj2nju4/hqdefault.jpg','width':480,'height':360}},'channelTitle':'일상이 여행 Everyday travel','liveBroadcastContent':'none','publishTime':'2023-06-26T17:01:52Z'}},{'kind':'youtube#searchResult','etag':'igwVYULXxaQth3eW0yYJdGES1p0','id':{'kind':'youtube#video','videoId':'Jq0JhyTGV1s'},'snippet':{'publishedAt':'2023-06-03T14:13:20Z','channelId':'UCnnMvNRBm4_QR3Ws6Mwvkzg','title':'#국내여행 | #속초여행 #속초핫플 #강원도여행 #쇼츠 #shorts #trip #korea','description':'koreatravel #한국여행 #속초아이 #대관람차.','thumbnails':{'default':{'url':'https://i.ytimg.com/vi/Jq0JhyTGV1s/default.jpg','width':120,'height':90},'medium':{'url':'https://i.ytimg.com/vi/Jq0JhyTGV1s/mqdefault.jpg','width':320,'height':180},'high':{'url':'https://i.ytimg.com/vi/Jq0JhyTGV1s/hqdefault.jpg','width':480,'height':360}},'channelTitle':'고잉업스토어','liveBroadcastContent':'none','publishTime':'2023-06-03T14:13:20Z'}},{'kind':'youtube#searchResult','etag':'R-lu4PAvZHSSF6Xnapxl6o02uNs','id':{'kind':'youtube#video','videoId':'0AX9wJABick'},'snippet':{'publishedAt':'2021-10-25T23:33:56Z','channelId':'UC9JNV1bPeVgRW10tUbo2tsg','title':'양양 하조대\uD83C\uDFDEㅣ강원도 여행ㅣ바다ㅣtravel log','description':'','thumbnails':{'default':{'url':'https://i.ytimg.com/vi/0AX9wJABick/default.jpg','width':120,'height':90},'medium':{'url':'https://i.ytimg.com/vi/0AX9wJABick/mqdefault.jpg','width':320,'height':180},'high':{'url':'https://i.ytimg.com/vi/0AX9wJABick/hqdefault.jpg','width':480,'height':360}},'channelTitle':'쇼츠트립','liveBroadcastContent':'none','publishTime':'2021-10-25T23:33:56Z'}}]}";
        System.out.println(response);

        //String > JSON 변환
        JSONObject json = new JSONObject(response);

        //link 추출
        JSONObject link;

        //items 추출
        JSONArray items = json.getJSONArray("items");
        JSONObject snippet;
        String description;
        String title;
        //System.out.println(items);
        // json 재배열
        for (int i =0; i<items.length(); i++){
            snippet = items.getJSONObject(i).getJSONObject("snippet");
            System.out.println("~~~~~~~~~~~~"+i+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("boardid : 1");
            System.out.println("cateGid : Vlog");
            System.out.println("cateMid : 여행");
            System.out.println("cateSid : 강원도");
            System.out.println("title : " + snippet.optString("title"));
            System.out.println("channelTitle : " + snippet.optString("channelTitle"));
            System.out.println("createDate : " + snippet.optString("publishedAt"));
            link = items.getJSONObject(i).getJSONObject("id");
            System.out.println("link : https://www.youtube.com/shorts/" + link.optString("videoId"));
            System.out.println("profile_img : " + snippet.optString("channelId"));
            System.out.println("thumbnails : " + snippet.optString("thumbnails"));
            System.out.println("description : " + snippet.optString("description"));
            System.out.println("reviewCount : 100");
            System.out.println("likeCount : 100");
            System.out.println("disLikeCount : 100");
            System.out.println("grade : 100");

            title = snippet.optString("title");
            String gettitle[] = title.split("#");
            System.out.println("┏-------해시태그 추출------┐");
            System.out.println("|======제목======");
            for(int y=1; y<gettitle.length; y++){
                //System.out.println(getdescription[y]);
                //System.out.println(gettitle[y]);
                String hastag[] = gettitle[y].split(" ");
                System.out.println("|#"+hastag[0]);
            }
            System.out.println("|======내용에서 추출======");
            description = snippet.optString("description");
            //System.out.println(description);
            String getdescription[] = description.split("#");
            for(int y=1; y<getdescription.length; y++){
                //System.out.println(getdescription[y]);
                String hastag[] = getdescription[y].split(" ");
                System.out.println("|#"+hastag[0]);
            }
            System.out.println("└-------해시태그 추출------┘");

        }



        // 응답 내용 반환
        return null;
    }
}
