package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.ServerDetails;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileServers {
    private List<ServerDetails> defaultServers;
    private List<ServerDetails> domainSpecificServers;
}
