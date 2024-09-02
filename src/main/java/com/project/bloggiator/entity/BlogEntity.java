package com.project.bloggiator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "blog_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogEntity {

    @Id
    private String id;
    @NonNull
    private String title;
    @NonNull
    private String content;
    private List<String> imageUrls = new ArrayList<>();
    private LocalDateTime date;
    private String authorName;
    @DBRef
    private User author;
    @DBRef
    private List<Comment> comments;
}