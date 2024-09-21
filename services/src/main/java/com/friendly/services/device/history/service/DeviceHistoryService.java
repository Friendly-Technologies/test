package com.friendly.services.device.history.service;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.response.UserExperienceRebootResetEvents;
import com.friendly.commons.models.request.LongIdRequest;
import com.friendly.commons.models.user.Session;
import com.friendly.services.device.history.orm.acs.model.DeviceHistoryProjection;
import com.friendly.services.device.history.orm.acs.repository.DeviceHistoryRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceHistoryService {
    final JwtService jwtService;
    final DeviceHistoryRepository deviceHistoryRepository;
    public UserExperienceRebootResetEvents rebootResetEvents(String token, LongIdRequest request) {
        Session session = jwtService.getSession(token);
        ClientType clientType = session.getClientType();
        String zoneId = session.getZoneId();

        AtomicLong resetD = new AtomicLong(0L);
        AtomicLong rebootD = new AtomicLong(0L);
        AtomicLong resetW = new AtomicLong(0L);
        AtomicLong rebootW = new AtomicLong(0L);
        AtomicLong resetM = new AtomicLong(0L);
        AtomicLong rebootM = new AtomicLong(0L);

        List<DeviceHistoryProjection> lst =
                deviceHistoryRepository.findGroupedDataByCpeId(request.getId(),
                        DateTimeUtils.getDiffMinutes(zoneId));

        calculateEvents(
                lst, clientType, zoneId, rebootD, resetD, rebootW, resetW, rebootM, resetM);

        return new UserExperienceRebootResetEvents(
                new UserExperienceRebootResetEvents.EventDate(rebootD.get(), rebootW.get(), rebootM.get()),
                new UserExperienceRebootResetEvents.EventDate(resetD.get(), resetW.get(), resetM.get()));
    }

    private void calculateEvents(
            final List<DeviceHistoryProjection> lst,
            final ClientType clientType,
            final String zoneId,
            AtomicLong rebootD,
            AtomicLong resetD,
            AtomicLong rebootW,
            AtomicLong resetW,
            AtomicLong rebootM,
            AtomicLong resetM) {

        String reboot = getIdsToString(deviceHistoryRepository.getEventCodeIdsByName("1 BOOT"));
        String reset = getIdsToString(deviceHistoryRepository.getEventCodeIdsByName("0 BOOTSTRAP"));

        Instant now = Instant.now();
        Instant week =
                DateTimeUtils.clientToServer(
                                now.minus(7, ChronoUnit.DAYS), clientType, zoneId)
                        .truncatedTo(ChronoUnit.DAYS);
        Instant month =
                DateTimeUtils.clientToServer(
                                now.minus(31, ChronoUnit.DAYS), clientType, zoneId)
                        .truncatedTo(ChronoUnit.DAYS);
        for (DeviceHistoryProjection item : lst) {
            String sid = String.format(",%d,", item.getEventCodeId());
            LocalDate localDate = item.getCreated();
            Instant created = LocalDateTime.of(localDate, LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC);
            if (localDate.equals(LocalDate.now())) {
                if (reboot.contains(sid)) {
                    rebootD.addAndGet(item.getCount());
                } else if (reset.contains(sid)) {
                    resetD.addAndGet(item.getCount());
                }
            }
            if (localDate.equals(LocalDate.now().minusDays(7))
                    || created.isAfter(week)) {
                if (reboot.contains(sid)) {
                    rebootW.addAndGet(item.getCount());
                } else if (reset.contains(sid)) {
                    resetW.addAndGet(item.getCount());
                }
            }
            if (localDate.equals(LocalDate.now().minusDays(31))
                    || created.isAfter(month)) {
                if (reboot.contains(sid)) {
                    rebootM.addAndGet(item.getCount());
                } else if (reset.contains(sid)) {
                    resetM.addAndGet(item.getCount());
                }
            }
        }
    }

    public static String getIdsToString(List<Integer> integers) {
        return integers.stream().map(Object::toString).collect(Collectors.joining(",", ",", ","));
    }
}