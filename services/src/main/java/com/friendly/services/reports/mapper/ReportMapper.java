package com.friendly.services.reports.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.DeviceRegistrationReport;
import com.friendly.commons.models.reports.FirmwareVersionReport;
import com.friendly.commons.models.reports.ProfileDownloadReport;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.orm.acs.model.projections.CpeRegReportProjection;
import com.friendly.services.productclass.orm.acs.model.projections.ProductGroupFirmwareReportProjection;
import com.friendly.services.management.profiles.orm.acs.model.ProfileDownloadReportProjection;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportMapper {

    public List<DeviceRegistrationReport> arrayToDeviceRegistrationReports(final List<CpeRegReportProjection> productClassEntities,
                                                                           final ClientType clientType,
                                                                           final String zoneId,
                                                                           final String dateFormat,
                                                                           final String timeFormat) {
        if (productClassEntities == null) {
            return Collections.emptyList();
        }

        return productClassEntities.stream()
                                   .map(pc -> arrayToDeviceRegistrationReport(pc, clientType, zoneId,
                                                                              dateFormat, timeFormat))
                                   .collect(Collectors.toList());
    }

    private DeviceRegistrationReport arrayToDeviceRegistrationReport(final CpeRegReportProjection report, final ClientType clientType,
                                                                     final String zoneId, final String dateFormat,
                                                                     final String timeFormat) {
        return DeviceRegistrationReport.builder()
                                       .domain(report.getDomainName())
                                       .serial(report.getSerial())
                                       .manufacturer(report.getManufacturerName())
                                       .model(report.getModel())
                                       .createdIso(DateTimeUtils.serverToUtc(report.getCreated(), clientType))
                                       .updatedIso(DateTimeUtils.serverToUtc(report.getUpdated(), clientType))
                                       .created(DateTimeUtils.formatAcs(
                                               (report.getCreated()), clientType, zoneId, dateFormat, timeFormat))
                                       .updated(DateTimeUtils.formatAcs(
                                               (report.getUpdated()), clientType, zoneId, dateFormat, timeFormat))
                                       .phone(report.getPhone())
                                       .build();
    }

    public ProfileDownloadReport buildProfileDownloadReport(final Session session, final UserResponse user,
                                                            final ClientType clientType, final ProfileDownloadReportProjection r) {
        String zoneId = session.getZoneId();
        return ProfileDownloadReport.builder()
                                    .id(r.getProfileId())
                                    .name(r.getProfileName())
                                    .manufacturer(r.getManufacturerName())
                                    .model(r.getModel())
                                    .domain(r.getDomainName())
                                    .fileType(r.getFileType())
                                    .version(r.getProfileVersion())
                                    .url(r.getUrl())
                                    .creator(r.getCreator())
                                    .created(DateTimeUtils.formatAcs(r.getCreated(), clientType, zoneId,
                                            user.getDateFormat(), user.getTimeFormat()))
                                    .createdIso(DateTimeUtils.serverToUtc(r.getCreated(), clientType))
                                    .completedTasks(r.getCompleted())
                                    .pendingTasks(r.getPending())
                                    .rejectedTasks(r.getRejected())
                                    .failedTasks(r.getFailed())
                                    .build();
    }

    public FirmwareVersionReport buildFirmwareVersionReport(final ProductGroupFirmwareReportProjection r) {
        return FirmwareVersionReport.builder()
                                    .manufacturer(r.getFirmware())
                                    .model(r.getModel())
                                    .domain(r.getDomainName())
                                    .version(r.getFirmware())
                                    .quantity(r.getCount())
                                    .build();
    }
}
