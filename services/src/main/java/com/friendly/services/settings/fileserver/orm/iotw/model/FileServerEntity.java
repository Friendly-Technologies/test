package com.friendly.services.settings.fileserver.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.model.AbstractEntity;
import com.friendly.services.uiservices.system.orm.iotw.model.ServerDetailsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Model that represents persistence version of File Server Entity
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "iotw_file_server")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FileServerEntity extends AbstractEntity<Long> {

    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

    @Column(name = "domain_id", nullable = false)
    private Integer domainId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_details_id", referencedColumnName = "id")
    private List<ServerDetailsEntity> serverDetails;

}
