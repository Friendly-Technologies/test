package com.friendly.services.device.activity.orm.acs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Model that represents persistence version of CPE
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SqlResultSetMapping(name = "Mapping.DeviceActivityDetailsEntity",
        classes = @ConstructorResult(targetClass = DeviceActivityDetailsEntity.class,
                columns = {@ColumnResult(name = "name"),
                        @ColumnResult(name = "value"),
                        @ColumnResult(name = "creator")}))
@SqlResultSetMapping(name = "Mapping.DeviceActivityDetailsNameCreatorEntity",
        classes = @ConstructorResult(targetClass = DeviceActivityDetailsEntity.class,
                columns = {@ColumnResult(name = "name"),
                        @ColumnResult(name = "creator")}))
@SqlResultSetMapping(name = "Mapping.DeviceActivitySetAttribDetailsEntity",
        classes = @ConstructorResult(targetClass = DeviceActivitySetAttribDetailsEntity.class,
                columns = {@ColumnResult(name = "name"),
                        @ColumnResult(name = "notification"),
                        @ColumnResult(name = "accessList"),
                        @ColumnResult(name = "creator")}))

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamForSetAttribute",
        query = "select n.name as name, a.access_list as accessList, a.notification as notification, a.creator as creator " +
                "from cpe_prov_attrib_his a, cpe_parameter_name n " +
                "where n.id = a.name_id and a.task_id=:id",
        resultSetMapping = "Mapping.DeviceActivitySetAttribDetailsEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getCreatorFromFileHistory",
        query = "select url as name, creator from cpe_file_history where task_id=:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsNameCreatorEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromProvisionHistory",
        query = "select n.name as name, p.value as value, p.creator as creator " +
                "from cpe_provision_history p inner join cpe_parameter_name n on p.name_id = n.id " +
                "where p.task_id =:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsEntity")


@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromObjectHistory",
        query = "select n.name as name, p.creator as creator FROM cpe_prov_obj_history p" +
                " inner join cpe_parameter_name n on n.id=p.name_id where p.task_id =:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsNameCreatorEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromObjectParamHistory",
        query = "select concat(n.name, ph.name) as name, " +
                "ph.value as value, ph.creator as creator from cpe_prov_obj_parameter_history ph " +
                "inner join cpe_prov_obj_instance_history ih on ih.cpe_provision_object_id = ph.cpe_provision_object_id " +
                "inner join cpe_parameter_name n on n.id = ih.name_id " +
                "where ph.task_id=:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromProvAttrHis",
        query = "select n.name as name, a.creator as creator " +
                "from cpe_prov_attrib_his a inner join cpe_parameter_name n on n.id = a.name_id where a.task_id =:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsNameCreatorEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromCustomRpcHistory",
        query = "select request_message as name, creator from custom_rpc_history where task_id=:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsNameCreatorEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromOp",
        query = "select 'Install' as name, h.url as value, h.creator as creator " +
                "from cpe_op_install_his h where h.task_id =:id " +
                "union all select concat('Update ', h.uuid)  as name, h.url as value, h.creator as creator " +
                "from cpe_op_update_his h where h.task_id =:id " +
                "union all select concat('Uninstall ', h.uuid)  as name, null as value, h.creator as creator " +
                "from cpe_op_uninstall_his h where h.task_id =:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsEntity")

@NamedNativeQuery(name = "DeviceActivityDetailsEntity.getTaskParamFromDeleteObject",
        query = "select n.name as name, p.creator as creator " +
                "FROM cpe_delete_provision_object p inner join cpe_parameter_name n on n.id = p.name_id " +
                "where p.id =:id",
        resultSetMapping = "Mapping.DeviceActivityDetailsNameCreatorEntity")
public class DeviceActivityDetailsEntity implements Serializable {

    public DeviceActivityDetailsEntity(String name, String creator) {
        this.name = name;
        this.creator = creator;
    }

    @Id
    private String name;
    private String value;
    private String creator;
}
