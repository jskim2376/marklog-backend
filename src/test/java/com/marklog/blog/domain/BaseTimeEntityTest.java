package com.marklog.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
class BaseTimeEntityImpl extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

}

@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class BaseTimeEntityTest {
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	public void testBaseTimeEntity() {
		// given
		LocalDateTime now = LocalDateTime.of(2000, 6, 4, 0, 0, 0);
		BaseTimeEntityImpl baseTimeEntityImpl = new BaseTimeEntityImpl();
		// when
		entityManager.persist(baseTimeEntityImpl);

		// then
		assertThat(baseTimeEntityImpl.getCreatedDate()).isAfter(now);
		assertThat(baseTimeEntityImpl.getModifiedDate()).isAfter(now);
	}
}
