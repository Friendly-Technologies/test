package com.friendly.services.uiservices.user;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.settings.domain.DomainService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.USER_IS_NOT_FOUND;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceHelper {

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final DomainService domainService;

    public UserEntity getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(USER_IS_NOT_FOUND, userId));
    }

    public Integer getDomainId(final Long userId) {
        return getUser(userId).getDomainId();
    }

    public List<Long> getUserIdsByDomainId(final ClientType clientType, final Integer domainId) {
        if (isSuperDomain(domainId)) {
            return null;
        }
        List<Integer> domainIds = domainService.getChildDomainIds(domainId);

        return (domainIds == null || domainIds.isEmpty())
                ? null
                : userRepository.getUserIdsByDomains(clientType, domainIds);
    }
}
