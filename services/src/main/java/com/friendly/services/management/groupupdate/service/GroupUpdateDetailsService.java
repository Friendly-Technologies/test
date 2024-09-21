package com.friendly.services.management.groupupdate.service;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateGroupDetailsResponse;
import com.friendly.services.management.groupupdate.dto.request.GroupUpdateGroupDetailsRequest;
import com.friendly.commons.models.request.IntIdRequest;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum;
import com.friendly.services.management.groupupdate.mapper.GroupUpdateDetailsMapper;
import com.friendly.services.management.groupupdate.orm.acs.repository.UpdateGroupRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.uiservices.user.UserService;
import com.ftacs.Exception_Exception;
import com.ftacs.UpdateGroupWS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupUpdateDetailsService {
    private final JwtService jwtService;
    private final UpdateGroupRepository updateGroupRepository;
    private final UserService userService;
    private final GroupUpdateDetailsMapper mapper;

    public GroupUpdateGroupDetailsResponse getDetails(String token, IntIdRequest body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        return mapper.entityToDetails(
                updateGroupRepository.findById(body.getId())
                        .orElseThrow(() -> new FriendlyIllegalArgumentException(
                                ServicesErrorRegistryEnum.UPDATE_GROUP_NOT_FOUND,
                                body.getId())),
                session,
                user);
    }

    public boolean save(String token, GroupUpdateGroupDetailsRequest body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());

        UpdateGroupWS updateGroupWS = mapper.detailsToWS(body, user, session);

        try {
            if (body.getId() == null || body.getId() == 0) {
                AcsProvider.getAcsWebService(session.getClientType()).createUpdateGroup(updateGroupWS, user.getName());
            } else {
                AcsProvider.getAcsWebService(session.getClientType()).updateUpdateGroup(updateGroupWS, body.getId(), user.getName());
            }
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
        return false;
    }


}