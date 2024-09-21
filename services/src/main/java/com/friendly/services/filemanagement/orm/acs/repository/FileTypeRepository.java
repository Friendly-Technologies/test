package com.friendly.services.filemanagement.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.filemanagement.orm.acs.model.FileTypeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to interact with persistence layer to store {@link FileTypeEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface FileTypeRepository extends BaseJpaRepository<FileTypeEntity, Integer> {

    List<FileTypeEntity> findAllByProtocolId(final Integer protocolId);

    @Query(nativeQuery = true, value = "select all ft.id from file_type ft where ft.protocol_id = :protocolId")
    List<Integer> findAllFileTypeIdsByProtocolId(final Integer protocolId);

    @Query(nativeQuery = true, value = "select f.file_name, f.file_date FROM cpe c " +
            "inner join product_class p on c.product_class_id = p.id " +
            "inner join files_ftp f on p.group_id = f.group_id " +
            "where c.id = :deviceId and f.file_type = :fileTypeId and f.location_id in :domainIds")
    List<Object[]> getDeviceActivityTaskNames(Long deviceId, Integer fileTypeId, List<Integer> domainIds);


    @Query(nativeQuery = true, value = "select  f.file_name, f.file_date FROM cpe c " +
            "inner join product_class p on c.product_class_id = p.id " +
            "inner join files_ftp f on p.group_id = f.group_id " +
            "where c.id = :deviceId and f.file_type = :fileTypeId")
    List<Object[]> getDeviceActivityTaskNames(Long deviceId, Integer fileTypeId);

    List<FileTypeEntity> findAllByType(String type);

    @Query("SELECT f.name FROM FileTypeEntity f where f.id = :id")
    String findNameById(final Integer id);

}
