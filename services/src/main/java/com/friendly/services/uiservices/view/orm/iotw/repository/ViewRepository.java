package com.friendly.services.uiservices.view.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.view.ViewType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.uiservices.view.orm.iotw.model.ViewEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link ViewEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ViewRepository extends BaseJpaRepository<ViewEntity, Long> {

    Optional<ViewEntity> getByIdAndType(final Long id, final ViewType type);

    @Query("SELECT v.id FROM ViewEntity v WHERE v.name = :name AND v.type = :type AND v.clientType = :clientType " +
            "AND v.domainId = :domainId")
    Optional<Long> findIdByNameClientTypeAndViewTypeForSuperDomain(final String name, final ViewType type, final ClientType clientType,
                                                                   final Integer domainId);
    @Query("SELECT v.id FROM ViewEntity v WHERE v.name = :name AND v.type = :type AND v.clientType = :clientType " +
            "AND (v.domainId IS NULL OR v.domainId = 0)")
    Optional<Long> findIdByNameClientTypeAndViewTypeForSuperDomain(final String name, final ViewType type, final ClientType clientType);

    @Query("SELECT v.id, v.name, v.defaultUser, v.defaultDomain FROM ViewEntity v WHERE v.clientType = :clientType " +
            "AND v.type = :type AND v.domainId = :domainId")
    List<Object[]> getSimpleViewsForDomain(final ClientType clientType, final ViewType type, final Integer domainId);

    @Query("SELECT v.id, v.name, v.defaultUser, v.defaultDomain FROM ViewEntity v WHERE v.clientType = :clientType " +
            "AND v.type = :type AND (v.domainId is null OR v.domainId = 0)")
    List<Object[]> getSimpleViewsForSuperDomain(final ClientType clientType, final ViewType type);

    @Query("SELECT v.id, v.name, v.defaultUser, v.defaultDomain FROM ViewEntity v WHERE v.clientType = :clientType " +
            "AND v.type = :type AND (:domainId is null or v.domainId = :domainId)")
    List<Object[]> getSimpleViews(final ClientType clientType, final ViewType type, final Integer domainId);

    @Query("SELECT v.id FROM ViewEntity v INNER JOIN ViewUserEntity vu ON v.id = vu.viewId " +
            "WHERE v.type = :type AND v.defaultUser = true AND vu.userId = :userId")
    Optional<Long> getDefaultUserView(final ViewType type, final Long userId);

    @Query("SELECT v.id FROM ViewEntity v WHERE v.defaultDomain = true AND v.type = :type AND v.clientType = :clientType " +
            "AND (:domainId is null and v.domainId is null or v.domainId = :domainId)")
    Optional<Long> getDefaultDomainView(final ViewType type, final ClientType clientType, final Integer domainId);

    @Modifying
    @Query("UPDATE ViewEntity v SET v.defaultDomain = false WHERE v.type = :type AND v.clientType = :clientType " +
            "AND (:domainId is null and v.domainId is null or v.domainId = :domainId) AND v.id <> :id")
    void resetDefaultDomain(final ViewType type, final ClientType clientType, final Integer domainId, final Long id);

    boolean existsById(Long id);

    @Query("SELECT v FROM ViewEntity v WHERE v.type = 'GroupUpdateView'")
    List<ViewEntity> getGroupUpdateConditions();
}
