package com.friendly.services.uiservices.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.request.AuthBody;
import com.friendly.commons.models.auth.request.AuthRequest;
import com.friendly.commons.models.auth.response.AuthResponse;
import com.friendly.commons.models.auth.response.LogoutReasonTypeResponse;
import com.friendly.services.settings.userinterface.InterfaceItem;
import com.friendly.services.settings.userinterface.InterfaceService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.AUTH_IS_NULL;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.AUTH_PARSING_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    @NonNull
    InterfaceService interfaceService;
    @NonNull
    ObjectMapper mapper;
    @NonNull
    HttpSession httpSession;
    @NonNull
    JwtService jwtService;

    public AuthType getCurrentAuthType() {
        Optional<String> authOptional = interfaceService.getInterfaceItemEntity(InterfaceItem.AUTHENTICATION_TYPE);
        if (!authOptional.isPresent()) {
            return AuthType.DATABASE;
        }

        String auth = authOptional.get();
        try {
            AuthConfigDto authConfigDto = mapper.readValue(auth, AuthConfigDto.class);
            if (authConfigDto == null || authConfigDto.getValue() == null) {
                throw new FriendlyIllegalArgumentException(AUTH_IS_NULL);
            }
            return AuthType.fromValue(authConfigDto.getValue());
        } catch (JsonProcessingException e) {
            throw new FriendlyIllegalArgumentException(AUTH_PARSING_FAILED);
        }
    }

    public AuthResponse getAuthResponse(AuthRequest request, HttpServletRequest httpServletRequest, long expTime) {
        final String notificationIdentifier = UUID.randomUUID().toString();
        AuthType authType = getCurrentAuthType();
        final AuthBody auth = jwtService.createToken(request, httpSession, notificationIdentifier, httpServletRequest,
                authType);

        return AuthResponse.builder()
                .token(auth.getToken())
                .expirationTimeMs(expTime)
                .notificationIdentifier(notificationIdentifier)
                .sessionHash(auth.getSessionHash())
                .build();
    }

    public LogoutReasonTypeResponse getLogoutReasonTypeResponse(String token, HttpServletRequest httpServletRequest) {
        return jwtService.logout(token, httpServletRequest);
    }
}
