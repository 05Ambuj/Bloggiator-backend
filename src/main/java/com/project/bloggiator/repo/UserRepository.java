package com.project.bloggiator.repo;

import com.project.bloggiator.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUserName(String userName);
    void deleteByUserName(String name);
    User findByEmail(String email);
}
