package com.friendly.services.settings.acs;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.AcsUserBody;
import com.friendly.commons.models.settings.AcsUsersBody;
import com.friendly.commons.models.settings.acs.AcsUser;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.acs.orm.acs.model.AcsUserEntity;
import com.friendly.services.settings.acs.orm.acs.repository.AcsUserRepository;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.userinterface.InterfaceService;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.friendly.services.infrastructure.utils.PasswordUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FORMAT_NOT_SUPPORTED;
import static java.lang.Boolean.FALSE;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcsUserService {

    public static final String LOGIN = "login";
    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final AcsUserRepository userRepository;

    @NonNull
    private final InterfaceService interfaceService;

    @NonNull
    private final DomainService domainService;

    public FTPage<AcsUser> getAcsUsers(final String token, final AcsUsersBody body) {
        final Session session = jwtService.getSession(token);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(),
                body.getPageSize(), null, LOGIN);
        final Integer userDomainId = body.getDomainId();

        final boolean domainsEnable = domainService.isDomainsEnabled(session.getClientType());
        final List<Integer> domainIds;
        if (domainsEnable) {
            final Integer domainId = userDomainId != null
                    ? userDomainId
                    : domainService.getDomainIdByUserId(session.getUserId())
                    .orElse(-1);
            if (!domainId.equals(-1)) {
                domainIds = domainService.getChildDomainIds(domainId);
            } else {
                domainIds = null;
            }
        } else {
            domainIds = null;
        }

        final List<Page<AcsUserEntity>> acsUserEntities =
                pageable.stream()
                        .map(p -> userRepository
                                .findAll(getSearchFilters(domainIds, body.getSearchParam(), body.getSearchExact()), p))
                        .collect(Collectors.toList());
        final List<AcsUser> acsUsers =
                acsUserEntities.stream()
                        .map(Page::getContent)
                        .flatMap(entities -> entities.stream()
                                .map(entity -> AcsUser.builder()
                                        .login(entity.getLogin())
                                        .build()))
                        .collect(Collectors.toList());

        final FTPage<AcsUser> page = new FTPage<>();
        return page.toBuilder()
                .items(acsUsers)
                .pageDetails(PageUtils.buildPageDetails(acsUserEntities))
                .build();
    }

    private static Specification<AcsUserEntity> getSearchFilters(final List<Integer> domainIds,
                                                                 final String searchParam,
                                                                 final Boolean searchExact) {
        return (root, cq, cb) -> {
            final Predicate domainPredicate = domainIds == null ? null :
                    cb.in(root.get("domainId")).value(domainIds);

            final Predicate loginPredicate;
            if (StringUtils.isNotBlank(searchParam)) {
                if (searchExact == null || searchExact == FALSE) {
                    loginPredicate = cb.like(root.get(LOGIN).as(String.class), "%" + searchParam + "%");
                } else {
                    loginPredicate = cb.equal(root.get(LOGIN), searchParam);
                }
            } else {
                loginPredicate = null;
            }

            return cb.and(Stream.of(domainPredicate, loginPredicate)
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new));
        };
    }

    public AcsUser getAcsUser(final String token, final String login) {
        final Session session = jwtService.getSession(token);

        return userRepository.findById(login)
                .map(entity -> entityToUser(session.getClientType(), entity))
                .orElse(null);
    }

    @Transactional
    public AcsUser updateAcsUser(final String token, final AcsUserBody body) {
        final Session session = jwtService.getSession(token);
        final Integer domainId;
        if (domainService.getDomainIdByUserId(session.getUserId()).isPresent()) {
            domainId = domainService.getDomainIdByUserId(session.getUserId()).get();
        } else {
            domainId = 0;
        }
        final Optional<AcsUserEntity> currentUser = userRepository.findById(body.getLogin());

        final AcsUserEntity newUser;
        if (currentUser.isPresent()) {
            newUser = userRepository.saveAndFlush(currentUser.get()
                    .toBuilder()
                    .password(body.getPassword())
                    .build());
        } else {
            newUser = userRepository.saveAndFlush(AcsUserEntity.builder()
                    .domainId(domainId)
                    .login(body.getLogin())
                    .password(body.getPassword())
                    .build());
        }
        return entityToUser(session.getClientType(), newUser);
    }

    @Transactional
    public boolean deleteAcsUser(final String token, final List<String> logins) {
        jwtService.getSession(token);

        final List<String> existLogins = logins.stream()
                .filter(userRepository::existsById)
                .collect(Collectors.toList());
        existLogins.forEach(userRepository::deleteById);

        return !existLogins.isEmpty();
    }

    @Transactional
    public void addAcsUsers(final String token, final MultipartFile file) {
        final Session session = jwtService.getSession(token);
        final Integer currentDomainId;
        if (domainService.getDomainIdByUserId(session.getUserId()).isPresent()) {
            currentDomainId = domainService.getDomainIdByUserId(session.getUserId()).get();
        } else {
            currentDomainId = 0;
        }

        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null || ObjectUtils.notEqual(extension.toLowerCase(), "csv")) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        try {
            final CSVReader csvReader = new CSVReader(new FileReader(convert(file)));
            final List<AcsUserEntity> users =
                    csvReader.readAll()
                            .stream()
                            .skip(1)
                            .map(u -> AcsUserEntity.builder()
                                    .login(u[0])
                                    .password(u[1])
                                    .domainId(getDomainId(currentDomainId, session.getUserId()))
                                    .build())
                            .collect(Collectors.toList());

            userRepository.saveAll(users);
        } catch (IOException | CsvException e) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
    }

    public static File convert(final MultipartFile file) {
        try {
            final File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            final FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile;
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
    }

    private Integer getDomainId(final Integer currentDomainId, final Long userId) {
        return currentDomainId == null
                ? domainService.getDomainIdByUserId(userId).orElse(null)
                : currentDomainId.equals(-1) ? null : currentDomainId;
    }

    private AcsUser entityToUser(final ClientType clientType, final AcsUserEntity entity) {
        final Boolean showPassword = interfaceService.getInterfaceValue(clientType, "ShowACSUserPassword")
                .map(Boolean::parseBoolean)
                .orElse(false);
        return AcsUser.builder()
                .login(entity.getLogin())
                .password(showPassword ? entity.getPassword()
                        : PasswordUtils.getHiddenPassword(entity.getPassword()))
                .build();
    }

}
