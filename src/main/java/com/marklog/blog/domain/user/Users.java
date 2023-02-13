package com.marklog.blog.domain.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.marklog.blog.domain.BaseTimeEntity;
import com.marklog.blog.domain.post.Post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=50, nullable = false, unique=true)
    private String email;


    @Column(length=20, nullable = false)
    private String name;


    @Column(length=200, nullable = false)
    private String picture;

    @Column(length=100)
    private String introduce;

    @Column(length=20, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    List<Post> posts = new ArrayList<>();


    @Builder
    public Users(String name, String email, String picture, String title, String introduce, Role role){
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.title = title;
        this.introduce = introduce;
        this.role = role;
    }

    public Users update(String name, String picture, String title, String introduce){
        this.name = name;
        this.picture = picture;
        this.title = title;
        this.introduce = introduce;

        return this;
    }

    public Map<String, Object> toAttributes(){
    	Map <String, Object> map = new HashMap<>();
    	map.put("id", this.id);
    	map.put("email", this.email);
    	return map;

    }

    public String getRoleKey(){
        return this.role.getKey();
    }

}