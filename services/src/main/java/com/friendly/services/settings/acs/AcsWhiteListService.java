package com.friendly.services.settings.acs;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.RemoveWhiteListBody;
import com.friendly.commons.models.settings.WhiteListBody;
import com.friendly.commons.models.settings.acs.whitelist.*;
import com.friendly.commons.models.settings.request.WhiteListIpRequest;
import com.friendly.commons.models.settings.request.WhiteListRequest;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.acs.orm.acs.model.WhiteListEntity;
import com.friendly.services.settings.acs.orm.acs.repository.WhiteListRepository;
import com.friendly.services.settings.acs.orm.acs.repository.WhiteListSerialRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.acs.mapper.AcsMapper;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import com.ftacs.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.*;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcsWhiteListService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final UserService userService;

    @NonNull
    private final WhiteListRepository whiteListRepository;

    @NonNull
    private final WhiteListSerialRepository whiteListSerialRepository;

    @NonNull
    private final AcsMapper mapper;


    public FTPage<AbstractAcsWhiteList> getWhiteList(final String token, final WhiteListBody body) {
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");
        final WhiteListType type = body.getType();
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());


        final List<org.springframework.data.domain.Page<WhiteListEntity>> whiteListEntities;
        final List<AbstractAcsWhiteList> whiteList;
        switch (type) {
            case IP_RANGE:
                whiteListEntities = pageable.stream()
                        .map(whiteListRepository::findAllByIpRangeIsNotNull)
                        .collect(Collectors.toList());
                whiteList = whiteListEntities.stream()
                        .map(Page::getContent)
                        .flatMap(wls -> mapper.whiteListEntitiesToWhiteListIps(wls, clientType,
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());
                break;
            case SERIAL:
                whiteListEntities = pageable.stream()
                        .map(whiteListRepository::findAllByIpRangeIsNull)
                        .collect(Collectors.toList());
                whiteList = whiteListEntities.stream()
                        .map(Page::getContent)
                        .flatMap(wls -> mapper.whiteListEntitiesToWhiteListSerials(wls, clientType,
                                        session.getZoneId(),
                                        user.getDateFormat(),
                                        user.getTimeFormat())
                                .stream())
                        .map(s -> s.toBuilder()
                                .serials(
                                        whiteListSerialRepository.countByWhiteListId(s.getId()))
                                .build())
                        .collect(Collectors.toList());
                break;
            default:
                throw new FriendlyIllegalArgumentException(WHITE_LIST_NOT_SUPPORTED);
        }

        final FTPage<AbstractAcsWhiteList> page = new FTPage<>();
        return page.toBuilder()
                .items(whiteList)
                .pageDetails(PageUtils.buildPageDetails(whiteListEntities))
                .build();
    }

    public boolean addWhiteListIp(final String token, final WhiteListIpRequest request) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();

        final AcsWhiteListIp whiteListIp = AcsWhiteListIp.builder()
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .ipRange(request.getIpRange())
                .onlyCreated(request.isOnlyCreated())
                .build();
        final CpeWhiteListWS whiteListWS = new CpeWhiteListWS();
        final CpeWhiteListDataWS whiteListDataWS = new CpeWhiteListDataWS();
        whiteListDataWS.setIpRange(whiteListIp.getIpRange());
        whiteListDataWS.setManufacturer(whiteListIp.getManufacturer());
        whiteListDataWS.setModel(whiteListIp.getModel());
        whiteListDataWS.setOnlyCreated(whiteListIp.getOnlyCreated() != null && whiteListIp.getOnlyCreated());
        whiteListWS.getCPE().add(whiteListDataWS);
        try {
            AcsProvider.getAcsWebService(clientType).addCPEToWhiteList(whiteListWS, creator);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
        return true;
    }

    public boolean addWhiteListSerial(final String token, final WhiteListRequest request, final MultipartFile file) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final String creator = clientType.name().toUpperCase() + "/" + user.getUsername();

        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (extension == null || ObjectUtils.notEqual(extension.toLowerCase(), "txt") &&
                ObjectUtils.notEqual(extension.toLowerCase(), "csv")) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        String content;
        try {
            content = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }

        final AcsWhiteListSerial whiteListSerial = AcsWhiteListSerial.builder()
                .description(request.getDescription())
                .typeSerial(request.getType())
                .build();
        try {
            AcsProvider.getAcsWebService(clientType)
                    .importWhiteListFile(creator, whiteListSerial.getDescription(), whiteListSerial.getTypeSerial().name(),
                            content, whiteListSerial.getOnlyCreated() != null && whiteListSerial.getOnlyCreated());
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
        return true;
    }


    public void removeWhiteList(final String token,
                                final RemoveWhiteListBody body) {
        final Session session = jwtService.getSession(token);
        final List<Integer> ids = body.getIds();
        final ACSWebService acsWebService = AcsProvider.getAcsWebService(session.getClientType());
        switch (body.getType()) {
            case IP_RANGE:
                try {
                    final IntegerArrayWS integerArrayWS = new IntegerArrayWS();
                    integerArrayWS.getId().addAll(ids);

                    acsWebService.deleteCPEFromWhiteList(integerArrayWS);
                } catch (Exception_Exception e) {
                    throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
                }
                break;
            case SERIAL:
                ids.forEach(id -> {
                    try {
                        acsWebService.deleteCPESFromWhiteListByFileId(id);
                    } catch (Exception_Exception e) {
                        throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
                    }
                });
                break;
            default:
                throw new FriendlyIllegalArgumentException(WHITE_LIST_NOT_SUPPORTED);
        }
    }

    public WhiteListCheckResponse checkWhiteList(final String token, final AcsCheckWhiteList whiteList) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());

        try {
            final StringResponse stringResponse =
                    AcsProvider.getAcsWebService(session.getClientType())
                            .checkCPEWhiteList(null,
                                    whiteList.getSerial(),
                                    whiteList.getTypeSerial() == null
                                            ? null : whiteList.getTypeSerial().name());
            final boolean isKnown = StringUtils.isNotBlank(stringResponse.getString());
            final WhiteListCheckResponse.WhiteListCheckResponseBuilder<?, ?> checkResponse =
                    WhiteListCheckResponse.builder()
                            .isKnown(isKnown);
            if (isKnown) {
                int whiteListId = Integer.parseInt(stringResponse.getString());
                Optional<WhiteListEntity> whiteListEntity = whiteListRepository.findById(whiteListId);
                checkResponse.whiteListId(whiteListId);
                if (whiteListEntity.isPresent()) {
                    checkResponse.description(whiteListEntity.get().getDescription());
                    checkResponse.created(DateTimeUtils.formatAcs(whiteListEntity.get().getCreated(), session.getClientType(),
                            session.getZoneId(), user.getDateFormat(), user.getTimeFormat()));
                }
            }
            return checkResponse.build();
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public boolean removeSerialFromWhiteList(final String token,
                                             final RemoveSerialFromWhiteList writeListSerials) {
        final Session session = jwtService.getSession(token);

        try {
            final StringArrayWS serials = new StringArrayWS();
            serials.getString().addAll(writeListSerials.getSerials());

            final BooleanResponse response =
                    AcsProvider.getAcsWebService(session.getClientType())
                            .deleteCPESFromWhiteListBySerials(serials,
                                    writeListSerials.getTypeSerial() == null
                                            ? null : writeListSerials.getTypeSerial()
                                            .name());
            return response.isResult();
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }
}
