package com.friendly.commons.models.settings;

import com.friendly.commons.models.auth.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents persistence version of Email Server Setting
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SnmpServer implements Serializable {

    private ClientType id;
    private String host;
    private String port;
    private String community;
    private SnmpVersionType version;

}
