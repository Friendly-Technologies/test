package com.friendly.commons.models.settings.iot;

import com.friendly.commons.models.settings.bootstrap.BootstrapLWM2M;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of IOT controller
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AddIotBootstrapConfigBody implements Serializable {
    private BootstrapLWM2M config;
    
    
}
