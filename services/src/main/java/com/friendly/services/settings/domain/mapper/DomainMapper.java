package com.friendly.services.settings.domain.mapper;

import com.friendly.commons.models.user.Domain;
import com.friendly.services.device.info.orm.acs.model.DomainEntity;
import com.ftacs.IspListWS;
import com.ftacs.IspWS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DomainMapper {

    public Domain domainEntitiesToDomain(final List<DomainEntity> domainEntities) {
        final List<Domain> domains = domainEntitiesToDomains(domainEntities);

        return domains.isEmpty() ? null : domains.get(0);
    }

    public List<Domain> domainEntitiesToDomains(final List<DomainEntity> domainEntities) {
        if (domainEntities == null || domainEntities.isEmpty()) {
            return Collections.emptyList();
        }
        final Map<String, DomainEntity> domainEntityMap = getDomainEntityMap(domainEntities);

        List<DomainEntity> domains = sortDomains(new ArrayList<>(domainEntityMap.values()));

        return domains.stream()
                .map(this::domainEntityToDomain)
                .collect(Collectors.toList());
    }


    public List<DomainEntity> sortDomains(List<DomainEntity> domains) {

        domains.forEach(domain -> {
            if(!domain.getDomains().isEmpty()) {
                domain.setDomains(sortDomains(domain.getDomains()));
            }
        });

        return domains.stream()
                .sorted(Comparator.comparing(DomainEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

    }

    public Domain domainEntitiesToDomainUpdate(final List<DomainEntity> domainEntities) {
        final List<Domain> domains = domainEntitiesToDomainsUpdate(domainEntities);

        return domains.isEmpty() ? null : domains.get(0);
    }

    private List<Domain> domainEntitiesToDomainsUpdate(final List<DomainEntity> domainEntities) {
        if (domainEntities == null || domainEntities.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        final Map<String, DomainEntity> domainEntityMap = getDomainEntityMap(domainEntities);

        return domainEntityMap.values()
                .stream()
                .map(this::domainEntityToDomain)
                .collect(Collectors.toList());

    }

    private Map<String, DomainEntity> getDomainEntityMap(final List<DomainEntity> domainEntities) {
        final String parentName = domainEntities.get(0).getName().contains(".")
                ? domainEntities.get(0).getName().substring(domainEntities.get(0).getName().lastIndexOf(".") + 1)
                : domainEntities.get(0).getName(); // used for the cases where subdomain has a NOT Super Domain parent

        final Map<String, DomainEntity> domainEntityMap =
                domainEntities.stream()
                        .collect(Collectors.toMap(DomainEntity::getName, domainEntity ->
                                {
                                    domainEntity.setFullName(domainEntity.getName());
                                    final int index = domainEntity.getName().lastIndexOf(".");
                                    domainEntity.setDomains(new ArrayList<>());
                                    if (index != -1) {
                                        domainEntity.setParentId(domainEntity.getName().substring(0, index));
                                        domainEntity.setName(domainEntity.getName().substring(index + 1));
                                    }
                                    return domainEntity;
                                }, (u, v) -> {
                                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                                },
                                LinkedHashMap::new
                        ));
        domainEntityMap.values().stream()
                .filter(d -> d.getParentId() != null)
                .forEach(domainEntity -> {
                    domainEntityMap.computeIfPresent(domainEntity.getParentId(), (key, value) -> {
                        value.getDomains().add(domainEntity);
                        return value;
                    });
                });
        return domainEntityMap.entrySet().stream()
                .filter(d -> d.getValue().getParentId() == null || d.getValue().getName().equals(parentName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Domain domainEntityToDomain(final DomainEntity domainEntity) {
        return Domain.builder()
                .id(domainEntity.getId())
                .name(domainEntity.getName())
                .fullName(domainEntity.getFullName())
                .items(nestedDomainEntitiesToDomains(domainEntity.getDomains()))
                .build();

    }

    private Set<Domain> nestedDomainEntitiesToDomains(final List<DomainEntity> domainEntities) {
        if (domainEntities == null) {
            return Collections.EMPTY_SET;
        }

        return domainEntities.stream()
                .filter(Objects::nonNull)
                .map(this::domainEntityToDomain)
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }

    public IspListWS domainToIsp(final String parentName, final Domain domain) {
        final String domainName = StringUtils.isNotBlank(parentName)
                ? parentName + "." + domain.getName()
                : domain.getName();
        final IspListWS ispWS = new IspListWS();

        final IspWS mainDomain = new IspWS();
        mainDomain.setId(domain.getId());
        mainDomain.setName(domainName);

        ispWS.getIsps().add(mainDomain);
        ispWS.getIsps().addAll(domainsToIsps(domainName, domain.getItems()));

        return ispWS;
    }

    private Set<IspWS> domainsToIsps(final String parentName, final Set<Domain> domains) {
        if (domains == null) {
            return Collections.EMPTY_SET;
        }

        return domains.stream()
                .filter(Objects::nonNull)
                .flatMap(domain -> domainToIsp(parentName, domain).getIsps().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }


}
