package com.marklog.blog.domain.postlike;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PostLikeIdClass implements Serializable{
	private Long post;
	private Long user;

	@Override
	public int hashCode() {
		return Objects.hash(post, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		PostLikeIdClass other = (PostLikeIdClass) obj;
		return Objects.equals(post, other.post) && Objects.equals(user, other.user);
	}


}
