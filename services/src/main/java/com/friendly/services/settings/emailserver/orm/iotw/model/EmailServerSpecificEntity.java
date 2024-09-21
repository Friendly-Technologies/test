package com.friendly.services.settings.emailserver.orm.iotw.model;

import com.friendly.commons.models.auth.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "iotw_email_server_domain_specific")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailServerSpecificEntity implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "domain_id", nullable = false)
    private Integer domainId;

    @Column(name = "client_type", nullable = false)
    private ClientType clientType;

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
        EmailServerSpecificEntity that = (EmailServerSpecificEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
