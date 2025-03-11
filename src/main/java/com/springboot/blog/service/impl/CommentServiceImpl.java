package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentReposity;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private CommentReposity commentRepository;

    public CommentServiceImpl(CommentReposity commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    private Comment mapToEntity(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        return comment;
    }

    private CommentDto mapToDTO(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());
        commentDto.setPostId(comment.getPost().getId());
        return commentDto;
    }

    @Override
    @Transactional
    public CommentDto createComment(long postId, CommentDto commentDto) {
        try {
                System.out.println("thanh commentDto: "+commentDto) ;

                Comment comment = mapToEntity(commentDto);
                    System.out.println("thanh commentMain: "+comment);

                // retrieve post entity by id
                Post post = postRepository.findById(postId).orElseThrow(
                        () -> new ResourceNotFoundException("Post", "id", postId));

                // set post to comment entity
                comment.setPost(post);
                System.out.println("thanh comment: "+comment);
                //return null;

                // comment entity to DB
                Comment newComment =  commentRepository.save(comment);

                return mapToDTO(newComment);
        } catch (OptimisticLockingFailureException e) {
            // Handle the optimistic locking failure
            throw new RuntimeException("Failed to save comment due to concurrent modification", e);
        }
    }

    @Override
    public CommentDto getCommentById(long postId, long commentId) {
        // retrieve post entity by id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // retrieve comment entity by id and post
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId));
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment not found with id " + commentId);
        }
        return  mapToDTO(comment);

    }




    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream().map(
                this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto updateComment(long postId, long commentId, CommentDto commentDtoRequest) {
        // retrieve post entity by id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // retrieve comment entity by id and post
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment not found with id: " + commentId);
        }
        comment.setName(commentDtoRequest.getName());
        comment.setEmail(commentDtoRequest.getEmail());
        comment.setBody(commentDtoRequest.getBody());

        Comment updateComment = commentRepository.save(comment);
        return mapToDTO(updateComment);
    }

}
