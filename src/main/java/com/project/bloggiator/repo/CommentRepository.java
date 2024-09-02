package com.project.bloggiator.repo;

import com.project.bloggiator.entity.Comment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByBlogId(String blogId);
}
