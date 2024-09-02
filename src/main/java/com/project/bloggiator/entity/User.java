package com.project.bloggiator.entity;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    private String userName;
    @Indexed(unique = true)
    private String email;
    private String name;
    @NonNull
    private String password;
    @DBRef
    private List<BlogEntity>blogEntries=new ArrayList<>();
    private List<String> roles;
}
