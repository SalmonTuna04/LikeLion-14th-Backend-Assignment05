package com.likelion.likelioncrud.post.domain.repository;

import com.likelion.likelioncrud.member.domain.Member;
import com.likelion.likelioncrud.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByMember(Member member, Pageable pageable);
}