package com.friendly.services.settings.domain;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.exceptions.FriendlyPermissionException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.request.CheckDependencyRequest;
import com.friendly.commons.models.settings.response.CheckDependencyResponse;
import com.friendly.commons.models.user.Domain;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.info.orm.acs.model.DomainEntity;
import com.friendly.services.device.info.orm.acs.repository.DomainRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.domain.mapper.DomainMapper;
import com.friendly.services.settings.usergroup.UserGroupService;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.ftacs.Exception_Exception;
import com.ftacs.IntegerArrayWS;
import com.ftacs.IspListWS;
import com.ftacs.IspWS;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.friendly.commons.models.settings.DomainDependency.ACS_USER;
import static com.friendly.commons.models.settings.DomainDependency.USER;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.*;
import static com.friendly.services.infrastructure.utils.CommonUtils.SUPER_DOMAIN_NAME;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

/**
 * Service that exposes the base functionality for interacting with {@link Domain} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DomainService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final DomainRepository domainRepository;

    @NonNull
    private final DomainMapper domainMapper;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final InterfaceService interfaceService;

    @NonNull
    private final UserGroupService userGroupService;

    public boolean isDomainsEnabled(ClientType clientType) {
        return getDomainMode(clientType) > 0;
    }

    public int getDomainMode(ClientType clientType) {
        return interfaceService.getInterfaceValue(clientType, "IspEnable")
                .map(Integer::parseInt)
                .orElse(0);
    }

    /**
     * Get all Domains
     */
    public List<DomainEntity> getDomains() {
        return domainRepository.findAll();
    }


    /**
     * Get Domain Setting
     *
     * @return {@link Domain} setting
     */
    public Domain getDomain(final String token) {
        final Long userId = jwtService.getUserIdByHeaderAuth(token);
        return getDomainByUserId(userId);
    }

    /**
     * Create Domain Setting
     *
     * @return {@link Domain} setting
     */
    public Domain createOrUpdateDomain(final String token, final Domain domain) {
        final Session session = jwtService.getSession(token);
        validateDomainNames(domain);
        final ClientType clientType = session.getClientType();
        final DomainEntity currentDomain = domain.getId() != null ? domainRepository.findById(domain.getId())
                .orElse(null) : null;
        final String parentName = getParentName(currentDomain);

        final IspListWS ispListWS = domainMapper.domainToIsp(parentName, domain);
        ispListWS.getIsps().removeIf(ispWS -> ispWS.getId() != null);

        addDomains(clientType, ispListWS);
        updateDomainNames(clientType, domain, currentDomain, parentName);

        return getDomainByUserId(session.getUserId());
    }

    private void addDomains(final ClientType clientType, final IspListWS ispListWS) {
        try {
            final List<IspWS> ispWSList = ispListWS.getIsps();
            if (!ispWSList.isEmpty()) {
                validateDomainNames(ispWSList);
                AcsProvider.getAcsWebService(clientType).addIsps(ispListWS);
            }
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION_USER_READABLE, e.getMessage());
        }
    }

    private void updateDomainNames(final ClientType clientType, final Domain domain,
                                   final DomainEntity currentDomain, final String parentName) {
        if (currentDomain != null
                && StringUtils.isNotBlank(domain.getName())
                && !domain.getName().equals(getDomainName(currentDomain))) {
            final Domain domains = domainMapper.domainEntitiesToDomainUpdate(
                    domainRepository.getDomains(currentDomain.getId()));
            if (domains != null) {
                domains.setName(domain.getName());
                final List<IspWS> isps = domainMapper.domainToIsp(parentName, domains).getIsps();
                validateDomainNames(isps);
                isps.forEach(isp -> updateDomainName(clientType, isp));
            }
        }
    }

    private void validateDomainNames(final List<IspWS> ispWSList) {
        ispWSList.forEach(this::validateDomainName);
    }

    private void validateDomainNames(final Domain domain) {
        if (domain == null) return;
        if (domain.getName().contains(".")) {
            throw new FriendlyIllegalArgumentException(ILLEGAL_CHARACTER, domain.getName());
        }
        if (domain.getItems() == null) return;

        domain.getItems()
                .forEach(this::validateDomainNames);
    }

    private void validateDomainName(final IspWS ispWS) {
        if (ispWS.getName().equals(SUPER_DOMAIN_NAME)
                || domainRepository.getDomainIdByName(ispWS.getName()).isPresent()) {
            throw new FriendlyIllegalArgumentException(DOMAIN_NAME_IS_NOT_UNIQUE, ispWS.getName());
        }
    }

    private IspWS updateDomainName(final ClientType clientType, final IspWS isp) {
        try {
            return AcsProvider.getAcsWebService(clientType).updateIsp(isp);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION_USER_READABLE, e.getMessage());
        }
    }

    /**
     * Delete Domain Setting
     */
    @Transactional
    public Boolean deleteDomains(final String token, final List<Integer> domainIds) {
        final ClientType clientType = jwtService.getClientTypeByHeaderAuth(token);

        final IntegerArrayWS integerArrayWS = new IntegerArrayWS();
        final List<Integer> ids = new ArrayList<>();

        CheckDependencyResponse checkDependencyResponse = userGroupService.checkDependencyExist(token, new CheckDependencyRequest(domainIds));

        if(checkDependencyResponse.isExist()) {
            if(checkDependencyResponse.getDependencies().contains(USER)) {
                throw new FriendlyIllegalArgumentException("One of the domains depends on a user, cannot be deleted", 500);
            } else if(checkDependencyResponse.getDependencies().contains(ACS_USER)) {
                throw new FriendlyIllegalArgumentException("One of the domains depends on an acs user, cannot be deleted", 500);
            } else {
                throw new FriendlyIllegalArgumentException("One of the domains depends on a device, cannot be deleted", 500);
            }
        }

        domainIds.stream()
                .filter(Objects::nonNull)
                .forEach(domainId -> domainRepository.getDomains(domainId)
                        .stream()
                        .map(DomainEntity::getId)
                        .forEach(id -> {
                            if (userRepository.existsForDomain(domainId).isPresent()) {
                                throw new FriendlyPermissionException(NO_PERMISSION, "Domain: " + domainId + " contains users");
                            }
                            userRepository.deleteDomainFromUser(id);
                            ids.add(id);
                        }));
        integerArrayWS.getId().addAll(ids.stream()
                .distinct()
                .collect(Collectors.toList()));

        try {
            return AcsProvider.getAcsWebService(clientType).removeIsps(integerArrayWS);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION_USER_READABLE, e.getMessage());
        }
    }

    public List<Integer> getChildDomainIds(final Integer domainId) {

        List<Integer> domainIds;
        if (isSuperDomain(domainId)) {
            domainIds = domainRepository.findAll()
                    .stream()
                    .map(DomainEntity::getId)
                    .collect(Collectors.toList());
            domainIds.add(0);
            domainIds.add(-1);
            domainIds.add(null);
        } else {
            domainIds = domainRepository.getDomains(domainId)
                    .stream()
                    .map(DomainEntity::getId)
                    .collect(Collectors.toList());
        }
        return domainIds;
    }



    public List<Integer> getDomainIds(UserResponse user) {
        final List<Integer> domainIds;
        if (user.getDomainId() == null || user.getDomainId() == 0) {
            domainIds = null;
        } else {
            domainIds = getDomainIdByUserId(user.getId())
                    .map(this::getChildDomainIds)
                    .orElse(null);
        }
        return domainIds;
    }

    public String getDomainNameById(final Integer domainId) {
        if (isSuperDomain(domainId)) {
            return SUPER_DOMAIN_NAME;
        }
        return domainRepository.findById(domainId)
                .map(DomainEntity::getName)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(DOMAIN_NOT_FOUND, domainId));
    }

    private Domain getDomainByUserId(final Long userId) {
        return getDomainIdByUserId(userId).map(domainRepository::getDomains)
                .map(domainMapper::domainEntitiesToDomain)
                .orElseGet(this::getSuperDomain);
    }

    public Optional<Integer> getDomainIdByUserId(final Long userId) {
        return userRepository.getDomainId(userId);
    }

    public Integer getParentDomainId(final Integer domainId) {
        final Optional<String> nameOpt = domainRepository.getDomainNameById(domainId);
        if (!nameOpt.isPresent()) {
            return -1;
        }
        final String name = nameOpt.get();
        final int index = name.lastIndexOf(".");
        if (index != -1) {
            return domainRepository.getDomainIdByName(name.substring(0, index))
                    .orElse(-1);
        }
        return -1;
    }

    public List<Integer> getDomainIdsByName(final String domainName) {
        if (domainName == null || domainName.equals(SUPER_DOMAIN_NAME)) {
            return null;
        }
        return domainRepository.getDomainIdsByName(domainName);
    }

    private Domain getSuperDomain() {
        return Domain.builder()
                .id(0)
                .name(SUPER_DOMAIN_NAME)
                .fullName(SUPER_DOMAIN_NAME)
                .items(new LinkedHashSet<>(domainMapper.domainEntitiesToDomains(domainRepository.findAll())))
                .build();
    }

    private String getParentName(final DomainEntity domainEntity) {
        if (domainEntity == null) {
            return null;
        }

        final int index = domainEntity.getName().lastIndexOf(".");

        if (index != -1) {
            return domainEntity.getName()
                    .substring(0, index);
        }
        return null;
    }

    private String getDomainName(final DomainEntity domainEntity) {
        final int index = domainEntity.getName().lastIndexOf(".");

        if (index != -1) {
            return domainEntity.getName()
                    .substring(index + 1);
        }

        return domainEntity.getName();
    }

}
