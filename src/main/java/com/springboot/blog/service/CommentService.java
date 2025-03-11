package com.springboot.blog.service;

import com.springboot.blog.payload.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, CommentDto commentDto);
    CommentDto getCommentById(long postId, long commentId);
    List<CommentDto> getCommentsByPostId(long postId);
}
