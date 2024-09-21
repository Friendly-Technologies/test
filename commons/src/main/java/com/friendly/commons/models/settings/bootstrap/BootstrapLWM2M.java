package com.friendly.commons.models.settings.bootstrap;

import com.friendly.commons.models.settings.resource.AbstractResource;
import com.friendly.commons.models.settings.security.MaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BootstrapLWM2M  {

    private Integer id;
    private Instant updatedIso;
    private String updated;
    private String updater;
    private String name;
    private String mask;
    private MaskType maskType;

    private List<BootstrapSecurity> securities;
    private List<BootstrapServer> servers;
    private List<AbstractResource> resources;

}
