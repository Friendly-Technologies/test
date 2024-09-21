package com.friendly.services.reports.utils.strategy.excel;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.management.profiles.orm.acs.repository.ProfileFileRepository;
import com.friendly.services.reports.service.ReportFileService;
import com.friendly.services.reports.dto.ReportStatus;
import com.friendly.services.reports.utils.ReportUtils;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.reports.ReportType.PROFILE_DOWNLOAD;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FIELD_CAN_NOT_BE_EMPTY;
import static com.friendly.services.infrastructure.utils.CommonUtils.isSuperDomain;

@Component
@RequiredArgsConstructor
public class ExcelProfileDownloadReportGeneratorStrategy implements ExcelReportGeneratorStrategy {

    @NonNull
    private final DomainService domainService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final ProfileFileRepository profileFileRepository;
    @NonNull
    private final ReportUtils reportUtils;
    @NonNull
    private final WsSender wsSender;

    @Async
    @Override
    public void generateReport(Session session, Map<String, Object> params, String fileName) {
        final ClientType clientType = session.getClientType();
        final Integer id = (Integer) params.get("id");
        if (id == null) {
            throw new FriendlyIllegalArgumentException(FIELD_CAN_NOT_BE_EMPTY, "profileId");
        }

        final Integer domainId = (Integer) params.get("domainId");
        final String manufacturer = (String) params.get("manufacturer");
        final String model = (String) params.get("model");

        final List<Integer> domainIds = isSuperDomain(domainId)
                ? null
                : domainService.getChildDomainIds(domainId);

        final List<Object[]> report = profileFileRepository.findAllForExcel(id.longValue(), domainIds,
                domainIds == null, manufacturer, model);

        final Integer userDomainId = reportUtils.getUserDomainId(domainId, session.getUserId());
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final String path = ReportFileService.createExcelTable(userDomainId, report, PROFILE_DOWNLOAD, true,
                clientType, session.getZoneId(), user.getDateFormat(), user.getTimeFormat(), null, null, fileName);
        ReportUtils.addReportStatus(fileName, ReportStatus.COMPLETED);
        wsSender.sendCompleteFileEvent(session.getClientType(), path);
    }

    @Override
    public ReportType getReportType() {
        return PROFILE_DOWNLOAD;
    }
}
