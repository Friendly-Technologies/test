package com.friendly.commons.models.settings.resource;

import static com.friendly.commons.models.settings.resource.ProtocolResourceType.LWM2M;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ResourceLWM2M extends AbstractResource {

    @Builder
    public ResourceLWM2M(final Integer id,
                         final Integer objectId,
                         final String name,
                         final String description,
                         final ResourceType instanceType,
                         final String version,
                         final List<ResourceDetailsItem> items,
                         final List<ResourceDetails> parameters) {
        super(id, objectId, name, description, instanceType, version, items, parameters);
    }

}
