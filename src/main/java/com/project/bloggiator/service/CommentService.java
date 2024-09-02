package com.project.bloggiator.service;

import com.project.bloggiator.entity.Comment;
import com.project.bloggiator.repo.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
    public List<Comment> findByBlogId(String blogId) {
        return commentRepository.findByBlogId(blogId);
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }
}