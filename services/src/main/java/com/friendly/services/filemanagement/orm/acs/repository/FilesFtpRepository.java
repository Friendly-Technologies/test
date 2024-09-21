package com.friendly.services.filemanagement.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.filemanagement.orm.acs.model.FilesFtpEntity;
import com.friendly.services.filemanagement.orm.acs.model.FilesFtpPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesFtpRepository extends BaseJpaRepository<FilesFtpEntity, FilesFtpPK> {

    @Query("select distinct f.groupId from FilesFtpEntity f")
    List<Long> getUsedGroupId();

    List<FilesFtpEntity> findAllByGroupIdAndFileTypeId(Long groupId, Integer fileTypeId);
    Optional<FilesFtpEntity> findByFileNameAndFileTypeId(String fileName, Integer fileTypeId);
    Optional<FilesFtpEntity> findByFileNameAndFileTypeIdAndDomainIdIn(String fileName, Integer fileTypeId, List<Integer> domainIds);
    Optional<FilesFtpEntity> findByFileNameAndGroupId(String fileName, Long groupId);
    Optional<FilesFtpEntity> findByFileNameAndGroupIdAndDomainIdIn(String fileName, Long groupId, List<Integer> domainIds);
    Optional<FilesFtpEntity> findByFileName(String fileName);
    Optional<FilesFtpEntity> findByFileNameAndDomainIdIn(String fileName, List<Integer> domainIds);
    List<FilesFtpEntity> findByFileNameIn(List<String> fileNames);
    List<FilesFtpEntity> findByFileNameInAndDomainIdIn(List<String> fileNames, List<Integer> domainIds);

    @Query("SELECT f FROM FilesFtpEntity f " +
            "INNER JOIN ProductClassGroupEntity p ON p.id = f.groupId " +
            "WHERE (:domainIds is null or f.domainId IN :domainIds) " +
            "AND (:protocolId is null or p.protocolId = :protocolId)" +
            "AND (:model is null or p.model = :model)" +
            "AND (:manufacturer is null or p.manufacturerName = :manufacturer)" +
            "AND (:fileTypeId is null or f.fileTypeId = :fileTypeId) order by p.manufacturerName")
    Page<FilesFtpEntity> findAllForListView(List<Integer> domainIds, Integer protocolId, Integer fileTypeId,
                                            String manufacturer, String model, Pageable pageable);

    @Query("SELECT f FROM FilesFtpEntity f " +
            "WHERE f.groupId = :groupId AND f.domainId IN (:domainIds) AND f.newest = 1")
    Optional<FilesFtpEntity> getNewestFirmwareObj(Long groupId, List<Integer> domainIds);
}
