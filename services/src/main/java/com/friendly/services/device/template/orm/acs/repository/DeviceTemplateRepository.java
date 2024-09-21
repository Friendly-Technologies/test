package com.friendly.services.device.template.orm.acs.repository;

import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.device.method.orm.acs.model.CpeMethodNameEntity;
import com.friendly.services.device.template.orm.acs.model.DeviceTemplateEntity;
import com.friendly.services.device.template.orm.acs.model.DeviceTemplatePK;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DeviceTemplateRepository extends BaseJpaRepository<DeviceTemplateEntity, DeviceTemplatePK> {
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into device_template " +
                    "(product_group_id, name_id, value, writeable) " +
                    "( " +
                    "select ?2, name_id, value, writeable " +
                    "from cpe_parameter " +
                    "where cpe_id = ?1 and (value is null or value != 'device_params_sub') " +
                    ")")
    void updateDeviceTemplate(Integer deviceId, Integer groupId);

    void deleteByGroupId(Integer groupId);

    @Query("SELECT 'y' FROM DeviceTemplateEntity p WHERE p.groupId = :groupId AND p.nameId = :nameId")
    String isParamExist(final Long groupId, final Long nameId);

    @Query(value = "SELECT 'y' FROM device_template p join cpe_parameter_name n on n.id=p.name_id WHERE p.product_group_id = :groupId AND n.name like :param limit 1", nativeQuery = true)
    String isParameterExistsLikeMysql(final Long groupId, final String param);

    @Query(value = "SELECT 'y' FROM device_template p join cpe_parameter_name n on n.id=p.name_id WHERE p.product_group_id = :groupId AND n.name like :param and rownum=1", nativeQuery = true)
    String isParameterExistsLikeOracle(final Long groupId, final String param);

    @Query(value = "SELECT 'y' FROM device_template_method tm join cpe_method_name m on m.id=tm.method_name_id WHERE tm.product_group_id = :groupId AND m.name like :param  limit 1", nativeQuery = true)
    String isMethodExistsLikeMysql(final Long groupId, final String param);

    @Query(value = "SELECT 'y' FROM device_template_method tm join cpe_method_name m on m.id=tm.method_name_id WHERE tm.product_group_id = :groupId AND m.name like :param and rownum=1", nativeQuery = true)
    String isMethodExistsLikeOracle(final Long groupId, final String param);

    @Query("SELECT p.parameterName.name FROM DeviceTemplateEntity p WHERE p.groupId = :groupId AND p.parameterName.name like :param")
    List<String> getParamNamesLike(final Long groupId, final String param);

    List<DeviceTemplateEntity> findAllByGroupId(Long groupId);

    @Query("SELECT p FROM DeviceTemplateEntity p INNER JOIN CpeParameterNameEntity pn ON p.nameId = pn.id " +
            "WHERE p.groupId = :groupId and pn.name like :fullName")
    List<DeviceTemplateEntity> findAllByGroupIdAndFullNameLike(final Long groupId, final String fullName);


    @Query(value = "SELECT 'y' FROM device_template t " +
            "inner join cpe_parameter_name pn ON t.name_id = pn.id " +
            "WHERE t.product_group_id = :groupId AND (pn.name like 'InternetGatewayDevice.IPPingDiagnostics.%' " +
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
    String isAnyDiagnosticsExistsMysql(final Long groupId);

    @Query(value = "SELECT 'y' FROM device_template t " +
            "inner join cpe_parameter_name pn ON t.name_id = pn.id " +
            "WHERE t.product_group_id = :groupId AND (pn.name like 'InternetGatewayDevice.IPPingDiagnostics.%' " +
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
    String isAnyDiagnosticsExistsOracle(final Long groupId);

    @Query("SELECT m FROM CpeMethodNameEntity m INNER JOIN DeviceTemplateMethodEntity t ON t.methodNameId = m.id WHERE t.groupId = :groupId ")
    List<CpeMethodNameEntity> getCpeMethodNameEntityByGroupId(Long groupId);



    @Query("SELECT p.parameterName.name FROM DeviceTemplateEntity p WHERE p.groupId = :groupId AND p.parameterName.name like :param AND p.writeable=1")
    List<String> getWriteParamNamesLike(Long groupId, String param);

    @Query(
            nativeQuery = true,
            value =
                    "SELECT 1 "
                            + "WHERE EXISTS ( "
                            + "    SELECT 1 "
                            + "    FROM device_template "
                            + "    WHERE product_group_id = ?1 "
                            + ");")
    List<Object[]> checkIfTemplateExists(Long id);
}
