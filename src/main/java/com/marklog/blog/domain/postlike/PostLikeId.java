package com.marklog.blog.domain.postlike;

import java.io.Serializable;
import java.util.Objects;

public class PostLikeId implements Serializable{
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostLikeId other = (PostLikeId) obj;
		return Objects.equals(post, other.post) && Objects.equals(user, other.user);
	}
	
	
}
