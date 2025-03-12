package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    private ModelMapper mapper;


    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {

        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        // convert DTo to entity
        Post post = maptoEntity(postDto);
        Post newPost =  postRepository.save(post);

        // convert entity to DTO
        return mapToDTO(newPost);
    }

    /**
     *
     * @param post
     * @return
     */

    private PostDto mapToDTO(Post post) {

        /**
         * 1. Create a new PostDto object
         * 2. Set the id, title, content, and description of the PostDto object to the id, title, content, and description of the Post object, respectively
         * 3. Return the PostDto object
         */
        //Old code
        //        PostDto postDto = new PostDto();
        //        postDto.setId(post.getId());
        //        postDto.setTitle(post.getTitle());
        //        postDto.setContent(post.getContent());
        //        postDto.setDescription(post.getDescription());
        //        return postDto;

        //New code
        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
    }

    /**
     *
     * @param postDto
     * @return
     */

    private Post maptoEntity(PostDto postDto) {
        /**
         * 1. Create a new Post object
         * 2. Set the title, content, and description of the Post object to the title, content, and description of the PostDto object, respectively
         * 3. Return the Post object
         */
        //Old code.
        //        Post post = new Post();
        //        post.setTitle(postDto.getTitle());
        //        post.setContent(postDto.getContent());
        //        post.setDescription(postDto.getDescription());
        //        return post;
        //new Code
        Post post = mapper.map(postDto, Post.class);
        return post;
    }

    @Override
    public PostResponse getAllPosts(int pageSize, int pageNo, String sortBy,  String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize, sort);
        //get content from Object
        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> listPost = posts.getContent();
        List<PostDto> contents = listPost.stream().map(this::mapToDTO
        ).collect(Collectors.toList());

        PostResponse postRespond = new PostResponse();
        postRespond.setContent(contents);
        postRespond.setPageNo(pageNo);
        postRespond.setPageSize(pageSize);
        postRespond.setTotalElements(posts.getTotalElements());
        postRespond.setTotalPages(posts.getTotalPages());
        postRespond.setLast(posts.isLast());
        return postRespond;


    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());
        Post updatedPost = postRepository.save(post);
        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

}
