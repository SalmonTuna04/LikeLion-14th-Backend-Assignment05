package com.likelion.likelioncrud.member.domain;

import com.likelion.likelioncrud.member.api.dto.request.MemberUpdateRequestDto;
import com.likelion.likelioncrud.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Part part;

    // 로그인 시 사용하는 이메일 (중복 불가)
    @Column(unique = true)
    private String email;

    // BCrypt로 암호화된 비밀번호 저장
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    private Member(String name, int age, Part part, String email, String password) {
        this.name = name;
        this.age = age;
        this.part = part;
        this.email = email;
        this.password = password;
    }

    public void update(MemberUpdateRequestDto memberUpdateRequestDto) {
        this.name = memberUpdateRequestDto.name();
        this.age = memberUpdateRequestDto.age();
    }
}