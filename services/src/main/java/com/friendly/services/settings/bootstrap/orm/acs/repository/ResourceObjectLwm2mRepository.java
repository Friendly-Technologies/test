package com.friendly.services.settings.bootstrap.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapResourceLwm2mEntity;
import org.springframework.stereotype.Repository;


@Repository
public interface ResourceObjectLwm2mRepository extends BaseJpaRepository<BootstrapResourceLwm2mEntity, Integer> {

}
