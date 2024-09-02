package com.project.bloggiator.repo;

import com.project.bloggiator.entity.BlogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BloggiatorRepository extends MongoRepository<BlogEntity,
        String> {
    Optional<BlogEntity> findByImageUrlsContaining(String imageUrl);
}

