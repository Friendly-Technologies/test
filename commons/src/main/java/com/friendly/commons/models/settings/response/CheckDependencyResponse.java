package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.DomainDependency;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckDependencyResponse {
    private boolean exist;
    private List<DomainDependency> dependencies;
}
