package com.project.bloggiator.controller;

import com.project.bloggiator.entity.BlogEntity;
import com.project.bloggiator.entity.Comment;
import com.project.bloggiator.entity.User;
import com.project.bloggiator.service.BloggiatorService;
import com.project.bloggiator.service.CloudinaryImageService;
import com.project.bloggiator.service.CommentService;
import com.project.bloggiator.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0" +
        ".1:5500", "https://05ambuj.github.io", "http://localhost:5501",
        "http://127.0.0" +
                ".1:5501" })
@RequestMapping("/bloggiator")

@Slf4j
public class BlogController {

    @Autowired
    private BloggiatorService bloggiatorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @GetMapping("/all-blogs")
    public ResponseEntity<?> getAllBlogsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        if (user != null) {
            List<BlogEntity> allBlogs = user.getBlogEntries();
            if (allBlogs != null && !allBlogs.isEmpty()) {
                return new ResponseEntity<>(allBlogs, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<BlogEntity>> getAllBlogs() {
        List<BlogEntity> allBlogs = bloggiatorService.getAllBlogs();
        if (allBlogs != null && !allBlogs.isEmpty()) {
            return new ResponseEntity<>(allBlogs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    @PostMapping
    public ResponseEntity<BlogEntity> createBlog(@RequestParam("title") String title,
                                                 @RequestParam("content") String content,
                                                 @RequestPart(value = "files", required = false) MultipartFile[] files) {
        try {
            BlogEntity blogEntry = new BlogEntity();
            blogEntry.setTitle(title);
            blogEntry.setContent(content);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            blogEntry.setAuthorName(userName);
            blogEntry.setDate(LocalDateTime.now());
            // Handle image uploads if files are provided
            if (files != null && files.length > 0) {
                List<String> imageUrls = cloudinaryImageService.uploadImages(files);
                blogEntry.setImageUrls(imageUrls);
            }
            // Save the blog entry
            bloggiatorService.saveEntry(blogEntry, userName);
            return new ResponseEntity<>(blogEntry, HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("Error uploading images", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error creating blog entry", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-comment")
    public ResponseEntity<?> addComment(@RequestBody Map<String, String> request) {
        String blogId = request.get("blogId");
        String content = request.get("content");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        Optional<BlogEntity> blogEntityOpt = bloggiatorService.blogById(blogId);
        if (blogEntityOpt.isPresent()) {
            BlogEntity blogEntity = blogEntityOpt.get();

            Comment comment = new Comment();
            comment.setContent(content);
            comment.setAuthor(userName);
            comment.setTimestamp(LocalDateTime.now());

            List<Comment> comments = blogEntity.getComments();
            if (comments == null) {
                comments = new ArrayList<>();
            }
            comments.add(comment);

            blogEntity.setComments(comments);
            bloggiatorService.saveEntry(blogEntity, userName);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found");
    }

@GetMapping("id/{ID}")
public ResponseEntity<BlogEntity> blogById(@PathVariable String ID) {
    // Fetch the blog post by ID
    Optional<BlogEntity> blogEntity = bloggiatorService.blogById(ID);

    if (blogEntity.isPresent()) {
        BlogEntity blog = blogEntity.get();

        // Fetch comments associated with the blog post
        List<Comment> comments = commentService.findByBlogId(ID);
        blog.setComments(comments);

        return new ResponseEntity<>(blog, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

    @DeleteMapping("id/{ID}")
    public ResponseEntity<?> deleteById(@PathVariable String ID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = bloggiatorService.deleteBlogById(ID, userName);
        if (removed) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


        @PutMapping("id/{ID}")
        public ResponseEntity<?> updateBlogById(@PathVariable String ID,
                                                @RequestParam("title") String title,
                                                @RequestParam("content") String content,
                                                @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUserName(userName);
            List<BlogEntity> collect = user.getBlogEntries().stream()
                    .filter(x -> x.getId().equals(ID))
                    .collect(Collectors.toList());

            if (!collect.isEmpty()) {
                Optional<BlogEntity> blogEntity = bloggiatorService.blogById(ID);
                if (blogEntity.isPresent()) {
                    BlogEntity oldEntry = blogEntity.get();
                    oldEntry.setTitle(title != null && !title.isEmpty() ? title : oldEntry.getTitle());
                    oldEntry.setContent(content != null && !content.isEmpty() ? content : oldEntry.getContent());

                    if (files != null && files.length > 0) {
                        List<String> existingImageUrls = oldEntry.getImageUrls() != null ? oldEntry.getImageUrls() : new ArrayList<>();
                        List<String> newImageUrls = cloudinaryImageService.uploadImages(files);
                        existingImageUrls.addAll(newImageUrls);
                        oldEntry.setImageUrls(existingImageUrls);
                    }

                    bloggiatorService.saveEntry(oldEntry);
                    return new ResponseEntity<>(oldEntry, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    @PostMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");

        Optional<BlogEntity> blogEntityOpt = bloggiatorService.findByImageUrl(imageUrl);
        if (blogEntityOpt.isPresent()) {
            BlogEntity blogEntity = blogEntityOpt.get();
            blogEntity.getImageUrls().remove(imageUrl);
            cloudinaryImageService.deleteImage(imageUrl);
            bloggiatorService.saveEntry(blogEntity);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
    }
}
