package com.friendly.commons.models.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

/**
 * Model that represents persistence version of Email Server Setting
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EmailServer implements Serializable {

    String host;
    String port;
    String username;
    String password;
    String from;
    String subject;
    boolean enableSSL;

}
