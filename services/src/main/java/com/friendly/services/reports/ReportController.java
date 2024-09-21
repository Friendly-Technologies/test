package com.friendly.services.reports;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.reports.ActiveSessionsBody;
import com.friendly.commons.models.reports.DeviceDistributionBody;
import com.friendly.commons.models.reports.DeviceDistributionReport;
import com.friendly.commons.models.reports.DeviceEventBody;
import com.friendly.commons.models.reports.DeviceEventReport;
import com.friendly.commons.models.reports.DeviceOfflineBody;
import com.friendly.commons.models.reports.DeviceOfflineReport;
import com.friendly.commons.models.reports.DeviceOnlineBody;
import com.friendly.commons.models.reports.DeviceOnlineReport;
import com.friendly.commons.models.reports.DeviceRegistrationReport;
import com.friendly.commons.models.reports.DeviceRegistrationReportBody;
import com.friendly.commons.models.reports.DeviceReport;
import com.friendly.commons.models.reports.DeviceUpdateReportBody;
import com.friendly.commons.models.reports.FileDatesList;
import com.friendly.commons.models.reports.FileReport;
import com.friendly.commons.models.reports.FirmwareVersionBody;
import com.friendly.commons.models.reports.FirmwareVersionReport;
import com.friendly.commons.models.reports.GeneratedReportsBody;
import com.friendly.commons.models.reports.GroupUpdateBody;
import com.friendly.commons.models.reports.GroupUpdateDeviceBody;
import com.friendly.commons.models.reports.ProfileDownloadBody;
import com.friendly.commons.models.reports.ProfileDownloadReport;
import com.friendly.commons.models.reports.ReportFileBody;
import com.friendly.commons.models.reports.SessionStatisticsBody;
import com.friendly.commons.models.reports.StatisticOperationsBody;
import com.friendly.commons.models.reports.StatisticOperationsReport;
import com.friendly.commons.models.reports.UserActivityReport;
import com.friendly.commons.models.reports.UserActivityReportBody;
import com.friendly.commons.models.reports.request.LinkRequest;
import com.friendly.commons.models.reports.request.LinksRequest;
import com.friendly.commons.models.reports.response.DeviceActivityTypeDescriptionsResponse;
import com.friendly.commons.models.reports.response.UserActivityTypeDescriptionsResponse;
import com.friendly.commons.models.user.UserSession;
import com.friendly.services.infrastructure.base.BaseController;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateDeviceReport;
import com.friendly.services.management.groupupdate.dto.response.GroupUpdateReport;
import com.friendly.services.management.groupupdate.service.GroupUpdateService;
import com.friendly.services.reports.dto.ReportResponse;
import com.friendly.services.reports.service.ReportsService;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.settings.sessions.SessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.friendly.commons.CommonRegistry.IOT_AUTH_HEADER;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.REPORT_IS_EMPTY;

/**
 * Controller that exposes an API to interact with Reports
 * <p>
 * This controller is primarily a wrapper around the Reports
 * </p>
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@RestController
@Api(value = "Operations with report")
@RequestMapping("iotw/Report")
public class ReportController extends BaseController {

    @NonNull
    private final ReportsService reportsService;
    @NonNull
    private final GroupUpdateService groupUpdateService;
    @NonNull
    private final SessionService sessionService;

    public ReportController(@NonNull AlertProvider alertProvider,
                            @NonNull ReportsService reportsService,
                            @NonNull GroupUpdateService groupUpdateService,
                            @NonNull SessionService sessionService) {
        super(alertProvider);
        this.reportsService = reportsService;
        this.groupUpdateService = groupUpdateService;
        this.sessionService = sessionService;
    }

    @ApiOperation(value = "Generate report to file")
    @PutMapping(value = "generateReport")
    public String generateReportFile(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                      @RequestBody final ReportFileBody body) {

        return reportsService.generateReport(token, body);
    }

    @ApiOperation(value = "Fetches reports status by file names")
    @PostMapping("/reportsStatus")
    public List<ReportResponse> getReportStatuses(@RequestBody List<String> fileNames) {
        return reportsService.getReportStatuses(fileNames);
    }

    @ApiOperation(value = "Get device registration report")
    @PostMapping("/deviceRegistration")
    public FTPage<DeviceRegistrationReport> getDeviceRegistrationReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                      @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                        @RequestBody final DeviceRegistrationReportBody body) {

        return reportsService.getDeviceRegistrationReport(token, body);
    }

    @ApiOperation(value = "Get device update report")
    @PostMapping("/userActivity/deviceUpdate")
    public FTPage<DeviceReport> getDeviceUpdateReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                      @RequestBody final DeviceUpdateReportBody body) {

        return reportsService.getDeviceUpdateReport(token, body);
    }

    @ApiOperation(value = "Get user activity report")
    @PostMapping("/userActivity/system")
    public FTPage<UserActivityReport> getDeviceUpdateReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                          @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                            @RequestBody final UserActivityReportBody body) {

        return reportsService.getUserActivityReport(token, body);
    }

    @ApiOperation(value = "Get statistic of operations report")
    @PostMapping(value = "/operations")
    public StatisticOperationsReport getStatisticOperations(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                            @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                            @RequestBody final StatisticOperationsBody body) {

        return reportsService.getStatisticOperationsReport(token, body);
    }

    @ApiOperation(value = "Get active sessions report")
    @PostMapping("/activeSessions")
    public FTPage<UserSession> getActiveSessions(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                               @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                 @RequestBody final ActiveSessionsBody body) {

        return sessionService.getActiveSessions(token, body);
    }

    @ApiOperation(value = "Get session statistic report")
    @PostMapping("/sessionStatistic")
    public FTPage<UserSession> getSessionStatistic(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                 @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                   @RequestBody final SessionStatisticsBody body) {

        return sessionService.getSessionStatistic(token, body);
    }

    @ApiOperation(value = "Get generated reports")
    @PostMapping("/files")
    public FTPage<FileReport> getGeneratedReports(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final GeneratedReportsBody body) {

        return reportsService.getReportFiles(token, body);
    }

    @ApiOperation(value = "Get generated reports dates")
    @PostMapping("/files/dates")
    public FileDatesList getGeneratedReportsDates(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return reportsService.getReportFilesDates(token);
    }


    @ApiOperation(value = "Get generated report")
    @PostMapping(value = "/file", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<Resource> getGeneratedReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                       @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                       @RequestBody final LinkRequest request) {

        final File file = reportsService.getReportFile(token, request.getLink());
        return getResource(file);
    }

    @ApiOperation(value = "Delete reports files")
    @DeleteMapping(value = "/file")
    public void deleteReports(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                              @RequestHeader(IOT_AUTH_HEADER) final String token,
                              @RequestBody final LinksRequest request) {

        reportsService.deleteReportFiles(token, request.getLinks());
    }

    @ApiOperation(value = "Get user activity types")
    @PostMapping(value = "/userActivity/userActivityTypes")
    public UserActivityTypeDescriptionsResponse getUserActivityTypes(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                  @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return reportsService.getUserActivityTypes(token);
    }

    @ApiOperation(value = "Get device update activity types")
    @PostMapping(value = "/userActivity/deviceUpdateTypes")
    public DeviceActivityTypeDescriptionsResponse geDeviceUpdateTypes(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                   @RequestHeader(IOT_AUTH_HEADER) final String token) {

        return reportsService.getDeviceUpdateActivityTypes(token);
    }

    @ApiOperation(value = "Get device distribution report")
    @PostMapping("/deviceDistribution")
    public DeviceDistributionReport getDeviceDistributionReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                @RequestBody final DeviceDistributionBody body) {

        return reportsService.getDeviceDistributionReport(token, body);
    }

    @ApiOperation(value = "Get online device report")
    @PostMapping("/deviceOnline")
    public DeviceOnlineReport getDeviceOnlineReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                    @RequestBody final DeviceOnlineBody body) {

        return reportsService.getDeviceOnlineReport(token, body);
    }

    @ApiOperation(value = "Get offline device report")
    @PostMapping("/deviceOffline")
    public DeviceOfflineReport getDeviceOnlineReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                     @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                     @RequestBody final DeviceOfflineBody body) {

        return reportsService.getDeviceOfflineReport(token, body);
    }

    @ApiOperation(value = "Get device events report")
    @PostMapping("/deviceEvent")
    public DeviceEventReport getDeviceEventReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                  @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                  @RequestBody final DeviceEventBody body) {

        return reportsService.getDeviceEventReport(token, body);
    }

    @ApiOperation(value = "Get profile download report")
    @PostMapping("/profileDownload")
    public FTPage<ProfileDownloadReport> getProfileDownloadReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                  @RequestBody final ProfileDownloadBody body) {

        return reportsService.getProfileDownloadReport(token, body);
    }

    @ApiOperation(value = "Get firmware version report")
    @PostMapping("/firmwareVersion")
    public FTPage<FirmwareVersionReport> getFirmwareVersionReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                  @RequestBody final FirmwareVersionBody body) {

        return reportsService.getFirmwareVersionReport(token, body);
    }

    @ApiOperation(value = "Get group update report")
    @PostMapping("/groupUpdate")
    public FTPage<GroupUpdateReport> getGroupUpdateReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                        @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                          @RequestBody final GroupUpdateBody body) {

        return groupUpdateService.getGroupUpdateReport(token, body);
    }

    @ApiOperation(value = "Get group update device report")
    @PostMapping("/groupUpdate/device")
    public FTPage<GroupUpdateDeviceReport> getGroupUpdateDeviceReport(@ApiParam(value = IOT_AUTH_HEADER, required = true, hidden = true)
                                                                    @RequestHeader(IOT_AUTH_HEADER) final String token,
                                                                      @RequestBody final GroupUpdateDeviceBody body) {

        return groupUpdateService.getGroupUpdateDeviceReport(token, body);
    }

    private ResponseEntity<Resource> getResource(final File file) {
        try {
            final Resource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }
    }
}