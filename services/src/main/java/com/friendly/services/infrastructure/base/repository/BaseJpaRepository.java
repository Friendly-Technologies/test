package com.friendly.services.infrastructure.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Base interface to JPA Repository
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@NoRepositoryBean
public interface BaseJpaRepository<E extends Serializable, ID extends Serializable> extends JpaRepository<E, ID> {
}
