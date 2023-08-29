package com.example.vblogserver.init.naver;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NaverService {
    private final NaverResponseData naverResponseData;

    public NaverService(NaverResponseData naverService) {
        this.naverResponseData = naverService;
    }

    public JSONObject getNaverData(String keyword, int maxResults) {

        String apiUrl = "https://developers.naver.com/proxyapi/openapi/v1/search/blog?display="+maxResults+"&start=1&sort=sim&filter=all&query="+keyword;

        NaverEntity response = naverResponseData.requestDataFromNaverApi(apiUrl);
        String insertdata = "{\"items\":[";
        List<NaverEntity.Item> items = response.getItems();
        for (NaverEntity.Item item : items) {
            insertdata +="{\"title\":\"" + item.getTitle()+"\",";
            insertdata +="\"writer\":\"" + item.getBloggername()+"\",";

            String year = item.getPostdate().substring(0, 4);
            String month = item.getPostdate().substring(4, 6);
            String day = item.getPostdate().substring(6, 8);
            String formattedDate = year + "." + month + "." + day;
            insertdata +="\"createDate\":\"" + formattedDate+"\",";
            insertdata +="\"link\":\"" + item.getLink()+"\",";
            insertdata +="\"description\":\"" + item.getDescription()+"\",";

            String hashtagdata = "";
            String gettitle[] = item.getDescription().split("#");
            for(int y=1; y<gettitle.length; y++){
                String hastag[] = gettitle[y].split(" ");
                hashtagdata += "#"+hastag[0];
            }
            insertdata +="\"heshtag\":\""+hashtagdata+"\"},";
            insertdata += "]}";

        }
        JSONObject returndata = new JSONObject(insertdata);
        return returndata;
    }
}