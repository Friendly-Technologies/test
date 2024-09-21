package com.friendly.services.uiservices.view.orm.iotw.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewUserEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewUserPK;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link ViewUserEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ViewUserRepository extends BaseJpaRepository<ViewUserEntity, ViewUserPK> {

    List<ViewUserEntity> getAllByUserId(final Long userId);
}
