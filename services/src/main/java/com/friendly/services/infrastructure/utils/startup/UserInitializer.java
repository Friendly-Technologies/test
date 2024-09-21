package com.friendly.services.infrastructure.utils.startup;

import com.friendly.commons.models.Themes;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.uiservices.system.orm.iotw.model.LocaleEntity;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.usergroup.orm.iotw.model.UserGroupEntity;
import com.friendly.services.uiservices.system.orm.iotw.repository.LocaleRepository;
import com.friendly.services.settings.usergroup.orm.iotw.repository.UserGroupRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("liquibaseIotw")
public class UserInitializer {

    private final static String USERNAME = "admin";

    @NonNull
    private final PasswordEncoder passwordEncoder;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final UserGroupRepository userGroupRepository;

    @NonNull
    private final LocaleRepository localeRepository;

    @PostConstruct
    @Transactional
    public void init() {
        initUser(ClientType.sc);
        initUser(ClientType.mc);
    }

    private void initUser(final ClientType clientType) {
        final Optional<UserEntity> userDetails =
                userRepository.findByUsernameAndClientTypeSuperDomain(USERNAME, clientType);
        if (!userDetails.isPresent()) {
            final UserGroupEntity userGroupAdmin =
                    userGroupRepository.saveAndFlush(UserGroupEntity.builder()
                            .name("admin")
                            .clientType(clientType)
                            .build());
            final LocaleEntity locale = localeRepository.saveAndFlush(LocaleEntity.builder()
                    .value("English")
                    .id("EN")
                    .build());
            userRepository.saveAndFlush(UserEntity.builder()
                    .username(USERNAME)
                    .password(passwordEncoder.encode(USERNAME))
                    .name(USERNAME)
                    .email(USERNAME + "@email.com")
                    .userGroupId(userGroupAdmin.getId())
                    .localeId(locale.getId())
                    .clientType(clientType)
                    .themeName(Themes.Light)
                    .dateFormat("Default")
                    .timeFormat("Default")
                    .domainId(0)
                    .blocked(false)
                    .build());
        }
    }
}
