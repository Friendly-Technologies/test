package com.friendly.commons.models.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents persistence version of ACS Address
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Connection implements Serializable {

    private String serviceName;
    private String title;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    public String getConnectionName() {
        return title != null
                ? title
                : host + ":" + port;
    }

}
