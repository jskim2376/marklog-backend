package com.marklog.blog.web.dto;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HelloRequestAndResponseDto{
	private final String name;
	private final int amount;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		HelloRequestAndResponseDto other = (HelloRequestAndResponseDto) obj;
		return amount == other.amount && Objects.equals(name, other.name);
	}

}
