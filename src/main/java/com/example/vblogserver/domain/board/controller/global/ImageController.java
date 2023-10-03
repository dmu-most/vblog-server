package com.example.vblogserver.domain.board.controller.global;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
@Controller
public class ImageController {
    private String imgdir = "/home/ubuntu/Img";

    @GetMapping("/img/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> saveFile(@PathVariable String fileName) {
        Path filePath = Paths.get(imgdir).resolve(fileName);
        Resource resource = getResource(filePath);

        if (resource != null && resource.exists() && resource.isReadable()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            // 이미지가 404인 경우
            Path fallbackImagePath = Paths.get(imgdir).resolve("vblog_logo.png");
            Resource fallbackResource = getResource(fallbackImagePath);

            if (fallbackResource != null && fallbackResource.exists() && fallbackResource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(fallbackResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    private Resource getResource(Path filePath) {
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}