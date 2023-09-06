package com.example.vblogserver.init.naver;
import com.example.vblogserver.init.GetApiKey;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NaverResponseData {
    private final GetApiKey getApiKey;

    public NaverResponseData(GetApiKey getApiKey) {
        this.getApiKey = getApiKey;
    }

    public NaverEntity requestDataFromNaverApi(String apiUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", getApiKey.getNaverApiKeyId()); // 네이버 API 클라이언트 ID
        //System.out.println("naverapikeyid : "+getApiKey.getNaverApiKeyId());
        headers.set("X-Naver-Client-Secret", getApiKey.getNaverApiKeySecret()); // 네이버 API 클라이언트 Secret
        //System.out.println("naverapikeysecret : "+getApiKey.getNaverApiKeySecret());
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverEntity> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, new HttpEntity<>(headers), NaverEntity.class);

        return response.getBody();
    }
}