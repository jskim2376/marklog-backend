package com.marklog.blog.config.auth.dto;

import java.util.Map;

import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.Users;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private String title;

    @Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email,
			String picture, String title) {
		super();
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.title = title;
	}

    public Users toEntity(){
        return Users.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .title(title)
                .role(Role.USER)
                .build();
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeKey, Map<String, Object> attributes){
        return ofGoogle(userNameAttributeKey, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeKey, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .title(((String) attributes.get("email")).split("@")[0])
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeKey)
                .build();
    }

}