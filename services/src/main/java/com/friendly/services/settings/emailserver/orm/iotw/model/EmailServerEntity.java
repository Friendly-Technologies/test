package com.friendly.services.settings.emailserver.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "iotw_email_server")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailServerEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private ClientType id;

    @Column(name = "domain_id", nullable = false)
    private Integer domainId;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private String port;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "destination")
    private String from;

    @Column(name = "subject")
    private String subject;

    @Column(name = "enable_ssl")
    private boolean enableSSL;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EmailServerEntity that = (EmailServerEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
