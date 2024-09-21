package com.friendly.services.settings.fileserver.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.fileserver.orm.iotw.model.FileServerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link FileServerEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface FileServerRepository extends BaseJpaRepository<FileServerEntity, Long> {

    @Query("SELECT fs FROM FileServerEntity fs WHERE fs.clientType = :clientType AND fs.domainId = :domainId")
    FileServerEntity getFileServerEntity(final ClientType clientType, final Integer domainId);

    Optional<FileServerEntity> findByClientTypeAndDomainId(ClientType clientType, Integer domainId);

    @Query(nativeQuery = true, value =
            "SELECT dt.address FROM iotw_file_server fs JOIN iotw_server_details dt ON dt.server_details_id=fs.id " +
                    "WHERE fs.client_type = :clientType and dt.name like :serverType")
    List<String> getServerList(ClientType clientType, String serverType);


    @Query("SELECT f.domainId FROM FileServerEntity f WHERE f.id = :id")
    Integer getDomainIdByServerDetailsId(Long id);
}
