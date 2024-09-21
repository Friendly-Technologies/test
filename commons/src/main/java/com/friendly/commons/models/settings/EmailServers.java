package com.friendly.commons.models.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmailServers {
    private EmailServer defaultServer;
    private EmailServer domainSpecificServer;
}
