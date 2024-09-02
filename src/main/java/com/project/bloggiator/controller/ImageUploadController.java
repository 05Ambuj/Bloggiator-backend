package com.project.bloggiator.controller;

import com.project.bloggiator.service.CloudinaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0" +
        ".1:5500", "https://05ambuj.github.io", "http://localhost:5501",
        "http://127.0.0" +
                ".1:5501" })
@RequestMapping("/api/v1/images")
public class ImageUploadController {

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImage(@RequestParam("file") MultipartFile[] file) {
        try {
            List<String> imageUrls = cloudinaryImageService.uploadImages(file);
            return ResponseEntity.ok(imageUrls);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonList("Image upload failed: " + e.getMessage()));
        }
    }
}

