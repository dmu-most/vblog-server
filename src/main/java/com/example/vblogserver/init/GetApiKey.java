package com.example.vblogserver.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GetApiKey {
    @Value("${apikey.youtube}")
    private String youtubeApiKey;

    @Value("${apikey.naver.id}")
    private String naverApiKeyId;

    @Value("${apikey.naver.secret}")
    private String naverApiKeySecret;

    public String getYoutubeApiKey(){
        return youtubeApiKey;
    }

    public String getNaverApiKeyId() {
        return naverApiKeyId;
    }

    public String getNaverApiKeySecret() {
        return naverApiKeySecret;
    }
}
