package com.example.vblogserver.init.youtube;

import com.example.vblogserver.init.GetApiKey;
import com.example.vblogserver.init.JsonEscapeUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;




@Service
public class YoutubeService {
    private final GetApiKey getApiKey;
    private final JsonEscapeUtil jsonEscapeUtil;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://content-youtube.googleapis.com/youtube/v3/search";
    private final String shorts = "쇼츠 ";

    public YoutubeService(GetApiKey getApiKey, JsonEscapeUtil jsonEscapeUtil) {
        this.getApiKey = getApiKey;
        this.jsonEscapeUtil = jsonEscapeUtil;
    }


    public JSONObject getYoutubeData(String keyword, int maxResults) {
        String apikey = getApiKey.getYoutubeApiKey();
        String url  = "https://content-youtube.googleapis.com/youtube/v3/search?q="+shorts+" "+keyword+"&part=snippet&maxResults="+maxResults+"&key="+apikey;
        String response = restTemplate.getForObject(url, String.class);

        //String > JSON 변환
        JSONObject json = new JSONObject(response);

        //link 추출
        JSONObject link;

        //썸네일 추출
        JSONObject thum;
        JSONObject thum2;

        //items 추출
        JSONArray items = json.getJSONArray("items");
        JSONObject snippet;
        String description;
        String title;
        String hashtagdata;
        // 추출할 데이터 값 셋팅
        String insertdata = "{\"items\":[";
        for (int i = 0; i < items.length(); i++) {
            hashtagdata = "";
            snippet = items.getJSONObject(i).getJSONObject("snippet");
            // 제목
            insertdata +="{\"title\":\""+snippet.optString("title")+"\",";
            System.out.println("Title : "+snippet.optString("title"));
            //게시자
            System.out.println("ChannelTitle : "+snippet.optString("channelTitle"));
            insertdata +="\"writer\":\""+snippet.optString("channelTitle")+"\",";
            //작성일자
            insertdata +="\"createDate\":\""+snippet.optString("publishedAt")+"\",";

            link = items.getJSONObject(i).getJSONObject("id");
            System.out.println("link : https://www.youtube.com/shorts/" + link.optString("videoId"));
            insertdata +="\"link\":\"https://www.youtube.com/shorts/"+link.optString("videoId")+"\",";
            System.out.println("profile_img : " + snippet.optString("channelId"));
            thum = new JSONObject(snippet.optString("thumbnails"));
            thum2 = new JSONObject(thum.optString("default"));
            System.out.println("thumbnails : " + thum2.optString("url"));
            insertdata +="\"thumbnails\":\""+thum2.optString("url")+"\",";
            System.out.println("description : " + jsonEscapeUtil.escapeDoubleQuotes(snippet.optString("description")));

            insertdata +="\"description\":\""+jsonEscapeUtil.escapeDoubleQuotes(snippet.optString("description"))+"\",";
            title = snippet.optString("title");
            String gettitle[] = title.split("#");
            //해시태그 추출
            //제목에서 추출
            for (int y = 1; y < gettitle.length; y++) {
                String hastag[] = gettitle[y].split(" ");
                hashtagdata += "#" + hastag[0];
            }
            //내용에서 추출
            description = snippet.optString("description");
            String getdescription[] = description.split("#");
            for (int y = 1; y < getdescription.length; y++) {
                String hastag[] = getdescription[y].split(" ");
                hashtagdata += "#" + hastag[0];
            }
            if(i == items.length()-1) insertdata +="\"heshtag\":\""+hashtagdata+"\"}";
            else insertdata +="\"heshtag\":\""+hashtagdata+"\"},";
        }
        insertdata += "]}";
        //System.out.println("insertdata : "+insertdata);
        JSONObject returndata = new JSONObject(insertdata);
        //System.out.println("returndata"+returndata.toString());
        //추출한 데이터로 재구성한 json 데이터 반환
        return returndata;
    }
}