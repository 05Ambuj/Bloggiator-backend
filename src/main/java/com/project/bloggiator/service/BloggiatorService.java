package com.project.bloggiator.service;

import com.project.bloggiator.controller.PublicController;
import com.project.bloggiator.entity.BlogEntity;
import com.project.bloggiator.entity.User;
import com.project.bloggiator.repo.BloggiatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class BloggiatorService {
    private static final Logger log = LoggerFactory.getLogger(BloggiatorService.class);
    @Autowired
    private BloggiatorRepository bloggiatorRepository;

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(BlogEntity blogEntry, String userName) {
        try {
            User user = userService.findByUserName(userName);
            blogEntry.setDate(LocalDateTime.now());
            BlogEntity savedEntry = bloggiatorRepository.save(blogEntry);
            user.getBlogEntries().add(savedEntry);
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("Error saving blog entry", e);
            throw new RuntimeException("An error occurred while saving the entry!", e);
        }
    }

    @Transactional
    public void saveEntry(BlogEntity blogEntry, String userName, MultipartFile[] files) {
        try {
            User user = userService.findByUserName(userName);
            if (files != null && files.length > 0) {
                List<String> imageUrls = cloudinaryImageService.uploadImages(files);
                blogEntry.setImageUrls(imageUrls);
            }

            blogEntry.setDate(LocalDateTime.now());
            BlogEntity savedEntry = bloggiatorRepository.save(blogEntry);
            user.getBlogEntries().add(savedEntry);
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("Error saving blog entry with images", e);
            throw new RuntimeException("An error occurred while saving the entry!", e);
        }
    }

    public void saveEntry(BlogEntity blogEntry) {
        try {
            bloggiatorRepository.save(blogEntry);
        } catch (Exception e) {
            log.error("Error saving blog entry", e);
            throw new RuntimeException("An error occurred while saving the entry!", e);
        }
    }

    public Optional<BlogEntity> blogById(String id) {
        return bloggiatorRepository.findById(id);
    }


    public BlogEntity findById(String id) {
        Optional<BlogEntity> blog = bloggiatorRepository.findById(id);
        return blog.orElse(null);
    }

    @Transactional
    public boolean deleteBlogById(String id, String userName) {
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
            removed = user.getBlogEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                bloggiatorRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Error deleting blog entry", e);
            throw new RuntimeException("An error occurred while deleting the entry", e);
        }
        return removed;
    }

    public Optional<BlogEntity> findByImageUrl(String imageUrl) {
        return bloggiatorRepository.findByImageUrlsContaining(imageUrl);
    }

    public List<BlogEntity> getAllBlogs() {
        return bloggiatorRepository.findAll();
    }
}