package com.project.hmartweb.web.controllers;

import com.project.hmartweb.application.services.file.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    @Autowired
    private IFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestPart List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(fileService.uploadFile(file));
        }
        return ResponseEntity.ok(urls);
    }
}
