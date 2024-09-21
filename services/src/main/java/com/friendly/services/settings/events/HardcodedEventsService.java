package com.friendly.services.settings.events;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.settings.acs.events.HardcodedEventItem;
import com.friendly.commons.models.settings.acs.events.HardcodedEventUrlItem;
import com.friendly.commons.models.settings.request.HardcodedEventRequest;
import com.friendly.commons.models.settings.request.HardcodedEventUrlDeleteRequest;
import com.friendly.commons.models.settings.request.HardcodedEventUrlRequest;
import com.friendly.commons.models.settings.response.HardcodedEventsResponse;
import com.friendly.commons.models.settings.response.HardcodedEventsUrlsResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.management.events.orm.acs.model.EventHardcodedUrlEntity;
import com.friendly.services.management.events.orm.acs.repository.EventHardcodedRepository;
import com.friendly.services.management.events.orm.acs.repository.EventHardcodedUrlRepository;
import com.friendly.services.management.events.orm.acs.repository.EventReceiverUrlRepository;
import com.friendly.services.settings.usergroup.orm.iotw.repository.UserGroupRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.acs.AcsConfigService;
import com.friendly.services.settings.events.mapper.HardcodedEventsMapper;
import com.friendly.services.infrastructure.utils.CommonUtils;
import com.ftacs.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardcodedEventsService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    @NonNull
    AcsConfigService acsConfigService;

    @NonNull
    EventHardcodedUrlRepository eventHardcodedUrlRepository;

    @NonNull
    EventReceiverUrlRepository eventReceiverUrlRepository;

    @NonNull
    EventHardcodedRepository eventHardcodedRepository;

    @NonNull
    HardcodedEventsMapper mapper;

    @NonNull
    private final JwtService jwtService;

    public HardcodedEventsResponse getEvents(final String token) {
        jwtService.getSession(token);
        HardcodedEventsResponse response = new HardcodedEventsResponse();
//        HardcodedEventsGeneralConfig generalConfig = new HardcodedEventsGeneralConfig();
//        try {
//            generalConfig.setAttempts(Integer.valueOf(acsConfigService.getAcsConfigParameterValue(token, "attempsForSendingMonitorResult")));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setTimeout(Integer.valueOf(acsConfigService.getAcsConfigParameterValue(token, "timeoutForSendingMonitorResult")));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setInterval(Integer.valueOf(acsConfigService.getAcsConfigParameterValue(token, "rangeForSendingMonitorResult")));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setSendTo(Integer.valueOf(acsConfigService.getAcsConfigParameterValue(token, "sendToFEMS")));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setWebServiceUrl(acsConfigService.getAcsConfigParameterValue(token, "urlForSendMonitorResult"));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setFemsUrl(acsConfigService.getAcsConfigParameterValue(token, "urlForSendMonitorResultAdditional"));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setSend(Boolean.valueOf(acsConfigService.getAcsConfigParameterValue(token, "enableHardcodedEvents")));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        try {
//            generalConfig.setProtocol(acsConfigService.getAcsConfigParameterValue(token, "hardcodedEventsProtocol"));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        response.setGeneralConfig(generalConfig);

        Map<Integer, List<Integer>> urlsToEventMap = eventHardcodedUrlRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        EventHardcodedUrlEntity::getEventId,
                        Collectors.mapping(EventHardcodedUrlEntity::getUrlId, Collectors.toList())
                ));
        List<HardcodedEventItem> eventItems = eventHardcodedRepository.findAll()
                .stream()
                .map(e -> mapper.eventHardcodedEntityToEvent(e, urlsToEventMap.get(e.getId())))
                .collect(Collectors.toList());

        response.setItems(eventItems);
        return response;
    }

    public void changeEvent(final String token, HardcodedEventRequest request) {
        List<HardcodedEventItem> items = request.getItems();
        final Session session = jwtService.getSession(token);
        EventHardcodedListWS eventHardcodedListWS = new EventHardcodedListWS();
        List<EventHardcodedWS> list = items.stream().map(item -> {
            EventHardcodedWS eventHardcodedWS = new EventHardcodedWS();
            eventHardcodedWS.setEventId(item.getId());
            eventHardcodedWS.setNotification(item.getEnabled() ? 1 : 0);
            IntegerArrayWS arrayWS = new IntegerArrayWS();
            arrayWS.getId().addAll(item.getUrlIds());
            eventHardcodedWS.setUrlIds(arrayWS);
            return eventHardcodedWS;
        }).collect(Collectors.toList());

        eventHardcodedListWS.getEventHardcoded().addAll(list);
        try {
            AcsProvider.getAcsWebService(session.getClientType())
                    .changeHardcodedEvent(eventHardcodedListWS);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public void changeUrls(final String token, HardcodedEventUrlRequest request) {
        List<Integer> deleteList = eventReceiverUrlRepository.getIds();
        List<HardcodedEventUrlItem> items = request.getItems();
        final Session session = jwtService.getSession(token);


        List<EventReceiverUrlWS> createList = new ArrayList<>();
        List<EventReceiverUrlWS> updateList = new ArrayList<>();
        for (HardcodedEventUrlItem item : items) {
            EventReceiverUrlWS eventReceiverUrlWS = new EventReceiverUrlWS();
            eventReceiverUrlWS.setUrl(item.getUrl());
            eventReceiverUrlWS.setUseFtacsNs(CommonUtils.ACS_OBJECT_FACTORY.createEventReceiverUrlWSUseFtacsNs(item.getUseFtacsNs()));
            if (item.getId() != null && item.getId() > 0) {
                eventReceiverUrlWS.setId(item.getId());
                updateList.add(eventReceiverUrlWS);
                deleteList.remove(item.getId());
            } else {
                createList.add(eventReceiverUrlWS);
            }
        }
        if (!createList.isEmpty()) {
            EventReceiverUrlListWS eventReceiverUrlListWS = new EventReceiverUrlListWS();
            eventReceiverUrlListWS.getEventReceiverUrl().addAll(createList);
            try {
                AcsProvider.getAcsWebService(session.getClientType())
                        .createReceiverUrls(eventReceiverUrlListWS);
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        }
        if (!updateList.isEmpty()) {
            EventReceiverUrlListWS eventReceiverUrlListWS = new EventReceiverUrlListWS();
            eventReceiverUrlListWS.getEventReceiverUrl().addAll(updateList);
            try {
                AcsProvider.getAcsWebService(session.getClientType())
                        .updateReceiverUrls(eventReceiverUrlListWS);
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        }

        if (!deleteList.isEmpty()) {
            IntegerArrayWS arrayWS = new IntegerArrayWS();
            arrayWS.getId().addAll(deleteList);
            try {
                AcsProvider.getAcsWebService(session.getClientType())
                        .removeReceiverUrls(arrayWS);
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        }
    }

    public void removeUrls(final String token, HardcodedEventUrlDeleteRequest request) {
        List<Integer> items = request.getIds();
        final Session session = jwtService.getSession(token);
        IntegerArrayWS arrayWS = new IntegerArrayWS();
        arrayWS.getId().addAll(items);
        try {
            AcsProvider.getAcsWebService(session.getClientType())
                    .removeReceiverUrls(arrayWS);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    public HardcodedEventsUrlsResponse getEventUrls(String token) {
        jwtService.getSession(token);
        List<HardcodedEventUrlItem> eventUrlItems = eventReceiverUrlRepository.findAll()
                .stream()
                .map(e -> mapper.eventReceiverUrlEntityToEventUrl(e))
                .collect(Collectors.toList());
        HardcodedEventsUrlsResponse response = new HardcodedEventsUrlsResponse();
        response.setItems(eventUrlItems);
        return response;
    }


}