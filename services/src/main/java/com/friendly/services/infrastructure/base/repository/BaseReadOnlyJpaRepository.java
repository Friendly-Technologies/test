package com.friendly.services.infrastructure.base.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Base interface to JPA Repository
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@NoRepositoryBean
public interface BaseReadOnlyJpaRepository<T extends Serializable, ID extends Serializable> extends Repository<T, ID> {

        List<T> findAll();

        List<T> findAll(Sort sort);

        Page<T> findAll(Pageable pageable);

        Optional<T> findById(ID id);

        List<T> findAllById(Iterable<ID> ids);

        T getOne(ID id);

        <S extends T> List<S> findAll(Example<S> var1);

        <S extends T> List<S> findAll(Example<S> var1, Sort var2);
}
