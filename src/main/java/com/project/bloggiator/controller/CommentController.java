package com.project.bloggiator.controller;

import com.project.bloggiator.entity.Comment;
import com.project.bloggiator.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0" +
        ".1:5500", "https://05ambuj.github.io", "http://localhost:5501",
        "http://127.0.0" +
                ".1:5501" })
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<String> addComment(@RequestBody Comment comment) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            comment.setAuthor(username);
            comment.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));

            commentService.saveComment(comment);
            return ResponseEntity.ok("Comment added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add comment: " + e.getMessage());
        }
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<Comment>> getCommentsByBlogId(@PathVariable String blogId) {
        List<Comment> comments = commentService.findByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable String id, @RequestBody Comment updatedComment) {
        Optional<Comment> existingCommentOpt = commentService.findById(id);

        if (existingCommentOpt.isPresent()) {
            Comment existingComment = existingCommentOpt.get();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            if (!existingComment.getAuthor().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only edit your own comments");
            }

            existingComment.setContent(updatedComment.getContent());
            existingComment.setTimestamp(LocalDateTime.now());

            commentService.saveComment(existingComment);
            return ResponseEntity.ok("Comment updated successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable String id) {
        Optional<Comment> existingCommentOpt = commentService.findById(id);

        if (existingCommentOpt.isPresent()) {
            Comment existingComment = existingCommentOpt.get();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            if (!existingComment.getAuthor().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own comments");
            }

            commentService.deleteComment(id);
            return ResponseEntity.ok("Comment deleted successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
    }
}
