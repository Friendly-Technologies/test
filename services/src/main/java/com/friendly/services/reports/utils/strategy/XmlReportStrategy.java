package com.friendly.services.reports.utils.strategy;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.device.info.service.DeviceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.friendly.commons.models.reports.ReportType.DEVICE_UPDATE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FORMAT_NOT_SUPPORTED;

@Component
@RequiredArgsConstructor
public class XmlReportStrategy implements ReportStrategy {

    @NonNull
    private final DeviceService deviceService;
    private static final Map<ReportType, BiConsumer<Session, Map<String, Object>>> XML_FUNCTION_MAP = new EnumMap<>(ReportType.class);

    @PostConstruct
    public void init() {
        XML_FUNCTION_MAP.put(DEVICE_UPDATE, deviceService::generateDeviceListXml);
    }

    @Override
    public void generateReport(ReportType reportType, Session session, Map<String, Object> params, String fileName) {
        final BiConsumer<Session, Map<String, Object>> biConsumer = XML_FUNCTION_MAP.get(reportType);
        if (biConsumer == null) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        biConsumer.accept(session, params);
    }
}
