package com.friendly.services.device.parameterstree.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterCpeNameValueProjection;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterNameIdValueProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Clob;
import java.util.List;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link CpeParameterEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface DeviceParameterRepository extends BaseJpaRepository<CpeParameterEntity, Long>,
        PagingAndSortingRepository<CpeParameterEntity, Long> {

    @Query("SELECT p.value FROM CpeParameterEntity p WHERE p.cpeId = :deviceId AND p.nameId = :nameId")
    Optional<String> getParamValue(final Long deviceId, final Long nameId);

    List<CpeParameterEntity> findAllByCpeId(final Long cpeId);


    @Query("SELECT pn FROM CpeParameterEntity cp INNER JOIN CpeParameterNameEntity pn ON cp.nameId = pn.id " +
            "WHERE pn.name LIKE :objectName AND pn.type <> 'Object Instance' AND cp.writeable = 1")
    List<CpeParameterNameEntity> findAllWritableNotObjectInstanceParamsLike(String objectName);

    @Query("SELECT p FROM CpeParameterEntity p INNER JOIN CpeParameterNameEntity pn ON p.nameId = pn.id " +
            "WHERE p.cpeId = :cpeId and pn.name like :fullName")
    List<CpeParameterEntity> findAllByCpeIdAndFullNameLike(final Long cpeId, final String fullName);

    @Query("SELECT 'y' FROM CpeParameterEntity p WHERE p.cpeId = :deviceId AND p.nameId = :nameId")
    String isParamExist(final Long deviceId, final Long nameId);

    @Query(value = "SELECT 'y' FROM cpe_parameter p join cpe_parameter_name n on n.id=p.name_id WHERE p.cpe_id = :deviceId AND n.name like :param limit 1", nativeQuery = true)
    String isParameterExistsLikeMysql(final Long deviceId, final String param);
    @Query(value = "SELECT 'y' FROM cpe_parameter p join cpe_parameter_name n on n.id=p.name_id WHERE p.cpe_id = :deviceId AND n.name like :param and rownum=1", nativeQuery = true)
    String isParameterExistsLikeOracle(final Long deviceId, final String param);

    @Query("SELECT pn.name FROM CpeParameterEntity p INNER JOIN CpeParameterNameEntity pn ON p.nameId = pn.id " +
            "WHERE p.cpeId = :deviceId AND pn.name like :param")
    List<String> getParamNamesLike(final Long deviceId, final String param);

    @Query("SELECT p.value FROM CpeParameterEntity p INNER JOIN CpeParameterNameEntity pn ON p.nameId = pn.id " +
            "WHERE p.cpeId = :deviceId AND pn.name like :param")
    List<String> getParamValuesLike(final Long deviceId, final String param);


    Optional<CpeParameterEntity> findByCpeIdAndNameId(final Long cpeId, final Long nameId);

    @Query(nativeQuery = true, value = "SELECT pe.value " +
            "FROM cpe_parameter_extended pe WHERE pe.cpe_id = ?1")
    List<String> getDeviceLogMySql(Long cpeId);

    @Query(nativeQuery = true, value = "SELECT pe.value " +
            "FROM cpe_parameter_extended pe WHERE pe.cpe_id = ?1")
    List<Clob> getDeviceLogOracle(Long cpeId);

    @Query(value = "SELECT 'y' FROM cpe_parameter_extended pe WHERE pe.cpe_id = :deviceId limit 1", nativeQuery = true)
    String isParameterExtendedByDeviceIdMysql(final Long deviceId);
    @Query(value = "SELECT 'y' FROM cpe_parameter_extended pe WHERE pe.cpe_id = :deviceId  and rownum=1", nativeQuery = true)
    String isParameterExtendedByDeviceIdOracle(final Long deviceId);

    @Query(value = "SELECT pe.value FROM cpe_parameter_extended pe WHERE pe.id = :id ", nativeQuery = true)
    String getParameterExtendedValueById(final Long id);

    @Query(value = "SELECT 'y' FROM cpe_parameter p " +
            "inner join cpe_parameter_name pn ON p.name_id = pn.id " +
            "WHERE p.cpe_id = :deviceId AND (pn.name like 'InternetGatewayDevice.IPPingDiagnostics.%' " +
            "or pn.name like 'Device.IP.Diagnostics.%' " +
            "or pn.name like 'Device.LAN.IPPingDiagnostics.%' " +
            "or pn.name like '%.WANDevice.%.WANDSLDiagnostics.%' " +
            "or pn.name like 'InternetGatewayDevice.TraceRouteDiagnostics.%' " +
            "or pn.name like 'Device.LAN.TraceRouteDiagnostics.%' " +
            "or pn.name like 'Device.WiFi.Neighboring%Diagnostic%' " +
            "or pn.name like 'InternetGatewayDevice.WiFi.Neighboring%Diagnostic.%' " +
            "or pn.name like '%WANDevice.%.WANATMF5LoopbackDiagnostics.%' " +
            "or pn.name like 'InternetGatewayDevice.DownloadDiagnostics.%' " +
            "or pn.name like 'Device.DownloadDiagnostics.%' " +
            "or pn.name like 'InternetGatewayDevice.UploadDiagnostics.%' " +
            "or pn.name like 'Device.UploadDiagnostics.%' " +
            "or pn.name like 'Device.DNS.Diagnostics.NSLookupDiagnostics%' " +
            "or pn.name like '%.NSLookupDiagnostics.') and rownum=1", nativeQuery = true)
    String isAnyDiagnosticsExistsOracle(final Long deviceId);

    @Query(value = "SELECT 'y' FROM cpe_parameter p " +
            "inner join cpe_parameter_name pn ON p.name_id = pn.id " +
            "WHERE p.cpe_id = :deviceId AND (pn.name like 'InternetGatewayDevice.IPPingDiagnostics.%' " +
            "or pn.name like 'Device.IP.Diagnostics.%' " +
            "or pn.name like 'Device.LAN.IPPingDiagnostics.%' " +
            "or pn.name like '%.WANDevice.%.WANDSLDiagnostics.%' " +
            "or pn.name like 'InternetGatewayDevice.TraceRouteDiagnostics.%' " +
            "or pn.name like 'Device.LAN.TraceRouteDiagnostics.%' " +
            "or pn.name like 'Device.WiFi.Neighboring%Diagnostic%' " +
            "or pn.name like 'InternetGatewayDevice.WiFi.Neighboring%Diagnostic.%' " +
            "or pn.name like '%WANDevice.%.WANATMF5LoopbackDiagnostics.%' " +
            "or pn.name like 'InternetGatewayDevice.DownloadDiagnostics.%' " +
            "or pn.name like 'Device.DownloadDiagnostics.%' " +
            "or pn.name like 'InternetGatewayDevice.UploadDiagnostics.%' " +
            "or pn.name like 'Device.UploadDiagnostics.%' " +
            "or pn.name like 'Device.DNS.Diagnostics.NSLookupDiagnostics%' " +
            "or pn.name like '%.NSLookupDiagnostics.')  limit 1", nativeQuery = true)
    String isAnyDiagnosticsExistsMysql(final Long deviceId);

    List<CpeParameterCpeNameValueProjection> findAllByCpeIdInAndNameIdIn(List<Long> cpeIds, List<Long> nameIds);

    List<CpeParameterNameIdValueProjection> findAllByCpeIdAndNameIdIn(Long cpeId, List<Long> nameIds);


    @Transactional
    @Modifying
    void deleteAllByCpeIdAndNameId(Long cpeId, Long nameId);

}
