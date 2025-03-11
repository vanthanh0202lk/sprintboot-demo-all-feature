package com.springboot.blog.repository;

import com.springboot.blog.entity.Post;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;


public interface PostRepository extends JpaRepository<Post, Long> {
    //code here.

}
