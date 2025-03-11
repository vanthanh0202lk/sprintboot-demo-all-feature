package com.springboot.blog.payload;

import lombok.Data;

@Data
public class PostDto {
    private long id;
    private String title;
    private String content;
    private String description;

}
