package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.commons.models.view.AbstractView;
import com.friendly.commons.models.view.ViewColumn;
import com.friendly.commons.models.view.ViewCondition;
import com.friendly.services.device.info.service.DeviceService;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.uiservices.view.ViewService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.commons.models.reports.ReportType.DEVICE_UPDATE;

@Component
@RequiredArgsConstructor
public class ExcelDeviceUpdateReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final DeviceService deviceService;
    @NonNull
    private final ViewService viewService;
    @NonNull
    private final DeviceUtils deviceUtils;
    @NonNull
    private final DeviceRepository deviceRepository;
    @NonNull
    private final UserService userService;
    @NonNull
    private final WsSender wsSender;


    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final Integer viewIdInt = (Integer) params.get("viewId");
        final Long viewId = viewIdInt == null ? null : Long.valueOf(viewIdInt);
        final String manufacturer = (String) params.get("manufacturer");
        final String model = (String) params.get("model");
        final ProtocolType protocolType = (ProtocolType) params.get("protocolType");
        final Integer exceptDeviceIdInt = (Integer) params.get("exceptDeviceId");
        List<HashMap<String, Object>> conditionMaps = params.get("conditions") != null ?
                (List<HashMap<String, Object>>) params.get("conditions") : null;
        final List<ViewCondition> conditions = DeviceUtils.parseFromParams(conditionMaps);
        final Long exceptDeviceId = exceptDeviceIdInt == null ? null : Long.valueOf(exceptDeviceIdInt);
        final Long userId = session.getUserId();
        String zoneId = session.getZoneId();
        final UserResponse user = userService.getUserByIdWithoutDomain(userId, zoneId);
        final Optional<Integer> userDomainId = domainService.getDomainIdByUserId(userId);
        final List<Integer> domainIds = userDomainId.map(domainService::getChildDomainIds)
                .orElse(null);
        final Map<String, String> sorts = (Map<String, String>) params.get("sorts");
        final String direction = sorts.get("direction");
        final String field = sorts.get("field");

        List<DeviceEntity> entities = deviceRepository
                .findAll(conditions == null ?
                        deviceService.getListFilters(viewId, domainIds, manufacturer, model, protocolType,
                                exceptDeviceId, session.getClientType(), zoneId) :
                        deviceService.getListFilters(viewId, domainIds, manufacturer, model, protocolType,
                                exceptDeviceId, session.getClientType(), conditions, zoneId));


        final AbstractView abstractView = deviceUtils.getAbstractView(viewId, session);
        final List<Object[]> deviceUpdateEntities =
                deviceService.extendDeviceEntityWithParameterValues(entities, session, user)
                        .stream()
                        .map(d -> deviceUtils.getEntityObjectsFromViewColumns(d, abstractView))
                        .collect(Collectors.toList());

        final String localeId = Optional.ofNullable(userService.getUserByIdWithoutDomain(userId, session.getZoneId()))
                .map(UserResponse::getLocaleId)
                .orElse("EN");
        final List<String> viewColumnNames =
                viewService.getViewColumns(viewId, localeId).stream().map(ViewColumn::getColumnName)
                        .collect(Collectors.toList());

        if(!direction.isEmpty() || !field.isEmpty()) {
            sortEntities(deviceUpdateEntities, field, direction, viewColumnNames);
        }

        final String path =
                ReportFileService.createExcelTable(userDomainId.orElse(null), deviceUpdateEntities,
                        DEVICE_UPDATE, true, session.getClientType(),
                        zoneId, user.getDateFormat(),
                        user.getTimeFormat(), null, null, viewColumnNames, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    private static int findFieldIndex(String field, List<String> viewColumnNames) {
        for (int i = 0; i < viewColumnNames.size(); i++) {
            if (viewColumnNames.get(i).equalsIgnoreCase(field)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Field for sorting not found: " + field);
    }

    private static void sortEntities(List<Object[]> entities, String field, String direction, List<String> viewColumnNames) {
        int fieldIndex = findFieldIndex(field, viewColumnNames);

        entities.sort((e1, e2) -> {
            try {
                Object value1 = e1[fieldIndex];
                Object value2 = e2[fieldIndex];

                int comparisonResult;

                if (field.equals("updated") || field.equals("created")) {
                    LocalDateTime date1 = parseDateFromString(value1);
                    LocalDateTime date2 = parseDateFromString(value2);

                    if (date1 != null && date2 != null) {
                        comparisonResult = date1.compareTo(date2);
                    } else {
                        throw new IllegalArgumentException("Error while processing values to date.");
                    }
                } else {
                    comparisonResult = ((Comparable) value1).compareTo(value2);
                }

                return "DESC".equalsIgnoreCase(direction) ? -comparisonResult : comparisonResult;

            } catch (ClassCastException ex) {
                throw new RuntimeException("Error while sorting by field: " + field, ex);
            }
        });
    }

    private static LocalDateTime parseDateFromString(Object value) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (value instanceof String) {
            try {
                return LocalDateTime.parse((String) value, dateTimeFormatter);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    @Override
    public ReportType getReportType() {
        return DEVICE_UPDATE;
    }
}
